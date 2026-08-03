import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LeakyBucketRateLimiterTest {

    @Test
    public void testCapacityLimit() {
        int capacity = 5;
        int leakRatePerSec = 0; // Set to 0 to prevent leakage during the instant test
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(capacity, leakRatePerSec);

        // Fill the bucket to its capacity
        for (int i = 0; i < capacity; i++) {
            Assertions.assertTrue(rateLimiter.allowRequest(), "Request " + i + " should be allowed");
        }

        // The bucket is now full, next request should be blocked
        Assertions.assertFalse(rateLimiter.allowRequest(), "Request exceeding capacity should be blocked");
    }

    @Test
    public void testLeakingOverTime() throws InterruptedException {
        int capacity = 3;
        int leakRatePerSec = 5; // leaks 5 requests per second (1 request every 200 ms)
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(capacity, leakRatePerSec);

        // Fill bucket
        for (int i = 0; i < capacity; i++) {
            Assertions.assertTrue(rateLimiter.allowRequest());
        }
        Assertions.assertFalse(rateLimiter.allowRequest());

        // Wait 450 ms, which should leak about 2.25 requests (so at least 2 requests can be made)
        Thread.sleep(450);

        Assertions.assertTrue(rateLimiter.allowRequest(), "Should allow request after leaking");
        Assertions.assertTrue(rateLimiter.allowRequest(), "Should allow second request after leaking");
    }

    @Test
    public void testConstantFlow() throws InterruptedException {
        int capacity = 10;
        int leakRatePerSec = 20; // 20 requests per second
        int durationMs = 1000;
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(capacity, leakRatePerSec);

        long start = System.currentTimeMillis();
        int allowedCount = 0;

        while (System.currentTimeMillis() - start < durationMs) {
            if (rateLimiter.allowRequest()) {
                allowedCount++;
            }
            Thread.sleep(5);
        }

        System.out.println("Allowed requests: " + allowedCount);
        // We expect: capacity (initial burst) + leakRatePerSec * durationMs/1000 requests
        // Let's assert a range because of timing/thread scheduling variance
        int expectedMin = capacity + (leakRatePerSec * (durationMs - 200) / 1000);
        int expectedMax = capacity + (leakRatePerSec * (durationMs + 200) / 1000) + 5;
        Assertions.assertTrue(allowedCount >= expectedMin && allowedCount <= expectedMax,
                "Allowed count " + allowedCount + " is not in expected range [" + expectedMin + ", " + expectedMax + "]");
    }
}
