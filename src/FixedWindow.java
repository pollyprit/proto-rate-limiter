import java.util.Timer;
import java.util.TimerTask;

public class FixedWindow implements RateLimiter {
    private int window;
    private int windowSize;
    private String id;
    private int windowExpiryTimeSec;
    private long success;
    private long failures;

    public FixedWindow(String id, int windowSize, int windowExpiryTimeSec) {
        this.id = id;
        this.windowSize = windowSize;
        this.windowExpiryTimeSec = windowExpiryTimeSec;
    }

    @Override
    public void start() {
        Timer timer = new Timer("RLWindowExpiry_" + id);
        timer.scheduleAtFixedRate(new WindowExpiryThread(this),
                this.windowExpiryTimeSec * 100, this.windowExpiryTimeSec * 1000);
    }

    @Override
    public boolean isRequestAllowed(String id) {
        if (window > 0) {
            --window;
            ++success;
            return true;
        }
        ++failures;
        return false;
    }


    public void windowExpired() {
        window = windowSize;
    }

    public long getSuccessCount() {
        return success;
    }

    public long getFailureCount() {
        return failures;
    }

    static class WindowExpiryThread extends TimerTask {
        private FixedWindow fixedWindow;

        WindowExpiryThread(FixedWindow fixedWindow) {
            this.fixedWindow = fixedWindow;
        }

        @Override
        public void run() {
            this.fixedWindow.windowExpired();
        }
    }

}
