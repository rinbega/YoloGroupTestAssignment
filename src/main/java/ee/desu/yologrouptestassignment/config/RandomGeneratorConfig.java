package ee.desu.yologrouptestassignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@Configuration
public class RandomGeneratorConfig {

    @Bean
    public RandomGenerator randomGenerator() {
        return RandomGeneratorFactory.of("SplittableRandom").create();
    }
}
