package ee.desu.yologrouptestassignment.config;

import ee.desu.yologrouptestassignment.controller.BetWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final BetWebSocketHandler betWebSocketHandler;

    public WebSocketConfig(BetWebSocketHandler betWebSocketHandler) {
        this.betWebSocketHandler = betWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(betWebSocketHandler, "/ws/bet");
    }
}