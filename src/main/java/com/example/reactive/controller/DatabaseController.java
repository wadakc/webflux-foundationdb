package com.example.reactive.controller;

import com.apple.foundationdb.Database;
import com.apple.foundationdb.tuple.Tuple;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class DatabaseController {

    private final  Database database;

    public DatabaseController(Database database) {
        this.database = database;
    }


    public RouterFunction<ServerResponse> routes() {
        return route(POST("/set"),this::set).
                andRoute(GET("/get"),this::get).
                andRoute(GET("/clear"),this::clear);
    }


    Mono<ServerResponse> get(ServerRequest req) {
        final String key = "area";
        CompletableFuture<byte[]> get = this.database
                .readAsync(tx -> tx.get(Tuple.from(key).pack()));
        return Mono.fromFuture(get).log("get")
                .map(result -> Tuple.fromBytes(result).getString(0)) //
                .map(v -> "area =>  " + v) //
                .flatMap(body -> ServerResponse.ok().syncBody(body)) //
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    Mono<ServerResponse> set(ServerRequest req) {
        final String key = "area";
        CompletableFuture<String> set = this.database.runAsync(tx -> {
            Mono<String> body = req.bodyToMono(String.class);
            return body.doOnNext(
                    value -> tx.set(Tuple.from(key).pack(), Tuple.from(value).pack()))
                    .log("set").toFuture();
        });
        return ServerResponse.ok().body(Mono.fromFuture(set), String.class);
    }

    Mono<ServerResponse> clear(ServerRequest req) {
        final String key = "area";
        CompletableFuture<Void> clear = this.database.runAsync(tx -> {
            tx.clear(Tuple.from(key).pack());
            return CompletableFuture.completedFuture(null);
        });
        return Mono.fromFuture(clear).log("clear")
                .then(ServerResponse.noContent().build());
    }
}

