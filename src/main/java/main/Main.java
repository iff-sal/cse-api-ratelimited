package main;

import controllers.StockController;
import middleware.rateLimiter.FixedWindowRateLimiter;
import middleware.rateLimiter.RateLimiter;
import middleware.rateLimiter.TockenBucketRateLimiter;
import redis.RedisClient;

import java.io.FileNotFoundException;

import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        // Redis client
        RedisClient redisClient = new RedisClient("localhost", 6379);

        //try different rate limiting algorithms, just comment and uncomment the line.
        // only allow 10 request in 60 seconds per ip address
        RateLimiter rateLimiter = new FixedWindowRateLimiter(redisClient.getResource(), 60, 10);
        // use this for burst limits.
//        RateLimiter rateLimiter = new TockenBucketRateLimiter(redisClient.getResource(), 10, 1);

        // set port for java spark
        port(4567);

        // Set up routes
        StockController stockController = new StockController(redisClient, rateLimiter);
        stockController.routes();

        //example end points
        //http://localhost:4567/stock?symbol=AAF&date=05-JAN-24&field=CLOSE%20PRICE%20(Rs.)
        //http://localhost:4567/stock?symbol=AAF&date=05-JAN-24


    }

}
