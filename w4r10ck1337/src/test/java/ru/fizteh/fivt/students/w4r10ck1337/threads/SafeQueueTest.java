package ru.fizteh.fivt.students.w4r10ck1337.threads;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class SafeQueueTest {
    SafeQueue<Integer> queue;

    @Before
    public void setUp() {
        queue = new SafeQueue(10);
    }

    @Test
    public void testSimpleQueueSingleThread() {
        List list = new ArrayList();

        IntStream.range(0, 10).forEach(t -> list.add(t));
        queue.offer(list);
        assertEquals(list, queue.take(10));

        queue.offer(list);
        assertEquals(list, queue.take(10));
    }

    @Test
    public void testSimpleQueueMultiThread() {
        List list = new ArrayList();
        List ans = new ArrayList();

        for (int i = 0; i < 10; i++) {
            list.add(i);
            ans.add(i % 5);
        }
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    queue.offer(list.subList(0, 5));
                }
            }
        };
        t.start();
        assertEquals(ans, queue.take(10));
        assertEquals(ans, queue.take(10));
    }

    @Test(timeout = 1000)
    public void testTimeouts() {
        List list = new ArrayList();

        IntStream.range(0, 15).forEach(t -> list.add(t));
        queue.take(10, 10);
        queue.offer(list, 10);
    }

    @Test(timeout = 1000)
    public void testTimeoutsCorrect() {
        List list = new ArrayList();

        IntStream.range(0, 10).forEach(t -> list.add(t));
        queue.offer(list, 100);
        assertEquals(list, queue.take(10, 100));
    }
}
