package com.fugui.vertx.async.future;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;

import java.util.ArrayList;
import java.util.List;

public class Main extends AbstractVerticle {

    public static void main(String[] args) {
        VertxOptions options = new VertxOptions().setBlockedThreadCheckInterval(1000 * 60 * 10);
        Vertx vertx = Vertx.vertx(options);

        vertx.deployVerticle(new Main());
    }

    private CompositeFuture future() {

        List<Future<?>> futures = new ArrayList<>();

        Future<Object> f1 = Future.future();
        vertx.executeBlocking(f -> {
            try {
                Thread.sleep( 1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("EXEc1 ");
            f.complete();
        },  f1.completer()  );

        Future<Object> f2 = Future.future();
        vertx.executeBlocking(f -> {
            try {
                Thread.sleep( 1000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("EXEc2 ");
            f.complete();
        }, f2.completer());

        return CompositeFuture.all(f1, f2);

    }


    public void start() throws Exception {
        HttpServerOptions serverOptions = new HttpServerOptions();
        HttpServer server = vertx.createHttpServer(serverOptions);
        server.requestHandler(r -> future().setHandler(cf -> {
            System.out.println("Now it my trun.");
            r.response().end("Hello Vertx.");
        }))
                .listen(8080);
    }

}
