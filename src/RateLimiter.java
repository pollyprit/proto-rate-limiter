interface RateLimiter {
    void start();

    boolean isRequestAllowed(String id);
    long getSuccessCount();
    long getFailureCount();
}
