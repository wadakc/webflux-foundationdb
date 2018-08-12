package com.example.reactive.controller;


import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.time.Duration;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.apple.foundationdb.Database;
import com.apple.foundationdb.tuple.Tuple;
import com.example.reactive.controller.resource.MapResource;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.crypto.Data;

public class ReactiveController {

    private int counter = 0;

    private final Database database;

    public ReactiveController(Database database) {
        this.database = database;
    }

    public RouterFunction<ServerResponse> routes() {
        return route(GET("/hello"), this::hello).andRoute(GET("/stream"), this::stream);
    }

    public Mono<ServerResponse> hello(ServerRequest req) {
        return ok().body(Flux.just("Hello", "World!"), String.class);
    }

    public Mono<ServerResponse> stream(ServerRequest req) {
        Stream<Integer> stream = Stream.iterate(0, i -> i + 1);

        Flux<MapResource> flux = Flux.fromStream(stream).zipWith(Flux.interval(Duration.ofSeconds(1)))
                .map(i -> {
                    counter += 1;
                    String  area = null;
                    try {
                        CompletableFuture<byte[]> get = this.database
                                .readAsync(tx -> tx.get(Tuple.from("area").pack()));
                        area = Tuple.fromBytes(get.get()).getString(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return new MapResource(counter,area,"kc",Calendar.getInstance().getTime().toString());
                });
        return ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(flux,
                MapResource.class);
    }

}
