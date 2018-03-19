package ru.alcereo.vote;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
public class VotingData {

    private Map<String,String> userVotes = new HashMap<>();

    public void addVote(String userName, String optionName){
        userVotes.put(userName, optionName);
    }

    public Map<String, Integer> getVotes(){

        Map<String, Integer> result = new HashMap<>();

        userVotes.forEach((key, value) -> result.put(
                value,
                result.getOrDefault(value, 0)+1
        ));

        return result;
    }

    public boolean userAlreadyVoted(String UserName){
        return userVotes.keySet().contains(UserName);
    }

}
