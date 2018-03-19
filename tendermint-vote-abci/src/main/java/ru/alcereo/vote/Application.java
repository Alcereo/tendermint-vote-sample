package ru.alcereo.vote;

import lombok.extern.slf4j.Slf4j;
import ru.alcereo.vote.frontend.MainFrame;

import java.net.URISyntaxException;
import java.util.Objects;

@Slf4j
public class Application {

    public static void main(String[] args) throws URISyntaxException {

        String nodeIpAddress = getEnv("NODE_IP");
        int abciPort = Integer.valueOf(getEnv("ABCI_PORT"));

        log.debug("NODE_IP: {}", nodeIpAddress);
        log.debug("ABCI_PORT: {}", abciPort);

        VoteABCICommunicationService communicationService = new VoteABCICommunicationService(nodeIpAddress, abciPort);

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                new MainFrame(arg, communicationService);
            }
        } else {
            new MainFrame("mr. First", communicationService);
            new MainFrame("mr. Second", communicationService);
            new MainFrame("mr. Third", communicationService);
        }

    }

    private static String getEnv(String name) {
        return Objects.requireNonNull(System.getenv(name), "Environment prop "+name+" not specified");
    }
}
