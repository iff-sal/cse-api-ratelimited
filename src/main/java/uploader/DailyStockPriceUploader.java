package uploader;


import com.google.gson.Gson;
import reader.DailyStockPriceReader;
import redis.RedisClient;
import redis.RedisJsonCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.json.Path2;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DailyStockPriceUploader {
    private final DailyStockPriceReader reader;
    private final RedisClient redisClient;
    private final Gson gson = new Gson();

    public DailyStockPriceUploader(DailyStockPriceReader reader, RedisClient redisClient) {
        this.reader = reader;
        this.redisClient = redisClient;
    }

    public void upload() {
        List<Map<String, Object>> data = reader.readData(getClass().getClassLoader().getResourceAsStream("2024 data.xls"));

        // Group data by company ID
        Map<String, List<Map<String, Object>>> groupedData = data.stream()
                .collect(Collectors.groupingBy(row -> (String) row.get("COMPANY ID")));

        try (Jedis jedis = redisClient.getResource()) {
            for (Map.Entry<String, List<Map<String, Object>>> entry : groupedData.entrySet()) {
                String companyId = entry.getKey();
                String redisKey = "stock:" + companyId;

                // Transform the list of daily records into a map of date -> record
                Map<String, Map<String, Object>> dailyData = entry.getValue().stream()
                        .collect(Collectors.toMap(
                                row -> (String) row.get("TRADING DATE"),
                                row -> {
                                    row.remove("COMPANY ID");
                                    row.remove("TRADING DATE");
                                    return row;
                                },
                                (existing, replacement) -> {
                                    //duplicate date for same company, may have to clean the data
                                    existing.putAll(replacement);
                                    return existing;
                                }
                        ));


                // seralize to json and upload to Redis as a JSON object
//                String json = gson.toJson(dailyData);
//                jedis.set(redisKey, json);
//                jedis.jsonSet(redisKey, Path2.ROOT_PATH, dailyData);

//                String redisKey = "stock:" + companyId;
                String jsonString = gson.toJson(dailyData);

//                jedis.sendCommand(redisKey, RedisJsonCommands.JSON_SET, redisKey, ".", jsonString);
                jedis.sendCommand(RedisJsonCommands.JSON_SET,
                        redisKey.getBytes(StandardCharsets.UTF_8),
                        ".".getBytes(StandardCharsets.UTF_8),
                        jsonString.getBytes(StandardCharsets.UTF_8));

            }
        }
    }
}
