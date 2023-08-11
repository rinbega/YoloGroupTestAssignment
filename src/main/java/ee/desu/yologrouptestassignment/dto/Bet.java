package ee.desu.yologrouptestassignment.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Bet {

    @NotNull
    @Range(min = 1, max = 100, message = "Guess should be an integer in range [1, 100]")
    private Integer guess;

    @NotNull
    @DecimalMin(value = "0.01", message = "Bet amount should be at least 0.01")
    private BigDecimal amount;

    public Bet(Integer guess, BigDecimal amount) {
        this.guess = guess;
        this.amount = amount;
    }

    public Bet() {
    }

    public Integer getGuess() {
        return guess;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}