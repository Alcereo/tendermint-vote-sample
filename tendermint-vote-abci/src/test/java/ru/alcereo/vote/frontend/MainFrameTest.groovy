package ru.alcereo.vote.frontend

import ru.alcereo.vote.VoteABCICommunicationService

import static org.mockito.Mockito.mock

class MainFrameTest extends GroovyTestCase {

    void testStartFront() {

        def votingService = mock(VoteABCICommunicationService)
        def front = new MainFrame("Alex", votingService)

        front.setOptionVotes "Second option" ,1

        front.setOptionVotes "Second option" ,2

        front.setOptionVotes "Second option" ,3

    }
}
