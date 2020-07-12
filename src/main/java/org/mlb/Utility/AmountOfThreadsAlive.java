package org.mlb.Utility;

import java.util.List;

public class AmountOfThreadsAlive {

    public static int getAmountOfThreadsAlive(List<Thread> scans) {
        int threadsRunning = 0;
        for (Thread scan : scans) {
            if (scan.isAlive()) {
                threadsRunning++;
            }
        }
        return threadsRunning;
    }

}



