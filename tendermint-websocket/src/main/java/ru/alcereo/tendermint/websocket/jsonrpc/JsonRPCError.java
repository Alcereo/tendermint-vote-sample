package ru.alcereo.tendermint.websocket.jsonrpc;

import lombok.ToString;

@ToString
public class JsonRPCError {

    public Integer code;
    public String message;
    public String data;

}
