package redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {

    private final JedisPool pool; // hold running connection, so dont have to creat connection. just request.

    public RedisClient(String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);

        this.pool = new JedisPool(poolConfig, host, port);
    }

    /**
     * gets a Jedis resource from the pool.
     *
     * @return a Jedis resource to talk to redis instance.
     */
    public redis.clients.jedis.Jedis getResource() {
        return pool.getResource();
    }

    /**
     * closes the connection pool.
     */
    public void close() {
        if (pool != null) {
            pool.close();
        }
    }
}
