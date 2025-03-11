import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;

public class LeakyBucket implements RateLimiter {
    private int queueSize;
    private String id;
    private Deque<String> queue = new ArrayDeque<String>();

    private long success;
    private long failures;

    public LeakyBucket(String id, int queueSize) {
        this.id = id;
        this.queueSize = queueSize;
    }

    @Override
    public void start() {
        Timer timer = new Timer("LeakyBucketProcessor_" + id);
        timer.scheduleAtFixedRate(new ProcessorThread(this), 1, 2 * 1000);
    }

    @Override
    public boolean isRequestAllowed(String id) {
        if (queue.size() < queueSize) {
            queue.addLast(id);
            ++success;
            return true;
        }
        ++failures;
        return false;
    }


    private void processRequests() {
        queue.clear();   // process all :)
    }

    public long getSuccessCount() {
        return success;
    }

    public long getFailureCount() {
        return failures;
    }

    static class ProcessorThread extends TimerTask {
        private LeakyBucket leakyBucket;

        ProcessorThread(LeakyBucket leakyBucket) {
            this.leakyBucket = leakyBucket;
        }

        @Override
        public void run() {
            this.leakyBucket.processRequests();
        }
    }

}
