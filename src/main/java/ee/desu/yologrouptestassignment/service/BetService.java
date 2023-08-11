package ee.desu.yologrouptestassignment.service;

import ee.desu.yologrouptestassignment.dto.Bet;
import ee.desu.yologrouptestassignment.dto.BetResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.random.RandomGenerator;

@Service
public class BetService {

    static final int LOWER_BOUND_INCLUSIVE = 1;
    static final int UPPER_BOUND_EXCLUSIVE = 101;

    private final RandomGenerator rng;

    public BetService(RandomGenerator rng) {
        this.rng = rng;
    }


    public BetResult placeBet(Bet bet) {
        int targetNumber = generateTargetNumber();
        if (bet.guess() > targetNumber) {
            return new BetResult(calculateWin(bet));
        }
        return new BetResult(BigDecimal.ZERO);
    }

    private int generateTargetNumber() {
        return rng.nextInt(LOWER_BOUND_INCLUSIVE, UPPER_BOUND_EXCLUSIVE);
    }

    /*
     Provided formula win = bet * (99 / (100 - guess)) together win the win condition guess > targetNumber means that
     picking 99 results in winning 99 times your bet with a probability of 98% (and 100 must be a special case), while
     picking 2 results in winning 1.01 times your bet with a probability of 1% (number 1 is never going to win).
    */
    private BigDecimal calculateWin(Bet bet) {
        double winFactor;
        if (bet.guess() == 100) {
            winFactor = 99;
        } else {
            winFactor = (double) 99 / (100 - bet.guess());
        }
        return bet.amount().multiply(BigDecimal.valueOf(winFactor)).setScale(2, RoundingMode.HALF_UP);
    }
}
