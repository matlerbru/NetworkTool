package org.mlb.NetworkTool;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.*;
import org.mlb.Utility.AmountOfThreadsAlive;

public class AmountOfThreadsAliveTest {


    private LinkedList<Thread> threads = new LinkedList<>();

    private int upperTestRange;

    @Before
    public void initialize() {
        upperTestRange = (int) ((Math.random() * 100) + 1.0);

        for(int i = 0; i < upperTestRange; i++){
            threads.add( new Thread(() -> {
                while(true){
                    Utility.Threads.sleep(200);
                }
            }));
        }
    }

    @Test
    public void getAmountOfThreadsAlive(){
        assertEquals(0, AmountOfThreadsAlive.getAmountOfThreadsAlive(threads));

        for (Thread thread : threads){
            thread.start();
        }

        Utility.Threads.sleep(100);
        assertEquals(upperTestRange, AmountOfThreadsAlive.getAmountOfThreadsAlive(threads));
    }
} 