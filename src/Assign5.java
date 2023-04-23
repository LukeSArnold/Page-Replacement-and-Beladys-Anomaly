import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Assign5 {

    static private final int MAX_PAGE_REFERENCE = 250;
    static private ArrayList<int[]> fifoFaults = new ArrayList<int[]>();
    static private ArrayList<int[]> lruFaults = new ArrayList<int[]>();
    static private ArrayList<int[]> mruFaults = new ArrayList<int[]>();

    public static void main(String[] args) throws InterruptedException {

        Date currentDate = new Date();
        long startTime = currentDate.getTime();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        int[][] total_fifoFaults = new int[1000][];
        int[][] total_lruFaults = new int[1000][];
        int[][] total_mruFaults = new int[1000][];

        for (int i = 0; i < 1000; i ++){
            int[] fifoFaults = new int[100];
            int[] lruFaults = new int[100];
            int[] mruFaults = new int[100];

            total_fifoFaults[i] = fifoFaults;
            total_lruFaults[i] = lruFaults;
            total_mruFaults[i] = mruFaults;

            runSimulation(executor, fifoFaults, lruFaults, mruFaults);
        }

        executor.shutdown();
        while (executor.awaitTermination(10, TimeUnit.SECONDS)) {
            Date newDate = new Date();
            long finalTime = newDate.getTime() - startTime;
            System.out.println("SIMULATION TOOK "+finalTime+" ms TO COMPLETE");

            summarizeResults(total_fifoFaults, total_lruFaults, total_mruFaults);
            break;
        }
    }

    private static void summarizeResults(int[][] fifoFaults, int[][] lruFaults, int[][] mruFaults){
        int fifoLowest = 0;
        int mruLowest = 0;
        int lruLowest = 0;

        for (int sim = 0; sim < 1000; sim++){
            for (int frame = 0; frame < 100; frame++){

                int fifoVal = fifoFaults[sim][frame];
                int mruVal = mruFaults[sim][frame];
                int lruVal = lruFaults[sim][frame];

                if ((fifoVal == mruVal) && (mruVal == lruVal)){
                    fifoLowest++;
                    mruLowest++;
                    lruLowest++;
                }

                else if ((fifoVal <= mruVal) && (fifoVal <= lruVal)){
                    fifoLowest++;
                    if (fifoVal == mruVal){
                        mruLowest++;
                    } else if (fifoVal == lruVal){
                        lruLowest++;
                    }
                }

                else if ((mruVal <= fifoVal) && (mruVal <= lruVal)){
                    mruLowest++;
                    if (mruVal == fifoVal){
                        fifoLowest++;
                    } else if (mruVal == lruVal){
                        lruLowest++;
                    }
                }

                else if ((lruVal <= mruVal) && (lruVal <= fifoVal)) {
                    lruLowest++;
                    if (mruVal == lruVal) {
                        mruLowest++;
                    } else if (fifoVal == lruVal) {
                        fifoLowest++;
                    }
                }
            }
        }
        System.out.println("FIFO min PF:" +fifoLowest);
        System.out.println("LRU min PF: "+lruLowest);
        System.out.println("MRU min PF: "+mruLowest);
    }
    private static void runSimulation(ExecutorService executor, int[] fifoFaults, int[] lruFaults, int[] mruFaults) {
        int[] sequence = generateSequence(1000, MAX_PAGE_REFERENCE);

        for (int frame = 1; frame < 100; frame++) {
            {
                TaskFIFO fifoTask = new TaskFIFO(sequence, frame, MAX_PAGE_REFERENCE, fifoFaults);
                TaskLRU lruTask = new TaskLRU(sequence, frame, MAX_PAGE_REFERENCE, lruFaults);
                TaskMRU mruTask = new TaskMRU(sequence, frame, MAX_PAGE_REFERENCE, mruFaults);

                executor.execute(fifoTask);
                executor.execute(lruTask);
                executor.execute(mruTask);
            }
        }
    }
    private static int[] generateSequence(int length, int max_reference){
        int[] sequence = new int[length];
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sequence[i] = rand.nextInt(max_reference);
        }
        return sequence;
    }
    public static void testFIFO() {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames is 3

        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskFIFO(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);

        // Replacement should be: 2, 1, 3, 1, 2.
        // Page Faults should be 7
        (new TaskFIFO(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);

        // Replacement should be: 1
        // Page Faults should be 4
        (new TaskFIFO(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }

    public static void testLRU() {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames is 3

        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskLRU(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);

        // Replacement should be: 2, 1, 3, 1, 2.
        // Page Faults should be 7
        (new TaskLRU(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);

        // Replacement should be: 1
        // Page Faults should be 4
        (new TaskLRU(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }

    public static void testMRU() {
        int[] sequence1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence2 = {1, 2, 1, 3, 2, 1, 2, 3, 4};
        int[] pageFaults = new int[4];  // 4 because maxMemoryFrames is 3

        // Replacement should be: 1, 2, 3, 4, 5, 6, 7, 8
        // Page Faults should be 9
        (new TaskMRU(sequence1, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[1]);

        // Replacement should be: 1, 2, 1, 3
        // Page Faults should be 6
        (new TaskMRU(sequence2, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[2]);

        // Replacement should be: 3
        // Page Faults should be 4
        (new TaskMRU(sequence2, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.printf("Page Faults: %d\n", pageFaults[3]);
    }


}