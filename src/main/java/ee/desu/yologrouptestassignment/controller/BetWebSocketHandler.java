package ee.desu.yologrouptestassignment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.desu.yologrouptestassignment.dto.ApiErrorResponse;
import ee.desu.yologrouptestassignment.dto.Bet;
import ee.desu.yologrouptestassignment.service.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class BetWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(BetWebSocketHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final LocalValidatorFactoryBean validator;
    private final BetService betService;

    public BetWebSocketHandler(LocalValidatorFactoryBean validator, BetService betService) {
        this.validator = validator;
        this.betService = betService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            var bet = mapper.readValue(message.getPayload(), Bet.class);
            var violations = validator.validate(bet);
            if (!violations.isEmpty()) {
                var errors = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.toList());
                var errorResponse = new ApiErrorResponse(HttpStatus.BAD_REQUEST,
                        ApiErrorResponse.VALIDATION_FAILED_MESSAGE, errors);
                var jsonErrorResponse = mapper.writeValueAsString(errorResponse);
                session.sendMessage(new TextMessage(jsonErrorResponse));
            } else {
                var result = betService.placeBet(bet);
                var json = mapper.writeValueAsString(result);
                session.sendMessage(new TextMessage(json));
            }
        } catch (JsonProcessingException e) {
            logger.error("Could not process JSON", e);
        } catch (IOException e) {
            logger.error("Could not send a WebSocket message", e);
        }
    }
}
