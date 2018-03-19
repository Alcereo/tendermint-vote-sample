package ru.alcereo.vote;

import com.github.jtendermint.crypto.ByteUtil;
import com.github.jtendermint.jabci.api.CodeType;
import com.github.jtendermint.jabci.api.ICheckTx;
import com.github.jtendermint.jabci.api.ICommit;
import com.github.jtendermint.jabci.api.IDeliverTx;
import com.github.jtendermint.jabci.socket.TSocket;
import com.github.jtendermint.jabci.types.*;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import ru.alcereo.vote.frontend.VoteFrontend;
import ru.alcereo.vote.messages.VoteMessage;
import ru.alcereo.tendermint.websocket.Websocket;
import ru.alcereo.tendermint.websocket.WebsocketException;
import ru.alcereo.tendermint.websocket.WebsocketStatus;
import ru.alcereo.tendermint.websocket.jsonrpc.JSONRPC;
import ru.alcereo.tendermint.websocket.jsonrpc.Method;
import ru.alcereo.tendermint.websocket.jsonrpc.calls.StringParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class VoteABCICommunicationService implements
        ICheckTx,
        IDeliverTx,
        ICommit,
        VoteCommunicationService,
        WebsocketStatus {

    private Websocket wsClient;
    private TSocket socket;

    private Gson gson = new Gson();
//    private int hashCount = 0;

    private Map<String, VoteFrontend> frontends = new HashMap<>();

    private VotingData votingData = new VotingData();


    public VoteABCICommunicationService(String nodeIpAddress, int abciPort) throws URISyntaxException {

        wsClient = new Websocket(new URI("ws://"+nodeIpAddress+":46657/websocket"),this);
        socket = new TSocket();
        socket.registerListener(this);
        new Thread(() -> {
            log.info("Starting TMSP Socket:" + abciPort);
            socket.start(abciPort);
        }).start();

        // wait 5 seconds before connecting the websocket
        log.info("waiting 5 seconds before connecting to Websocket...");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(() -> reconnectWS(), 5, TimeUnit.SECONDS);
    }

    private void reconnectWS() {
        log.info("Trying to connect to Web socket");
        try {
            wsClient.connect();
            log.info("Web socket connected");
        } catch (WebsocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendVote(String userName, String option) {

        log.debug("Trying to send vote");
        log.trace("Username: {}, option: {}",userName, option);

        String jsonMessage = gson.toJson(
                VoteMessage.builder()
                        .userName(userName)
                        .optionName(option)
                        .build()
        );

        log.trace("Construct JSONRPC from json vote message: {}", jsonMessage);

        JSONRPC rpc;
        try {
            rpc = new StringParam(
                    Method.BROADCAST_TX_ASYNC,
                    jsonMessage.getBytes()
            );
        }catch (Exception e){
            log.error("Error construct JSONRPC", e);
            e.printStackTrace();
            throw new RuntimeException("Error construct JSONRPC", e);
        }


        if (!wsClient.isOpen()) {
            log.error("Web socket is not open!");

        }else {

            log.trace("Send message by web socket");
            wsClient.sendMessage(rpc, e -> {
                log.trace("Sending result: {}", e);
            });
        }
    }

    @Override
    public void registerFrontend(String name, VoteFrontend front) {
        frontends.put(name, front);
    }


    @Override
    public ResponseCheckTx requestCheckTx(RequestCheckTx req) {
        log.debug("Request check start");
        log.trace("Request check req: {}", req);

        byte[] byteArray = req.getTx().toByteArray();
        VoteMessage msg = gson.fromJson(new String(byteArray), VoteMessage.class);

        if (votingData.userAlreadyVoted(msg.getUserName())){
            log.warn("Already voted request response by {}", msg.getUserName());

            return ResponseCheckTx
                    .newBuilder()
                    .setLog("User: "+msg.getUserName()+" already voted.")
                    .setCode(CodeType.BAD)
                    .build();
        }else {
            log.debug("Request check success");
            return ResponseCheckTx.newBuilder().setCode(CodeType.OK).build();
        }

    }

    @Override
    public ResponseCommit requestCommit(RequestCommit requestCommit) {
        return ResponseCommit.newBuilder()
                .setCode(CodeType.OK)
                .setData(ByteString.copyFrom(ByteUtil.toBytes(gson.toJson(votingData).hashCode())))
                .build();
    }

    @Override
    public ResponseDeliverTx receivedDeliverTx(RequestDeliverTx req) {
        log.debug("Delivery received start");
        log.trace("Delivery request req: {}", req);

        byte[] byteArray = req.getTx().toByteArray();
        VoteMessage msg = gson.fromJson(new String(byteArray), VoteMessage.class);

        if (votingData.userAlreadyVoted(msg.getUserName())){

            log.warn("Delivery already voted by {}", msg.getUserName());

            return ResponseDeliverTx.newBuilder()
                    .setLog("User: "+msg.getUserName()+" already voted.")
                    .setCode(CodeType.BAD)
                    .build();
        }

        log.debug("Add vote");

        votingData.addVote(msg.getUserName(), msg.getOptionName());

        log.trace("votingData: {}", votingData);

        frontends.forEach((s, voteFrontend) -> {
            voteFrontend.setOptionVotes(
                    msg.getOptionName(),
                    votingData.getVotes().get(msg.getOptionName())
            );
        });

        log.debug("Delivery received success");

        return ResponseDeliverTx.newBuilder().setCode(CodeType.OK).build();
    }
}
