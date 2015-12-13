package ru.fizteh.fivt.students.w4r10ck1337.threads;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Call {
    private static CyclicBarrier askBarrier, waitForAnswerBarrier, checkSuccessBarrier;
    private static boolean success = false;
    private static Object monitor = new Object();

    private static class CountThread extends Thread {
        private boolean result;
        private Random rand = new Random();

        @Override
        public void run() {
            while (true) {
                try {
                    askBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                result = rand.nextInt(10) != 0;
                if (result) {
                    System.out.println("Yes");
                } else {
                    System.out.println("No");
                }
                synchronized (monitor) {
                    success &= result;
                }
                try {
                    waitForAnswerBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                if (success) {
                    return;
                }
                try {
                    checkSuccessBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int n;
        try {
            n = new Integer(args[0]);
            if (n <= 0) {
                throw new NumberFormatException("");
            }
        } catch (Exception e) {
            System.out.print("Arg should be positive integer");
            return;
        }

        CountThread[] threads = new CountThread[n];
        askBarrier = new CyclicBarrier(n + 1);
        waitForAnswerBarrier = new CyclicBarrier(n + 1);
        checkSuccessBarrier = new CyclicBarrier(n + 1);

        for (int i = 0; i < n; i++) {
            threads[i] = new CountThread();
            threads[i].start();
        }

        while (!success) {
            System.out.println("Are you ready?");
            success = true;
            try {
                askBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            askBarrier.reset();
            try {
                waitForAnswerBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            waitForAnswerBarrier.reset();
            if (success) {
                break;
            }
            try {
                checkSuccessBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < n; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
