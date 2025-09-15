package middleware.rateLimiter;

public interface RateLimiter {

    public boolean isAllowed(String clientId);

}
