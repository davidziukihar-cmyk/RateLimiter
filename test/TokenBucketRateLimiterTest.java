import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TokenBucketRateLimiterTest {

    @Test
    void allowRequest() throws InterruptedException {
        int capacity = 10;
        int refillRateInSec = 1;
        int testTime = 3010;
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(capacity, refillRateInSec);
        Random random = new Random();
        int allowedCount = 0;
        long start = System.currentTimeMillis();

        //try 1 sec send requests from 2 users:
        while (System.currentTimeMillis() - start <= testTime) {
            String userId = String.valueOf(random.nextInt(2));
            Thread.sleep(1);
            if (rateLimiter.allowRequest()) {
//                System.out.println(userId + " was allowed send request");
                allowedCount++;
            }
        }
        System.out.println(allowedCount);
        Assertions.assertEquals(capacity + capacity*testTime/refillRateInSec/1000, allowedCount);
    }

}