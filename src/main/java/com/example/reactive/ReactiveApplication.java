package com.example.reactive;

import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.example.reactive.controller.DatabaseController;
import com.example.reactive.controller.ReactiveController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.ipc.netty.http.server.HttpServer;

import java.util.Optional;

@SpringBootApplication
public class ReactiveApplication {

	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		int port = Optional.ofNullable(System.getenv("PORT")) //
				.map(Integer::parseInt) //
				.orElse(8080);
		HttpServer httpServer = HttpServer.create("0.0.0.0", port);
		FDB fdb = FDB.selectAPIVersion(510);
		try (Database database = fdb.open()) {
			Logger logger = LoggerFactory.getLogger(ReactiveApplication.class);
			httpServer.startRouterAndAwait(routes -> {
				HttpHandler httpHandler = RouterFunctions.toHttpHandler(
						ReactiveApplication.routes(database), HandlerStrategies.builder().build());
				routes.route(x -> true, new ReactorHttpHandlerAdapter(httpHandler));
			}, context -> {
				long elapsed = System.currentTimeMillis() - begin;
				logger.info("Started in {} seconds", elapsed / 1000.0);
			});
			logger.info("Closing...");
		}
		SpringApplication.run(ReactiveApplication.class, args);
	}


	static RouterFunction<ServerResponse> routes(Database database) {
		return new ReactiveController(database).routes().and(new DatabaseController(database).routes());
	}
}
