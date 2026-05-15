package util;

public class Timer {
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        endTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public double getElapsedTimeSeconds() {
        return (endTime - startTime) / 1000.0;
    }

    public static double calculateSpeedUp(long sequentialTime, long parallelTime) {
        if (parallelTime == 0) {
            return 0;
        }
        return (double) sequentialTime / parallelTime;
    }

    @Override
    public String toString() {
        return String.format("Tempo: %.2f segundos (%d ms)", getElapsedTimeSeconds(), getElapsedTime());
    }
}
