package domain;

public class DrowsinessLogic {
    private static final long DROWSY_THRESHOLD_MS = 1500;
    private static final long ALARM_THRESHOLD_MS  = 3000;

    private long eyesClosedSince = -1;
    private EyeState currentState = EyeState.AWAKE;

    public EyeState evaluate(boolean eyesDetected) {
        long now = System.currentTimeMillis();

        if (eyesDetected) {
            eyesClosedSince = -1;
            currentState = EyeState.AWAKE;
        } else {
            if (eyesClosedSince == -1) {
                eyesClosedSince = now;
            }

            long closed = now - eyesClosedSince;

            if (closed >= ALARM_THRESHOLD_MS) {
                currentState = EyeState.ALARM_ACTIVE;
            } else if (closed >= DROWSY_THRESHOLD_MS) {
                currentState = EyeState.DROWSY;
            }
        }

        return currentState;
    }

    public boolean shouldActivateAlarm() {
        return currentState == EyeState.ALARM_ACTIVE;
    }

    public EyeState getCurrentState() {
        return currentState;
    }

    public long getClosedDurationMs() {
        if (eyesClosedSince == -1) return 0;
        return System.currentTimeMillis() - eyesClosedSince;
    }

    public void reset() {
        eyesClosedSince = -1;
        currentState = EyeState.AWAKE;
    }
}






