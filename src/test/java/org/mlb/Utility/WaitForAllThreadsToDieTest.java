package org.mlb.Utility;

import static org.junit.Assert.*;

import org.mlb.NetworkTool.*;

import java.util.LinkedList;

import org.junit.*;

public class WaitForAllThreadsToDieTest {

    private LinkedList<Thread> threads = new LinkedList<>();

    private int upperTestRange;

    @Before
    public void initialize() {
        upperTestRange = (int) ((Math.random() * 100) + 1.0);

        for(int i = 0; i < upperTestRange; i++){
            threads.add( new Thread(() -> {
                Sleep.sleep(200);
            }));
            threads.get(i).start();
        }
    }

    @Test
    public void t_wait(){
        WaitForAllThreadsToDie.wait(threads);
        assertEquals(0, AmountOfThreadsAlive.getAmountOfThreadsAlive(threads));
    }

} 
