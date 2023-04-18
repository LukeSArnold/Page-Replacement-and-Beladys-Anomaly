import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class TaskFIFO implements Runnable {

    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;
    public TaskFIFO(int [] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults){
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
        LinkedList<Integer> accessOrder = new LinkedList<Integer>();

        for (int element : sequence) {
            if (!frames.contains(element)) {
                faults++;
                //System.out.println("DIDN'T CONTAIN "+ element);
                if (frames.size() < maxMemoryFrames) {
                    frames.add(element);
                    accessOrder.add(element);
                } else {
                    replacements.add(accessOrder.getFirst());
                    frames.set(frames.indexOf(accessOrder.pop()),element);
                    accessOrder.add(element);
                }
            }
        }

        System.out.println("REPLACED: "+replacements);
        pageFaults[maxMemoryFrames] = faults;
    }
}
