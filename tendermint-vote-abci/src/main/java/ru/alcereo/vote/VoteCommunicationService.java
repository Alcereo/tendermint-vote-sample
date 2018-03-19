package ru.alcereo.vote;

import ru.alcereo.vote.frontend.VoteFrontend;

public interface VoteCommunicationService {
    void sendVote(String userName, String option);

    void registerFrontend(String userName, VoteFrontend votingService);

}
