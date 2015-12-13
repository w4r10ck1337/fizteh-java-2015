package ru.fizteh.fivt.students.w4r10ck1337.threads;

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

    public void offer(List<T> e) {
        stateChanged.lock();
        try {
            boolean added = false;
            while (!added) {
                try {
                    queueLock.lock();
                    if (queue.size() + e.size() <= maxSize) {
                        queue.addAll(e);
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
            stateChanged.unlock();
        }
    }

    public void offer(List<T> e, long timeout) {
        Future future = executor.submit(() -> offer(e));
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            return;
        } finally {
            future.cancel(true);
        }
    }

    List<T> take(int n) {
        stateChanged.lock();
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
            stateChanged.unlock();
        }
    }

    public List<T> take(int n, long timeout) {
        Future<List<T>> future = executor.submit(() -> take(n));
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            return null;
        } finally {
            future.cancel(true);
        }
    }

    SafeQueue(int maxSize) {
        this.maxSize = maxSize;
        queue = new ArrayDeque();
    }
}
