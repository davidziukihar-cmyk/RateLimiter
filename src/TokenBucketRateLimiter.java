import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimiter {
    private final int capacity;
    private final int refillRate;
    private AtomicLong tokens;
    private long lastRefillTimestamp;

    public TokenBucketRateLimiter(int capacity, int refillRateInSec) {
        this.capacity = capacity;
        this.refillRate = refillRateInSec;
        tokens = new AtomicLong(capacity);
        lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refillTokens();

        if (tokens.get() > 0) {
            System.out.println("allow, tokens before: " + tokens.get());
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private synchronized void refillTokens() {
        long now = System.currentTimeMillis();
        long spendTime = (System.currentTimeMillis() - lastRefillTimestamp);
        long tokensToAdd = spendTime * capacity / (refillRate * 1000L);

        if (tokensToAdd > 0) {
            System.out.println(tokens.get() + tokensToAdd);
            tokens.set(Math.min(capacity, tokens.get() + tokensToAdd));
            lastRefillTimestamp = now;
        }
    }
}
