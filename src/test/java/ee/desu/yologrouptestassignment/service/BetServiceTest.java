package ee.desu.yologrouptestassignment.service;

import ee.desu.yologrouptestassignment.dto.Bet;
import ee.desu.yologrouptestassignment.dto.BetResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BetServiceTest {

    private BetService betService;
    private RandomGenerator rng;

    @BeforeEach
    public void setUp() {
        rng = mock(RandomGenerator.class);
        betService = new BetService(rng);
    }

    @Test
    public void shouldLoseWhenGuessIs1() {
        when(rng.nextInt(anyInt(), anyInt())).thenReturn(1);
        Bet bet = new Bet(1, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.win(), is(BigDecimal.ZERO));
    }

    @Test
    public void shouldLoseWhenGuessIsHigher() {
        when(rng.nextInt(anyInt(), anyInt())).thenReturn(50);
        Bet bet = new Bet(51, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.win(), is(BigDecimal.ZERO));
    }

    @Test
    public void shouldWinWhenGuessIsLower() {
        when(rng.nextInt(anyInt(), anyInt())).thenReturn(100);
        Bet bet = new Bet(40, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.win(), comparesEqualTo(BigDecimal.valueOf(165)));
    }

    @Test
    public void shouldWinMaximumWhenGuessIs99() {
        when(rng.nextInt(anyInt(), anyInt())).thenReturn(100);
        Bet bet = new Bet(99, BigDecimal.valueOf(100));
        BetResult result = betService.placeBet(bet);

        assertThat(result.win(), comparesEqualTo(BigDecimal.valueOf(9900)));
    }

    @Test
    public void shouldGetCorrectWinAccordingToExample() {
        when(rng.nextInt(anyInt(), anyInt())).thenReturn(100);
        Bet bet = new Bet(50, BigDecimal.valueOf(40.5));
        BetResult result = betService.placeBet(bet);

        assertThat(result.win(), comparesEqualTo(BigDecimal.valueOf(80.19)));
    }
}
