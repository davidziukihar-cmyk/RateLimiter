import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SlidingWindowRateLimiter {
    private final int limit;
    private final long windowsSize;
    private final ConcurrentHashMap<Long, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int limit, long windowsSize) {
        this.limit = limit;
        this.windowsSize = windowsSize;
    }

    public boolean allowRequest() {
        long currentWindow = System.currentTimeMillis() / windowsSize;
        long previousWindow = System.currentTimeMillis() / windowsSize - 1;
        requestCounts.putIfAbsent(currentWindow, new AtomicInteger(0));
        requestCounts.putIfAbsent(previousWindow, new AtomicInteger(0));

        long previousCount = requestCounts.get(previousWindow).get();
        long currentCount = requestCounts.get(currentWindow).get();

        return (previousCount + currentCount) / 2 < limit && requestCounts.get(currentWindow).incrementAndGet() <= limit;

    }
}
