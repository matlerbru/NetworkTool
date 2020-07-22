package org.mlb.Utility;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.*;

public class GetAmountAliveTest {


    private LinkedList<Thread> threads = new LinkedList<>();

    private int upperTestRange;

    @Before
    public void initialize() {
        upperTestRange = (int) ((Math.random() * 100) + 1.0);

        for(int i = 0; i < upperTestRange; i++){
            threads.add( new Thread(() -> {
                Threads.sleep(200);
            }));
        }
    }

    @Test
    public void getAmountAlive(){
        assertEquals(0, Threads.getAmountAlive(threads));

        for (Thread thread : threads){
            thread.start();
        }

        Threads.sleep(100);
        assertEquals(upperTestRange, Threads.getAmountAlive(threads));
    }
} 