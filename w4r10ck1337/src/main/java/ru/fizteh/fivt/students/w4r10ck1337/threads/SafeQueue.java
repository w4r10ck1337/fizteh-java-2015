package ru.fizteh.fivt.students.w4r10ck1337.threads;

import com.sun.org.apache.bcel.internal.generic.LSTORE;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SafeQueue<T> {
    private int maxSize;
    private Queue<T> queue;
    private Lock queueLock = new ReentrantLock();
    private Lock stateChanged = new ReentrantLock();
    private Condition popWait = stateChanged.newCondition();
    private Condition pushWait = stateChanged.newCondition();
    private ExecutorService executor = Executors.newCachedThreadPool();

    private long getTimeout(long timeLimit) throws InterruptedException {
        long currTime = System.currentTimeMillis();
        if (currTime > timeLimit) {
            throw new InterruptedException("Timeout");
        }
        return timeLimit - currTime;
    }

    public void offer(List<T> e, long timeout, boolean needTimeout) throws InterruptedException {
        long timeLimit = System.currentTimeMillis() + timeout;
        if (needTimeout) {
            if (!stateChanged.tryLock(getTimeout(timeLimit), TimeUnit.MILLISECONDS)) {
                throw new InterruptedException("Timeout");
            }
        } else {
            stateChanged.lock();
        }
        try {
            boolean added = false;
            while (!added) {
                try {
                    if (needTimeout) {
                        if (!queueLock.tryLock(getTimeout(timeLimit), TimeUnit.MILLISECONDS)) {
                            throw new InterruptedException("Timeout");
                        }
                    } else {
                        queueLock.lock();
                    }
                    if (queue.size() + e.size() <= maxSize) {
                        queue.addAll(e);
                        added = true;
                    }
                } finally {
                    queueLock.unlock();
                }
                if (!added) {
                    if (needTimeout) {
                        if (!popWait.await(getTimeout(timeLimit), TimeUnit.NANOSECONDS)) {
                            throw new InterruptedException("Timeout");
                        }
                    } else {
                        popWait.await();
                    }
                }
            }
        } finally {
            pushWait.signalAll();
            stateChanged.unlock();
        }
    }

    public void offer(List<T> e) {
        try {
            offer(e, 0, false);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void offer(List<T> e, long timeout) throws InterruptedException {
        offer(e, timeout, true);
    }

    List<T> take(int n, long timeout, boolean needTimeout) throws InterruptedException {
        long timeLimit = System.currentTimeMillis() + timeout;
        if (needTimeout) {
            if (!stateChanged.tryLock(getTimeout(timeLimit), TimeUnit.MILLISECONDS)) {
                throw new InterruptedException("Timeout");
            }
        } else {
            stateChanged.lock();
        }
        try {
            List ans = new ArrayList<>();
            while (ans.size() < n) {
                try {
                    if (needTimeout) {
                        if (!queueLock.tryLock(getTimeout(timeLimit), TimeUnit.MILLISECONDS)) {
                            throw new InterruptedException("Timeout");
                        }
                    } else {
                        queueLock.lock();
                    }
                    if (queue.size() >= n) {
                        for (int i = 0; i < n; i++) {
                            ans.add(queue.poll());
                        }
                    }
                } finally {
                    queueLock.unlock();
                    if (ans.size() == n) {
                        return ans;
                    }
                }
                if (ans.size() < n) {
                    if (needTimeout) {
                        if (!pushWait.await(getTimeout(timeLimit), TimeUnit.NANOSECONDS)) {
                            throw new InterruptedException("Timeout");
                        }
                    } else {
                        pushWait.await();
                    }
                }
            }
            return ans;
        } finally {
            popWait.signalAll();
            stateChanged.unlock();
        }
    }

    public List<T> take(int n) {
        try {
            return take(n, 0, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<T> take(int n, long timeout) throws InterruptedException {
        return take(n, timeout, true);
    }



    SafeQueue(int maxSize) {
        this.maxSize = maxSize;
        queue = new ArrayDeque<>();
    }
}
