import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Use Cases
 *      API request limiting (e.g., max 100 requests per minute per user).
 *      Login attempt restrictions (e.g., 5 attempts per 10 minutes).
 */

public class FixedWindowRateLimiter {

    private ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
    private int requestLimit = 5;
    private long WINDOWS_FRAME = 500; //0,5 sec
    private long windowsStart = System.currentTimeMillis();

    synchronized boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - windowsStart > WINDOWS_FRAME) {
           windowsStart = currentTime;
           map.clear();
        }
        map.putIfAbsent(userId, new AtomicInteger(0));
        return map.get(userId).incrementAndGet() <= requestLimit;
    }
}
