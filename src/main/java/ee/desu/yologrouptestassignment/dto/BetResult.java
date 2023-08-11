package ee.desu.yologrouptestassignment.dto;

import java.math.BigDecimal;

public class BetResult {

    private final BigDecimal win;

    public BetResult(BigDecimal win) {
        this.win = win;
    }

    public BetResult() {
        this.win = BigDecimal.ZERO;
    }

    public BigDecimal getWin() {
        return win;
    }
}
