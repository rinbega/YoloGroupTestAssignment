package ee.desu.yologrouptestassignment.controller;

import ee.desu.yologrouptestassignment.dto.Bet;
import ee.desu.yologrouptestassignment.dto.BetResult;
import ee.desu.yologrouptestassignment.service.BetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class BetRestController {

    private final BetService betService;

    public BetRestController(BetService betService) {
        this.betService = betService;
    }

    @PostMapping("/bet")
    public BetResult placeBet(@RequestBody @Valid Bet bet) {
        return betService.placeBet(bet);
    }
}
