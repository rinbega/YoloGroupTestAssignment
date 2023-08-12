package ee.desu.yologrouptestassignment.service;

import ee.desu.yologrouptestassignment.dto.Bet;
import ee.desu.yologrouptestassignment.dto.BetResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BetServiceTest {

    private BetService betService;
    private Random rng;

    @BeforeEach
    public void setUp() {
        rng = mock(Random.class);
        betService = new BetService(rng);
    }

    @Test
    public void shouldLoseWhenGuessIs1() {
        fakeNextRng(1);
        Bet bet = new Bet(1, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.getWin(), is(BigDecimal.ZERO));
    }

    @Test
    public void shouldLoseWhenGuessIsHigher() {
        fakeNextRng(50);
        Bet bet = new Bet(51, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.getWin(), is(BigDecimal.ZERO));
    }

    @Test
    public void shouldWinWhenGuessIsLower() {
        fakeNextRng(100);
        Bet bet = new Bet(40, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.getWin(), comparesEqualTo(BigDecimal.valueOf(165)));
    }

    @Test
    public void shouldWinMaximumWhenGuessIs99() {
        fakeNextRng(100);
        Bet bet = new Bet(99, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.getWin(), comparesEqualTo(BigDecimal.valueOf(9900)));
    }

    @Test
    public void shouldGetCorrectWinAccordingToExample() {
        fakeNextRng(100);
        Bet bet = new Bet(50, BigDecimal.valueOf(40.5));
        BetResult result = betService.placeBet(bet);

        assertThat(result.getWin(), comparesEqualTo(BigDecimal.valueOf(80.19)));
    }

    @Test
    public void shouldGetCorrectWinForMinimumBetAmount() {
        fakeNextRng(100);
        Bet bet = new Bet(50, BigDecimal.valueOf(0.01));
        BetResult result = betService.placeBet(bet);

        assertThat(result.getWin(), comparesEqualTo(BigDecimal.valueOf(0.02)));
    }

    @Test
    public void shouldGetSameAmountBackForLowestGuess() {
        fakeNextRng(100);
        Bet bet = new Bet(1, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.getWin(), comparesEqualTo(BigDecimal.valueOf(100)));
    }

    private void fakeNextRng(int target) {
        when(rng.nextInt(BetService.UPPER_BOUND_EXCLUSIVE - BetService.LOWER_BOUND_INCLUSIVE))
                .thenReturn(target - BetService.LOWER_BOUND_INCLUSIVE);
    }
}
