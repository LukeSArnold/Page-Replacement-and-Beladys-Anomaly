import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Assign5 {
    static private final int MAX_PAGE_REFERENCE = 250;
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
            summarizeBeladyAnamoly(total_fifoFaults, total_lruFaults, total_mruFaults);
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

    public static void summarizeBeladyAnamoly(int[][] fifoFaults, int[][] lruFaults, int[][] mruFaults){
        int previousFIFO = 0;
        int fifoAnamolys = 0;
        int maxDistanceFifo = 0;

        // Summarizing Results for FIFO Simulations
        System.out.println("\nBelady's Anomaly Report for FIFO");
        for (int sim = 0; sim < 1000; sim++){
            for (int frame = 0; frame < 100; frame++){
                int currentFIFO = fifoFaults[sim][frame];
                if (previousFIFO == 0){
                    previousFIFO = currentFIFO;
                } else {
                    if (currentFIFO > previousFIFO){
                        int distance = currentFIFO - previousFIFO;
                        if (distance > maxDistanceFifo){
                            maxDistanceFifo = distance;
                        }
                        fifoAnamolys++;
                        System.out.println("\t\tdetected - Previous "+previousFIFO+"\t : Current "+currentFIFO+" ("+distance+")");
                    }
                }
                previousFIFO = currentFIFO;
            }
        }
        System.out.println("\tAnomaly detected "+fifoAnamolys+" times with a max difference of "+maxDistanceFifo);

        // Summarizing Results for LRU Simulations
        int previousLRU = 0;
        int LRUAnomalys = 0;
        int maxDistanceLRU = 0;
        System.out.println("\nBelady's Anomaly Report for LRU");
        for (int sim = 0; sim < 1000; sim++){
            for (int frame = 0; frame < 100; frame++){
                int currentLRU = lruFaults[sim][frame];
                if (previousLRU == 0){
                    previousLRU = currentLRU;
                } else {
                    if (currentLRU > previousLRU){
                        int distance = currentLRU - previousLRU;
                        if (distance > maxDistanceLRU){
                            maxDistanceLRU = distance;
                        }
                        LRUAnomalys++;
                        System.out.println("\t\tdetected - Previous "+previousLRU+"\t : Current "+currentLRU+" ("+distance+")");
                    }
                }
                previousLRU = currentLRU;
            }
        }
        System.out.println("\tAnomaly detected "+LRUAnomalys+" times with a max difference of "+maxDistanceLRU);

        // Summarizing Results for MRU Simulations
        int previousMRU = 0;
        int MRUAnomalys = 0;
        int maxDistanceMRU = 0;
        System.out.println("\nBelady's Anomaly Report for MRU");
        for (int sim = 0; sim < 1000; sim++){
            for (int frame = 0; frame < 100; frame++){
                int currentMRU = mruFaults[sim][frame];
                if (previousMRU == 0){
                    previousMRU = currentMRU;
                } else {
                    if (currentMRU > previousMRU){
                        int distance = currentMRU - previousMRU;
                        if (distance > maxDistanceMRU){
                            maxDistanceMRU = distance;
                        }
                        MRUAnomalys++;
                        System.out.println("\t\tdetected - Previous "+previousMRU+"\t : Current "+currentMRU+" ("+distance+")");
                    }
                }
                previousMRU = currentMRU;
            }
        }
        System.out.println("\tAnomaly detected "+MRUAnomalys+" times with a max difference of "+maxDistanceMRU);
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
}