package server.helper;

import java.util.TimerTask;

public class CountDownTimer extends TimerTask {

    private int timeRemaining;

    public CountDownTimer(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    @Override
    public void run() {
        timeRemaining--;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

}
