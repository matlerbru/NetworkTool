package org.mlb.Utility;

import java.util.List;

public class Threads {

    static public void waitForAllToDie(List<Thread> threads){
        while (threads.size() > 0) {
            for (int i = 0; i < threads.size(); i++) {
                if (!threads.get(i).isAlive()) {
                    threads.remove(i);
                }
            }
        }
    }

    public static int getAmountAlive(List<Thread> scans) {
        int threadsRunning = 0;
        for (Thread scan : scans) {
            if (scan.isAlive()) {
                threadsRunning++;
            }
        }
        return threadsRunning;
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}