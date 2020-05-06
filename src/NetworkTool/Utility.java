package NetworkTool;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Utility {

    public static class Threads {

        public static void sleep(int time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static void waitForAllThreadsToDie(List<Thread> threads) {
            while (threads.size() > 0) {
                for (int i = 0; i < threads.size(); i++) {
                    if (!threads.get(i).isAlive()) {
                        threads.remove(i);
                    }
                }
            }
        }

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

    public static int ordinalIndexOf(String string, String substring, int n) {
        try {
            int position = string.indexOf(substring);
            while (--n > 0 && position != -1)
                position = string.indexOf(substring, position + 1);
            return position;
        } catch (Exception e) {
            return -1;
        }
    }

}
