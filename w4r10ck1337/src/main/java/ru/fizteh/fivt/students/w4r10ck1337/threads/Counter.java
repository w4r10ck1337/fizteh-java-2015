package ru.fizteh.fivt.students.w4r10ck1337.threads;

public class Counter {
    private static volatile int currentId;
    private static class CountThread extends Thread {
        private int id, nextId;

        @Override
        public void run() {
            while (true) {
                while (id != currentId) {
                    Thread.yield();
                }
                System.out.println("Thread-" + String.valueOf(id + 1));
                currentId = nextId;
            }
        }
        CountThread(int id, int nextId) {
            this.id = id;
            this.nextId = nextId;
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
        currentId = 0;
        for (int i = 0; i < n; i++) {
            CountThread thread = new CountThread(i, (i + 1) % n);
            thread.start();
        }
    }
}