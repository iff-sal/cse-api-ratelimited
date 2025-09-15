package uploader;

import reader.ExcelReader;
import redis.RedisClient;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class DataUploader {

    private final ExcelReader reader;
    private final RedisClient redisClient;

    public DataUploader(ExcelReader reader, RedisClient redisClient) {
        this.reader = reader;
        this.redisClient = redisClient;
    }

    /**
     * reads data from the Excel file and uploads it to Redis.
     */
    public void upload() {
        List<Map<String, Object>> data = reader.readData(getClass().getClassLoader().getResourceAsStream("22Sector Market Capitalisation.xls"));

        try (Jedis jedis = redisClient.getResource()) {
            for (Map<String, Object> rowData : data) {
                // Create a unique key for each row
                String sheetName = (String) rowData.get("sheetName");
                String sector = (String) rowData.get("SECTOR");
                String key = sheetName + ":" + sector;

                // Store the relevant data in Redis as a hash
                for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                    String field = entry.getKey();
                    // Exclude fields that are part of the key
                    if (!field.equals("sheetName") && !field.equals("SECTOR") && entry.getValue() != null) {
                        jedis.hset(key, field, entry.getValue().toString());
                    }
                }
            }
        }
    }
}
