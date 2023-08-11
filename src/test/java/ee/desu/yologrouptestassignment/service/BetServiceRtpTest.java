package ee.desu.yologrouptestassignment.service;

import ee.desu.yologrouptestassignment.dto.Bet;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class BetServiceRtpTest {

    private final Logger logger = LoggerFactory.getLogger(BetServiceRtpTest.class);

    private static final int TOTAL_ROUNDS = 100_000_000;
    private static final int THREAD_COUNT = 24;
    private static final int ONE_ROUND_BET = 100;

    @Test
    public void shouldCalculateRTPForOneMillionGames() throws ExecutionException, InterruptedException {
        var betService = new BetService(new SplittableRandom());
        var threadPool = new ForkJoinPool(THREAD_COUNT);
        var totalSpent = BigDecimal.valueOf(TOTAL_ROUNDS).multiply(BigDecimal.valueOf(ONE_ROUND_BET));
        long startTime = System.currentTimeMillis();
        var totalWon = threadPool.submit(() ->
                IntStream.range(0, TOTAL_ROUNDS)
                        .parallel()
                        .mapToObj(i -> {
                            var guess = ThreadLocalRandom.current()
                                    .nextInt(BetService.LOWER_BOUND_INCLUSIVE, BetService.UPPER_BOUND_EXCLUSIVE);
                            var bet = new Bet(guess, BigDecimal.valueOf(ONE_ROUND_BET));
                            var result = betService.placeBet(bet);
                            return result.win();
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        ).get();
        threadPool.shutdown();
        long duration = System.currentTimeMillis() - startTime;
        var rtp = totalWon.divide(totalSpent, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        logger.info("{} rounds took {} ms, RTP: {}%", TOTAL_ROUNDS, duration, rtp);
    }
}
