package middleware.rateLimiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.args.ExpiryOption;

public class FixedWindowRateLimiter implements RateLimiter {

    private final Jedis jedis;
    private final int windowSize;
    private final int limit;

    public FixedWindowRateLimiter(Jedis jedis, int windowSize, int limit) {
        this.jedis = jedis;
        this.windowSize = windowSize;
        this.limit = limit;
    }

    @Override
    public boolean isAllowed(String clientId) {
        String key = "rate_limit:" + clientId;
        String ccounterStr = jedis.get(key);
        int ccounterInt = ccounterStr != null ? Integer.parseInt(ccounterStr) : 0;

        boolean isAllowed = ccounterInt < limit;

        if (isAllowed) {
            Transaction transaction = jedis.multi();
            transaction.incr(key);
            transaction.expire(key, windowSize, ExpiryOption.NX);
            transaction.exec();
        }

        return isAllowed;
    }
}
