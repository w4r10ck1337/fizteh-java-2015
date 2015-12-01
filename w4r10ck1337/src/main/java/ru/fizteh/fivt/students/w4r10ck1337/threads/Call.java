package ru.fizteh.fivt.students.w4r10ck1337.threads;

import java.io.InputStream;
import java.util.Random;

public class Call {
    private static class CountThread extends Thread {
        private int id;
        private boolean result;
        Random rand = new Random();

        public boolean getResult() {
            return result;
        }

        @Override
        public void run() {
            result = rand.nextInt(10) != 0;
            if (result) {
                System.out.println("Yes");
            } else {
                System.out.println("No");
            }
        }

        CountThread(int id) {
            this.id = id;
        }
    }

    public static void main(String[] args) {
        int n = 0;
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

        boolean success = true;
        while(success) {
            System.out.println("Are you ready?");
            for (int i = 0; i < n; i++) {
                threads[i] = new CountThread(i);
                threads[i].start();
            }
            for (int i = 0; i < n; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                success &= threads[i].getResult();
            }
        }
    }
}
