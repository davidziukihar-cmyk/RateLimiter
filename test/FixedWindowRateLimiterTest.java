import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FixedWindowRateLimiterTest {

    @Test
    void allowRequest() throws InterruptedException {
        FixedWindowRateLimiter rateLimiter = new FixedWindowRateLimiter();
        Random random = new Random();
        int allowedCount = 0;
        long start = System.currentTimeMillis();

        //try 1 sec send requests from 2 users:
        while (System.currentTimeMillis() - start < 1000) {
            String userId = String.valueOf(random.nextInt(2));
            Thread.sleep(1);
            if (rateLimiter.allowRequest(userId)) {
//                System.out.println(userId + " was allowed send request");
                allowedCount++;
            }
        }
        System.out.println(allowedCount);
    }
}