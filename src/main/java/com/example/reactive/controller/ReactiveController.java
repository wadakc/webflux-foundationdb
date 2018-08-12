package com.example.reactive.controller;


import com.sun.security.ntlm.Server;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RestController
public class ReactiveController {

    private  int counter = 0;

    public RouterFunction<ServerResponse> routes() {
        return route(GET("/test"),this::test);
    }

    Mono<ServerResponse> test(ServerRequest req) {
        return ok().body(Flux.just("Hello", "World!"), String.class);
    }


    @GetMapping("/stream")
    public Flux<Map<String, Integer>> stream() {
        Stream<Integer> stream = Stream.iterate(0, i -> i + 1); // Java8の無限Stream
        return Flux.fromStream(stream).zipWith(Flux.interval(Duration.ofSeconds(1)))
                .map(i -> {
                    counter += 1;
                    return Collections.singletonMap("value", counter);
                });
    }


}
