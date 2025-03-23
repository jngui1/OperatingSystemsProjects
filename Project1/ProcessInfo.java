// A class used for storing info relating to processes' start and end times
public class ProcessInfo {
    public int pid;
    public int startTime;
    public int endTime;

    public ProcessInfo(int pid, int startTime, int endTime)
    {
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
