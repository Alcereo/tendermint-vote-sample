package ru.alcereo.vote.messages

import ru.alcereo.vote.VotingData

class VotingDataTest extends GroovyTestCase {

    void testVoting() {

        def voteBasket = new VotingData()

        voteBasket.addVote("user1", "opt1")
        voteBasket.addVote("user2", "opt2")
        voteBasket.addVote("user3", "opt1")

        assertEquals(
                2,
                voteBasket.getVotes().get("opt1")
        )

        assertEquals(
                1,
                voteBasket.getVotes().get("opt2")
        )
    }

    void testAlreadyVoted() {

        def voteBasket = new VotingData()

        voteBasket.addVote("user1", "opt1")
        voteBasket.addVote("user2", "opt2")
        voteBasket.addVote("user3", "opt1")

        assertEquals(
                true,
                voteBasket.userAlreadyVoted("user1")
        )

        assertEquals(
                true,
                voteBasket.userAlreadyVoted("user2")
        )

        assertEquals(
                true,
                voteBasket.userAlreadyVoted("user3")
        )

        assertEquals(
                false,
                voteBasket.userAlreadyVoted("user4")
        )

    }
}
