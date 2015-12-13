package ru.fizteh.fivt.students.w4r10ck1337.threads;

public class Counter {
    private static int currentId;
    private static Object monitor = new Object();

    private static class CountThread extends Thread {
        private int id, nextId;

        @Override
        public void run() {
            while (true) {
                synchronized (monitor) {
                    while (id != currentId) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Thread-" + String.valueOf(id + 1));
                    currentId = nextId;
                    monitor.notifyAll();
                }
            }
        }
        CountThread(int id, int nextId) {
            this.id = id;
            this.nextId = nextId;
        }
    }

    public static void main(String[] args) {
        int n;
        try {
            n = Integer.valueOf(args[0]);
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
