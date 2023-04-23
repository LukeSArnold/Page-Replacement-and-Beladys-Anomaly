import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class TaskMRU implements Runnable {
    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;
    public TaskMRU (int [] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults){
        this.sequence = sequence;
        this.maxPageReference = maxPageReference;
        this.maxMemoryFrames = maxMemoryFrames;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run(){
        List<Integer> frames = new ArrayList<>();
        int faults = 0;
        for (int i =0; i<sequence.length;i++){
            int currentReference = sequence[i];
            if (!frames.contains(currentReference)) {
                faults++;
                if (frames.size() < maxMemoryFrames) {
                    frames.add(sequence[i]);
                } else {
                    frames.set((frames.indexOf(sequence[i - 1])),sequence[i]);
                }
            }
        }
        pageFaults[maxMemoryFrames] = faults;
    }
}
