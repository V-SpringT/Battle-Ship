/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.helper;

import java.util.TimerTask;

import javax.swing.JLabel;

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
