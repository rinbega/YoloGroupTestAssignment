package ee.desu.yologrouptestassignment.controller;

import ee.desu.yologrouptestassignment.dto.ApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldSendWonAmountWhenRequestJsonIsCorrect() throws Exception {
        String correctRequestJson = "{\"guess\": 50, \"amount\": 100}";

        mockMvc.perform(post("/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.win").isNumber());
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonIsEmpty() throws Exception {
        String invalidRequestJson = "{}";

        mockMvc.perform(post("/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(ApiErrorResponse.VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonGuessIsLessThan1() throws Exception {
        String invalidRequestJson = "{\"guess\": 0, \"amount\": 100}";

        mockMvc.perform(post("/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(ApiErrorResponse.VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$.errors[0]")
                        .value("guess: Guess should be an integer in range [1, 100]"));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonGuessIsGreaterThan100() throws Exception {
        String invalidRequestJson = "{\"guess\": 101, \"amount\": 100}";

        mockMvc.perform(post("/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(ApiErrorResponse.VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$.errors[0]")
                        .value("guess: Guess should be an integer in range [1, 100]"));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonBetAmountIsLessThan1Cent() throws Exception {
        String invalidRequestJson = "{\"guess\": 50, \"amount\": 0.00}";

        mockMvc.perform(post("/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(ApiErrorResponse.VALIDATION_FAILED_MESSAGE))
                .andExpect(jsonPath("$.errors[0]")
                        .value("amount: Bet amount should be at least 0.01"));
    }

    @Test
    public void shouldSendErrorMessageWhenRequestJsonIsMalformed() throws Exception {
        String invalidRequestJson = "{\"guess\": \"string\", \"amount\": 0.05}";

        mockMvc.perform(post("/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(ApiErrorResponse.MALFORMED_JSON_MESSAGE));
    }
}
