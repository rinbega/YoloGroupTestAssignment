package ee.desu.yologrouptestassignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.desu.yologrouptestassignment.dto.Bet;
import ee.desu.yologrouptestassignment.service.BetService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class BetWebSocketHandler extends TextWebSocketHandler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final BetService betService;

    public BetWebSocketHandler(BetService betService) {
        this.betService = betService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        var bet = mapper.readValue(message.getPayload(), Bet.class);
        var result = betService.placeBet(bet);
        var json = mapper.writeValueAsString(result);
        session.sendMessage(new TextMessage(json));
    }
}
