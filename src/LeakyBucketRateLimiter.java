public class LeakyBucketRateLimiter {
    private final int capacity;
    private final double leakRatePerMs;
    private double waterLevel;
    private long lastLeakTimestamp;

    public LeakyBucketRateLimiter(int capacity, int leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerMs = (double) leakRatePerSecond / 1000.0;
        this.waterLevel = 0.0;
        this.lastLeakTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        leak();

        if (waterLevel < capacity) {
            waterLevel += 1.0;
            return true;
        }
        return false;
    }

    private void leak() {
        long now = System.currentTimeMillis();
        long elapsedMs = now - lastLeakTimestamp;
        if (elapsedMs > 0) {
            double leakedAmount = elapsedMs * leakRatePerMs;
            waterLevel = Math.max(0.0, waterLevel - leakedAmount);
            lastLeakTimestamp = now;
        }
    }

    // Package-private or public getter for testing purposes
    public synchronized double getWaterLevel() {
        leak();
        return waterLevel;
    }
}
