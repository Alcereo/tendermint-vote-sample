package ru.alcereo.vote.messages;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VoteMessage {
    String userName;
    String optionName;
    public final long timestamp = System.currentTimeMillis();
}
