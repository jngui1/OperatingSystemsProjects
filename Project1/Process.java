// A class used for storing info about processes read from a given file
public class Process
{
    public int pid;
    public int arrivalTime;
    public int burstTime;
    public int priority;

    public Process(int pid, int arrivalTime, int burstTime, int priority)
    {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}
