package ee.desu.yologrouptestassignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.desu.yologrouptestassignment.dto.ApiErrorResponse;
import ee.desu.yologrouptestassignment.dto.BetResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BetWebSocketHandlerTest {

    @LocalServerPort
    private int port;

    private final WebSocketClient client = new StandardWebSocketClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldSendWonAmountWhenRequestJsonIsCorrect() throws Exception {
        String response = sendRequest("{\"guess\": 50, \"amount\": 100}");
        var result = mapper.readValue(response, BetResult.class);

        assertThat(result, instanceOf(BetResult.class));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonIsEmpty() throws Exception {
        String response = sendRequest("{}");
        var result = mapper.readValue(response, ApiErrorResponse.class);

        assertThat(result, instanceOf(ApiErrorResponse.class));
        assertThat(result.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getMessage(), is(ApiErrorResponse.VALIDATION_FAILED_MESSAGE));
        assertThat(result.getErrors(), hasSize(2));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonGuessIsLessThan1() throws Exception {
        String response = sendRequest("{\"guess\": 0, \"amount\": 100}");
        var result = mapper.readValue(response, ApiErrorResponse.class);

        assertThat(result, instanceOf(ApiErrorResponse.class));
        assertThat(result.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getMessage(), is(ApiErrorResponse.VALIDATION_FAILED_MESSAGE));
        assertThat(result.getErrors(), hasSize(1));
        assertThat(result.getErrors().get(0), is("guess: Guess should be an integer in range [1, 100]"));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonGuessIsGreaterThan100() throws Exception {
        String response = sendRequest("{\"guess\": 101, \"amount\": 100}");
        var result = mapper.readValue(response, ApiErrorResponse.class);

        assertThat(result, instanceOf(ApiErrorResponse.class));
        assertThat(result.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getMessage(), is(ApiErrorResponse.VALIDATION_FAILED_MESSAGE));
        assertThat(result.getErrors(), hasSize(1));
        assertThat(result.getErrors().get(0), is("guess: Guess should be an integer in range [1, 100]"));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonBetAmountIsLessThan1Cent() throws Exception {
        String response = sendRequest("{\"guess\": 50, \"amount\": 0.00}");
        var result = mapper.readValue(response, ApiErrorResponse.class);

        assertThat(result, instanceOf(ApiErrorResponse.class));
        assertThat(result.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getMessage(), is(ApiErrorResponse.VALIDATION_FAILED_MESSAGE));
        assertThat(result.getErrors(), hasSize(1));
        assertThat(result.getErrors().get(0), is("amount: Bet amount should be at least 0.01"));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonIsMalformed() throws Exception {
        String response = sendRequest("{\"guess\": \"string\", \"amount\": 0.05}");
        var result = mapper.readValue(response, ApiErrorResponse.class);

        assertThat(result, instanceOf(ApiErrorResponse.class));
        assertThat(result.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getMessage(), is(ApiErrorResponse.MALFORMED_JSON_MESSAGE));
    }

    private String sendRequest(String json) throws InterruptedException, ExecutionException, IOException, TimeoutException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        var future = client.doHandshake(new TestWebSocketHandler(completableFuture), "ws://localhost:" + port + "/ws/bet");
        var session = future.get();
        session.sendMessage(new TextMessage(json));
        String response = completableFuture.get(10, TimeUnit.SECONDS);
        session.close();
        return response;
    }

    private static class TestWebSocketHandler extends TextWebSocketHandler {

        private final CompletableFuture<String> completableFuture;

        public TestWebSocketHandler(CompletableFuture<String> completableFuture) {
            this.completableFuture = completableFuture;
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            completableFuture.complete(message.getPayload());
        }
    }
}
