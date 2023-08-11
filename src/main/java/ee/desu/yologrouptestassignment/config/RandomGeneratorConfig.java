package ee.desu.yologrouptestassignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Random;

@Configuration
public class RandomGeneratorConfig {

    @Bean
    public Random randomGenerator() {
        return new SecureRandom();
    }
}
