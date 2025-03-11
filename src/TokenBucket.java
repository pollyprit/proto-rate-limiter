import java.util.Timer;
import java.util.TimerTask;

public class TokenBucket implements RateLimiter {
    private int bucketSize;
    private long currentTokens;
    private int tokenRefill;
    private int tokenRefillRateSec;
    private String id;

    private long success;
    private long failures;

    public TokenBucket(String id, int bucketSize, int tokenRefill, int tokenRefillRateSec) {
        this.id = id;
        this.bucketSize = bucketSize;
        this.tokenRefill = tokenRefill;
        this.tokenRefillRateSec = tokenRefillRateSec;

        this.currentTokens = this.bucketSize;
    }

    @Override
    public void start() {
        Timer timer = new Timer("TokenBucket_" + id);
        timer.scheduleAtFixedRate(new TokenBucketFillerThread(this), this.tokenRefillRateSec,
                this.tokenRefillRateSec * 1000);
    }

    @Override
    public boolean isRequestAllowed(String id) {
        if (currentTokens > 0) {
            --currentTokens;
            ++success;
            return true;
        }
        ++failures;
        return false;
    }

    public void refillBucket() {
        if (currentTokens < bucketSize)
            currentTokens += tokenRefill;

        if (currentTokens > bucketSize)
            currentTokens = bucketSize;
        System.out.println("Bucket refilled: " + currentTokens);
    }

    public long getSuccessCount() {
        return success;
    }

    public long getFailureCount() {
        return failures;
    }

    static class TokenBucketFillerThread extends TimerTask {
        TokenBucket tokenBucket;

        TokenBucketFillerThread(TokenBucket tokenBucket) {
            this.tokenBucket = tokenBucket;
        }

        @Override
        public void run() {
            this.tokenBucket.refillBucket();
        }
    }
}
