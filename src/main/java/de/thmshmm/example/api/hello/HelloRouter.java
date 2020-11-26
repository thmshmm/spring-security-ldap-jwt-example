package de.thmshmm.example.api.hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static de.thmshmm.example.config.ApiProperties.API_PREFIX;

@Configuration
public class HelloRouter {

    @Bean
    public RouterFunction<ServerResponse> helloRoutes(HelloHandler handler) {
        return RouterFunctions.route(
                RequestPredicates.GET(API_PREFIX + "/hello")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                handler::hello)
                .andRoute(
                        RequestPredicates.GET(API_PREFIX + "/hello-admin")
                                .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                        handler::helloAdmin
                );
    }
}
