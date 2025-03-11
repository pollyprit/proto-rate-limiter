import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, UnknownHostException, InvalidKeySpecException {
        RateLimiter tokenBucket = new TokenBucket("HomeAPI_RL", 5, 2, 1);
        testRateLimiter(tokenBucket, "TokenBucket");

        RateLimiter leakyBucket = new LeakyBucket("LeakyBucket_RL", 5);
        testRateLimiter(leakyBucket, "LeakyBucket");

        RateLimiter fixedWindow = new FixedWindow("FixedWindow_RL", 4, 1);
        testRateLimiter(fixedWindow, "FixedWindow");
    }

    public static void testRateLimiter(RateLimiter rateLimiter, String name) {
        rateLimiter.start();

        for (int i = 1; i <= 100; i++) {
            if (rateLimiter.isRequestAllowed(Integer.toString(i)))
                System.out.println(i + ": allowed");
            else
                System.out.println(i + ": BLOCKED");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("\n" + name + "Rate Limiter stats (succ/fail): " +
                rateLimiter.getSuccessCount() + "/" + rateLimiter.getFailureCount());
    }
}