package ee.desu.yologrouptestassignment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BetController {

    private final BetService betService;

    public BetController(BetService betService) {
        this.betService = betService;
    }

    @PostMapping("/bet")
    BetResult placeBet(@RequestBody Bet bet) {
        return betService.placeBet(bet);
    }
}
