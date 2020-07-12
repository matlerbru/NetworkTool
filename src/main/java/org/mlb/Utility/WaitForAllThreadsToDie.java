package org.mlb.Utility;

import java.util.List;

public class WaitForAllThreadsToDie {

    static public void wait(List<Thread> threads){
        while (threads.size() > 0) {
            for (int i = 0; i < threads.size(); i++) {
                if (!threads.get(i).isAlive()) {
                    threads.remove(i);
                }
            }
        }
    }
    
}