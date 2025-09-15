package main;

import controllers.StockController;
import middleware.rateLimiter.FixedWindowRateLimiter;
import middleware.rateLimiter.RateLimiter;
import reader.DailyStockPriceReader;
import reader.SectorMarketCap22Reader;
import redis.RedisClient;
import redis.RedisJsonCommands;
import redis.clients.jedis.Jedis;
import uploader.DailyStockPriceUploader;
import uploader.DataUploader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
//        // In a real-world application, you would use a dependency injection framework
//        // to manage these dependencies.
//        RedisClient redisClient = new RedisClient("localhost", 6379);
////        SectorMarketCap22Reader reader = new SectorMarketCap22Reader();
////        DataUploader uploader = new DataUploader(reader, redisClient);
////
////        uploader.upload();
////
////        redisClient.close();
////
////        System.out.println("Data upload complete!");
//
//        DailyStockPriceReader reader = new DailyStockPriceReader();
////        InputStream inputStream = new FileInputStream("/Users/iffathsalah/Developer/cse-data-cd-api/cse-data-cd-api/src/main/resources/2024 Data.xls");
////        List<Map<String, Object>> data = reader.readData(inputStream);
////        data.forEach(System.out::println);
//
//        DailyStockPriceUploader dailyStockPriceUploader = new DailyStockPriceUploader(reader, redisClient);
//        dailyStockPriceUploader.upload();
////        redisClient.close();
//        System.out.println("DailyStockPriceUploader has been closed");
//
//        try (Jedis jedis = redisClient.getResource()) {
//            byte[] key = "stock:AAF".getBytes(StandardCharsets.UTF_8);
//            byte[] path = ".[\"01-JUL-24\"][\"CLOSE PRICE (Rs.)\"]".getBytes(StandardCharsets.UTF_8);
//
//            Object rawResponse = jedis.sendCommand(RedisJsonCommands.JSON_GET, key, path);
//            if (rawResponse != null) {
//                byte[] response = (byte[]) rawResponse;
//                String json = new String(response, StandardCharsets.UTF_8);
//                System.out.println(json);
//            } else {
//                System.out.println("No data found");
//            }
//        }

        // Redis client
        RedisClient redisClient = new RedisClient("localhost", 6379);

        RateLimiter rateLimiter = new FixedWindowRateLimiter(redisClient.getResource(), 60, 10); //

        // Set port for Spark if needed
        port(4567);



        // Set up routes
        StockController stockController = new StockController(redisClient, rateLimiter);
        stockController.routes();

//        http://localhost:4567/stock?symbol=AAF&date=05-JAN-24&field=CLOSE%20PRICE%20(Rs.)
//        http://localhost:4567/stock?symbol=AAF&date=05-JAN-24

    }

}
