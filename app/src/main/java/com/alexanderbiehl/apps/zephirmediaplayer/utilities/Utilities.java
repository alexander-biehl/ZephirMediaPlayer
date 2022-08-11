package com.alexanderbiehl.apps.zephirmediaplayer.utilities;

public class Utilities {

    /**
     * Function to convert milliseconds time to Timer format
     * Hours:Minutes:Seconds
     */
    public String millisecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondString = "";

        // Convert total duration into time
        int hours = (int)(milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds / (1000*60*60)) / (1000*6);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondString;

        // return timer string
        return finalTimerString;
    }

    /**
     *  Function to get Progress Percentage
     * @param currentDuration
     * @param totalDuration
     * @return
     */
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = 0d;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculate percentage
        percentage = (((double)currentSeconds)/totalSeconds)*1000;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress
     * @param totalDuration
     * @return - current duration in milliseconds
     */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
