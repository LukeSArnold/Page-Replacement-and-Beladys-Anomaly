import java.util.*;

public class TaskLRU implements Runnable{
    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;
    public TaskLRU(int [] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults){
        this.sequence = sequence;
        this.maxPageReference = maxPageReference;
        this.maxMemoryFrames = maxMemoryFrames;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run(){
        List<Integer> frames = new ArrayList<>();
        int faults = 0;
        List<Integer> replacements = new ArrayList<Integer>();
        Deque<Integer> accessOrder = new LinkedList<Integer>();

        for (int element : sequence) {
            if (!frames.contains(element)) {
                faults++;
                //System.out.println("DIDN'T CONTAIN "+ element);
                if (frames.size() < maxMemoryFrames) {
                    frames.add(element);
                    accessOrder.add(element);

                } else {
                    //System.out.println("LEAST USED ELEMENT IS "+accessOrder.getFirst());
                    frames.set(frames.indexOf(accessOrder.getFirst()),element);
                    replacements.add(accessOrder.getFirst());
                    accessOrder.remove(accessOrder.getFirst());
                    accessOrder.add(element);

                    //System.out.println("REPLACED: "+accessOrder.getFirst());
                }
            } else {
                accessOrder.remove(element);
                accessOrder.add(element);
            }
            //System.out.println("ARRAY: "+frames);
        }

        System.out.println("REPLACED: "+replacements);
        pageFaults[maxMemoryFrames] = faults;
    }
}
