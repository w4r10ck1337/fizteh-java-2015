package ru.fizteh.fivt.students.w4r10ck1337.threads;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SafeQueue {
    private int maxSize;
    private Queue queue;
    private Lock queueLock = new ReentrantLock();

    private Lock lock = new ReentrantLock();
    private Condition popWait = lock.newCondition();
    private Condition pushWait = lock.newCondition();

    public void offer(List e) {
        lock.lock();
        try {
            boolean added = false;
            while (!added) {
                try {
                    queueLock.lock();
                    if (queue.size() + e.size() <= maxSize) {
                        e.forEach(t -> queue.add(t));
                        added = true;
                    }
                } finally {
                    queueLock.unlock();
                }
                if (!added) {
                    try {
                        popWait.await();
                    } catch (InterruptedException ex) {
                        return;
                    }
                }
            }
        } finally {
            pushWait.signalAll();
            lock.unlock();
        }
    }

    public void offer(List e, long timeout) {
        Thread t = new Thread() {
            public void run() {
                offer(e);
            }
        };
        t.start();
        try {
            t.join(timeout);
            if (t.isAlive()) {
                t.interrupt();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return;
        }
    }

    List take(int n) {
        lock.lock();
        try {
            List ans = new ArrayList<>();
            while (ans.size() < n) {
                try {
                    queueLock.lock();
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
                    try {
                        pushWait.await();
                    } catch (InterruptedException ex) {
                        return null;
                    }
                }
            }
            return ans;
        } finally {
            popWait.signalAll();
            lock.unlock();
        }
    }

    private class Taker extends Thread {
        private List ans;
        private int n;

        @Override
        public void run() {
            ans = take(n);
        }

        public List getAns() {
            return ans;
        }

        Taker(int n) {
            this.n = n;
            ans = null;
        }
    }

    public List take(int n, long timeout) {
        Taker t = new Taker(n);
        t.start();

        try {
            t.join(timeout);
            if (t.isAlive()) {
                t.interrupt();
                return null;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }
        return t.getAns();
    }

    SafeQueue(int maxSize) {
        this.maxSize = maxSize;
        queue = new ArrayDeque<>();
    }
}
