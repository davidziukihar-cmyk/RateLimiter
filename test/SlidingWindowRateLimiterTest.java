import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

class SlidingWindowRateLimiterTest {

    @Test
    void allowRequest() throws InterruptedException {
        SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(5, 10000);
        Random random = new Random();
        int allowedCount = 0;
        long start = System.currentTimeMillis();

        //try 1 sec send requests from 2 users:
        while (System.currentTimeMillis() - start < 1000) {
            String userId = String.valueOf(random.nextInt(2));
            Thread.sleep(1);
            if (rateLimiter.allowRequest()) {
//                System.out.println(userId + " was allowed send request");
                allowedCount++;
            }
        }
        System.out.println(allowedCount);
        Assertions.assertEquals(5, allowedCount);
    }
}
