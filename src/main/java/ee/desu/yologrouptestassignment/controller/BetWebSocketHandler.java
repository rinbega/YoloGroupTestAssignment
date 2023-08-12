package ee.desu.yologrouptestassignment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.desu.yologrouptestassignment.dto.ApiErrorResponse;
import ee.desu.yologrouptestassignment.dto.Bet;
import ee.desu.yologrouptestassignment.dto.BetResult;
import ee.desu.yologrouptestassignment.service.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.List;
import java.util.Set;
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
                handleValidationErrors(session, message, violations);
            } else {
                handleSuccessfulRequest(session, bet);
            }
        } catch (JsonProcessingException e) {
            handleJsonProcessingException(session, e);
        }
    }

    private void handleSuccessfulRequest(WebSocketSession session, Bet bet) {
        var result = betService.placeBet(bet);
        sendBetResult(session, result);
    }

    private void handleJsonProcessingException(WebSocketSession session, JsonProcessingException ex) {
        logger.info("Could not process JSON: {}", ex.getMessage());
        sendErrorResponse(session, HttpStatus.BAD_REQUEST, ApiErrorResponse.MALFORMED_JSON_MESSAGE, List.of());
    }

    private void handleValidationErrors(WebSocketSession session,
                                        TextMessage message,
                                        Set<ConstraintViolation<Bet>> violations) {
        logger.info("Validation failed for {}", message.getPayload());
        var errors = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
        sendErrorResponse(session, HttpStatus.BAD_REQUEST, ApiErrorResponse.VALIDATION_FAILED_MESSAGE, errors);
    }

    private void sendErrorResponse(WebSocketSession session, HttpStatus status, String message, List<String> errors) {
        var errorResponse = new ApiErrorResponse(status, message, errors);
        try {
            var jsonErrorResponse = mapper.writeValueAsString(errorResponse);
            session.sendMessage(new TextMessage(jsonErrorResponse));
        } catch (IOException e) {
            logger.error("I/O error occurred while sending a WebSocket message: {}", e.getMessage());
        }
    }

    private void sendBetResult(WebSocketSession session, BetResult result) {
        try {
            var json = mapper.writeValueAsString(result);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.error("I/O error occurred while sending a WebSocket message: {}", e.getMessage());
        }
    }
}
