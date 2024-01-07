import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int THREAD_COUNT = 100;
    private static AtomicInteger atomicCounter = new AtomicInteger(0);
    private static int normalCounter = 0;

    static class AtomicIncrementerThread extends Thread {
        @Override
        public void run() {
            for(int i = 0; i < 1000; i++)
                atomicCounter.incrementAndGet();
        }
    }

    static class NormalIncrementerThread extends Thread {
        @Override
        public void run() {
            for(int i = 0; i < 1000; i++)
                normalCounter++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] atomicThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            atomicThreads[i] = new AtomicIncrementerThread();
            atomicThreads[i].start();
        }
        for (int i = 0; i < THREAD_COUNT; i++) {
            atomicThreads[i].join();
        }
        System.out.println("AtomicCounter's value: " + atomicCounter.get());

        Thread[] normalThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            normalThreads[i] = new NormalIncrementerThread();
            normalThreads[i].start();
        }
        for (int i = 0; i < THREAD_COUNT; i++) {
            normalThreads[i].join();
        }
        System.out.println("NormalCounter's value: " + normalCounter);
    }
}