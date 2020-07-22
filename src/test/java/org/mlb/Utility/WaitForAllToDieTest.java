package org.mlb.Utility;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.*;

public class WaitForAllToDieTest {

    private LinkedList<Thread> threads = new LinkedList<>();

    private int upperTestRange;

    @Before
    public void createThreads() {
        upperTestRange = (int) ((Math.random() * 100) + 1.0);

        for(int i = 0; i < upperTestRange; i++){
            threads.add( new Thread(() -> {
                Threads.sleep(200);
            }));
            threads.get(i).start();
        }
    }

    @Test
    public void WaitForAllToDie(){
        Threads.waitForAllToDie(threads);
        assertEquals(0, Threads.getAmountAlive(threads));
    }

} 
