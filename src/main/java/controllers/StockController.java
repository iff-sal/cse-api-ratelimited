package controllers;

import middleware.rateLimiter.RateLimiter;
import org.apache.poi.ss.formula.functions.Rate;
import redis.RedisClient;
import redis.RedisJsonCommands;
import redis.clients.jedis.Jedis;
import spark.Request;
import spark.Response;

import java.nio.charset.StandardCharsets;

import static spark.Spark.get;

public class StockController {

    private final RedisClient redisClient;
    private final RateLimiter rateLimiter;

    public StockController(RedisClient redisClient, RateLimiter rateLimiter) {
        this.redisClient = redisClient;
        this.rateLimiter = rateLimiter;
    }

    public void routes() {
        get("/stock", this::getStockData);
    }

    private Object getStockData(Request req, Response res) {
        String symbol = req.queryParams("symbol");
        String date = req.queryParams("date");
        String field = req.queryParams("field");

        String clientId = req.ip(); // rate limit by ip address

        if (!rateLimiter.isAllowed(clientId)) {
            res.status(429); // too many request
            return "Rate limit exceeded, try again later";
        }

        if (symbol == null || date == null) {
            res.status(400);
            return "Missing required parameters: symbol and date";
        }

        try (Jedis jedis = redisClient.getResource()) {
            byte[] key = ("stock:" + symbol).getBytes(StandardCharsets.UTF_8);

            // If field is provided, fetch specific field from date
            String redisPath;
            if (field != null && !field.isBlank()) {
                redisPath = ".[\"" + date + "\"][\"" + field + "\"]";
            } else {
                redisPath = ".[\"" + date + "\"]";
            }

            byte[] path = redisPath.getBytes(StandardCharsets.UTF_8);
            Object rawResponse = jedis.sendCommand(RedisJsonCommands.JSON_GET, key, path);

            if (rawResponse == null) {
                res.status(404);
                return "Data not found for given parameters";
            }

            String json = new String((byte[]) rawResponse, StandardCharsets.UTF_8);
            res.type("application/json");
            return json;
        } catch (Exception e) {
            res.status(500);
            return "Server error: " + e.getMessage();
        }
    }
}
