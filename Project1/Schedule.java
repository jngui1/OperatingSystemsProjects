// Importing the 'File' class, which will be used for reading the file
// containing process information
import java.io.File;

// Importing 'FileNotFountException', which will be raised if the specified
// file cannot be found
import java.io.FileNotFoundException;

// Importing the 'ArrayList' class, which will be used for creating arrays that
// can added to and deleted from
import java.util.ArrayList;

// Importing the 'Scanner' class, which will be used for reading the file
// containing process information
import java.util.Scanner;

public class Schedule
{
    // The 'read_file' function takes the filepath of a file containing process
    // information as input and returns an array containing Process objects
    // with that information
    public static Process[] read_file(String filepath)
    {
        // The 'returnList' ArrayList will contain each of the Process objects
        ArrayList<Process> returnList = new ArrayList<Process>();

        try
        {
            // Creates a File object using the file at the given filepath
            File processFile = new File(filepath);

            // Creates a Scanner object that analyses the given file
            Scanner fileReader = new Scanner(processFile);

            // Converts the information in the file into Process objects if the
            // file is formatted correctly
            if ((fileReader.next().equals("PID")) &&
                (fileReader.next().equals("Arrival_Time")) &&
                (fileReader.next().equals("Burst_Time")) &&
                (fileReader.next().equals("Priority")))
            {
                // For each row of information in the file, a new Process object
                // is created with values for the PID, arrival time, burst
                // time, and priority
                while (fileReader.hasNextInt())
                {
                    returnList.add(new Process(fileReader.nextInt(),
                        fileReader.nextInt(), fileReader.nextInt(),
                        fileReader.nextInt()));
                }
            }

            // Returns the list of Process objects as a traditional array
            //rather than as an ArrayList
            return returnList.toArray(new Process[returnList.size()-1]);
        }

        // If a file is not found at the specified filepath, and error message
        // is printed
        catch (FileNotFoundException e)
        {
            System.out.println("File not found.");
        }

        // In the event of an invalid filepath being given, an empty array is
        // returned
        return new Process[0];
    }

    // The 'fcfs' function takes an array containing Process objects as input,
    // and outputs how the corresponding processes would run if a First-Come,
    // First-Served scheduling algorithm was used
    public static void fcfs(Process[] processArray)
    {
        // The 'timer' variable keeps track of the current time in CPU units as
        // processes run
        int timer = 0;

        // The 'readyQueue' ArrayList contains processes that have arrived and
        // are ready to run
        ArrayList<Process> readyQueue = new ArrayList<Process>();

        // The 'passedProcessesIndexes' ArrayList contains the indexes of
        // processes that don't have the highest priority and won't start
        // running until a later time
        ArrayList<Integer> passedProcessIndexes = new ArrayList<Integer>();

        // The 'doneProcesses' ArrayList contains processes that have finished
        // running
        ArrayList<Process> doneProcesses = new ArrayList<Process>();

        // The 'processTimes' ArrayList contains information about the
        // processes' start and end times
        ArrayList<ProcessInfo> processTimes = new ArrayList<ProcessInfo>();

        // The 'runningPID' variable tracks the PID of the currently running
        // process
        // A value of -1 indicates that no process is currently running
        int runningPID = -1;

        // The 'minArrivalTime' variable tracks the earliest arrival time when
        // determining the next process to run
        int minArrivalTime = 0;

        // The 'maxPriority' variable tracks the largest explicit priority
        // value when determining the next process to run
        int maxPriority = 0;

        // The 'loopNum' variable tracks the number of loop cycles when
        // clearing the 'readyQueue' ArrayList
        int loopNum = 0;

        // The 'avgWaitTime' variable is used for calculating the average
        // waiting time of each process
        float avgWaitTime = 0.0f;

        // The 'avgTurnTime' variable is used for calculating the average
        // turnaround time of each process
        float avgTurnTime = 0.0f;

        // The 'done' variable is used to track whether each process has
        // finished running
        boolean done = true;

        while(true)
        {
            // At the start of each cycle, 'done' is set to 'true', and is only
            // set to 'false' if there is at least one process that has yet to
            // finish running
            done = true;

            // Entries in 'processTimes' are generated when a process starts
            // running, so there being less entries in 'processTimes' than
            // 'processArray' indicates that at least one process has not
            // started - and thus not finished - running
            if (processArray.length <= processTimes.size())
            {
                // Entries in 'processTimes' are generated with 'endTime' set
                // to -1, and are updated once a process finishes, so a process
                // whose end time is set to this value has yet to finish
                // running
                for (ProcessInfo times : processTimes)
                {
                    if (times.endTime == -1)
                    {
                        done = false;

                        break;
                    }
                }
            }

            else
            {
                done = false;
            }

            // If all processes have finished running, the cycle breaks
            if (done == true)
            {
                break;
            }

            // If a process is currently running, it's burst time is checked
            // each cycle to see if the process is finished yet
            for (int index = 0; index < processArray.length; index++)
            {
                // A process is found to be running if its unique PID matches
                // up with 'runningPID'
                if (runningPID == processArray[index].pid)
                {
                    // If the process's listed burst time isn't greater than
                    // the difference between the current time and the time the
                    // process started running, the process has finished
                    for (ProcessInfo times : processTimes)
                    {
                        if ((processArray[index].pid == times.pid) &&
                            (processArray[index].burstTime <= (timer - times.startTime)))
                        {
                            // With the running process now finished,
                            // 'runningPID' is reset to its default value
                            runningPID = -1;

                            // The time at which the process finished is
                            // recorded in its corresponding 'processTimes' entry
                            times.endTime = timer;

                            // The finished process is added to
                            // 'doneProcesses', signifying it as finished
                            doneProcesses.add(processArray[index]);

                            break;
                        }
                    }
                    
                    break;
                }
            }

            // The function will begin looking for a new process to run if no
            // process is currently running
            if (runningPID == -1)
            {
                for (int index = 0; index < processArray.length; index++)
                {
                    // Each process that was set to arrive by the current cycle
                    // and hasn't already finished running is added to
                    // 'readyQueue'
                    if ((processArray[index].arrivalTime <= timer) &&
                        (doneProcesses.contains(processArray[index]) == false))
                    {
                        readyQueue.add(processArray[index]);
                    }
                }

                // If only one process is added to 'readyQueue', that process
                // begins running
                if (readyQueue.size() == 1)
                {
                    // The PID of the selected process is marked as that of the
                    // currently running process
                    runningPID = readyQueue.get(0).pid;

                    // A new entry in 'processTimes' is made for the process,
                    // listing the starting time as the current time
                    processTimes.add(new ProcessInfo(readyQueue.get(0).pid, timer, -1));

                    // 'readyQueue' is cleared for the next time a new process
                    // to run is determined
                    readyQueue.remove(0);
                }

                // If more than one process is added to 'readyQueue', the
                // process to start running is determined by which process
                // arrived first
                else if (readyQueue.size() > 1)
                {
                    // The earliest arrival time is initially set to that of
                    // the first process in 'readyQueue'
                    minArrivalTime = readyQueue.get(0).arrivalTime;

                    // Each process in 'readyQueue' is examined to see if it
                    // has an earlier arrival time
                    for (int index = 0; index < readyQueue.size(); index++)
                    {
                        // If a process has a later arrival time, its index in
                        // 'readyQueue' is stored in 'passedProcessIndexes' for
                        // later removal from 'readyQueue'
                        if (minArrivalTime < readyQueue.get(index).arrivalTime)
                        {
                            passedProcessIndexes.add(index);
                        }

                        // If a process has an earlier arrival time, its
                        // arrival time is marked as the new minimum, and the
                        // indexes of all other processes before it in
                        // 'readyQueue' are stored in 'passedProcessIndexes'
                        else if (minArrivalTime > readyQueue.get(index).arrivalTime)
                        {
                            minArrivalTime = readyQueue.get(index).arrivalTime;

                            for (int passedIndex = index - 1; passedIndex >= 0; passedIndex--)
                            {
                                // To avoid duplicates, an index is not stored
                                // in 'passedProcessIndexes' if it is already
                                // present
                                if (passedProcessIndexes.contains(passedIndex) == false)
                                {
                                    passedProcessIndexes.add(passedIndex);
                                }
                            }
                        }
                    }

                    // The indexes in 'passedProcessIndexes' are sorted in
                    // ascending order such that their corresponding entries in
                    // 'readyQueue' can be removed sequentially
                    passedProcessIndexes.sort(null);

                    // For each index in 'passedProcessIndexes', the
                    // corresponding entry in 'readyQueue' is removed
                    for (int index : passedProcessIndexes)
                    {
                        // As each removal from 'readyQueue' causes the indexes
                        // in 'passedProcessIndexes' to refer to the entry one
                        // more index value ahead, 'loopNum' is incremeted
                        // after each removal to compensate
                        readyQueue.remove(index - loopNum);

                        loopNum++;
                    }

                    // 'loopNum' is reset and 'passedProcessIndexes' is cleared
                    // for the next time a new process to run is determined
                    loopNum = 0;

                    passedProcessIndexes.clear();

                    // If only one process remains in 'readyQueue', that
                    // process begins running
                    if (readyQueue.size() == 1)
                    {
                        // The PID of the selected process is marked as that of
                        // the currently running process
                        runningPID = readyQueue.get(0).pid;

                        // A new entry in 'processTimes' is made for the
                        // process, listing the starting time as the current
                        // time
                        processTimes.add(new ProcessInfo(readyQueue.get(0).pid, timer, -1));

                        // 'readyQueue' is cleared for the next time a new
                        // process to run is determined
                        readyQueue.remove(0);
                    }

                    // If multiple processes have the same arrival time and
                    // remain in 'readyQueue', the process to start running is
                    // then determined by the explicit priority values each
                    // process contains
                    else if (readyQueue.size() > 1)
                    {
                        // The highest priority value is initially set to that
                        // of the first process in 'readyQueue'
                        maxPriority = readyQueue.get(0).priority;

                        // Each process in 'readyQueue' is examined to see if
                        // it has a higher priority value
                        for (int index = 0; index < readyQueue.size(); index++)
                        {
                            // If a process has a lower priority value, its
                            // index in 'readyQueue' is stored in
                            // 'passedProcessIndexes' for later removal from
                            // 'readyQueue'
                            if (maxPriority > readyQueue.get(index).priority)
                            {
                                passedProcessIndexes.add(index);
                            }

                            // If a process has a higher priority value, its
                            // priority is marked as the new maximum, and the
                            // indexes of all other processes before it in
                            // 'readyQueue' are stored in 'passedProcessIndexes'
                            else if (maxPriority < readyQueue.get(index).priority)
                            {
                                maxPriority = readyQueue.get(index).priority;

                                for (int passedIndex = index - 1; passedIndex >= 0; passedIndex--)
                                {
                                    // To avoid duplicates, an index is not
                                    // stored in 'passedProcessIndexes' if it
                                    // is already present
                                    if (passedProcessIndexes.contains(passedIndex) == false)
                                    {
                                        passedProcessIndexes.add(passedIndex);
                                    }
                                }
                            }
                        }

                        // The indexes in 'passedProcessIndexes' are sorted in
                        // ascending order such that their corresponding
                        // entries in 'readyQueue' can be removed sequentially
                        passedProcessIndexes.sort(null);

                        // For each index in 'passedProcessIndexes', the
                        // corresponding entry in 'readyQueue' is removed
                        for (int index : passedProcessIndexes)
                        {
                            // As each removal from 'readyQueue' causes the
                            // indexes in 'passedProcessIndexes' to refer to
                            // the entry one more index value ahead, 'loopNum'
                            // is incremeted after each removal to compensate
                            readyQueue.remove(index - loopNum);

                            loopNum++;
                        }

                        // 'loopNum' is reset and 'passedProcessIndexes' is
                        // cleared for the next time a new process to run is
                        // determined
                        loopNum = 0;

                        passedProcessIndexes.clear();

                        // The PID of the first remaining process in
                        // 'readyQueue' is marked as that of the currently
                        // running process
                        runningPID = readyQueue.get(0).pid;

                        // A new entry in 'processTimes' is made for the
                        // process, listing the starting time as the current
                        // time
                        processTimes.add(new ProcessInfo(readyQueue.get(0).pid, timer, -1));

                        // 'readyQueue' is cleared for the next time a new
                        // process to run is determined
                        readyQueue.clear();
                    }
                }
            }

            // 'timer' is incremented, signifying the beginning of the next
            // cycle
            timer++;
        }

        // Once all processes have finished running, the function will display
        // information about them, starting with a Gantt chart showcasing the
        // execution order
        System.out.print("|");

        // The top of the chart displays the number of each process
        for (ProcessInfo processTime : processTimes)
        {
            System.out.print(" P" + processTime.pid + " |");
        }

        // The bottom chart displays the start and end times of each process
        System.out.print("\n" + processTimes.get(0).startTime);

        // After the start time of the first process is displayed, the function
        // will display the end times of each process
        for (ProcessInfo processTime : processTimes)
        {
            System.out.print("   ");

            // Times with two digits have one less space in front of them in
            // order to maintain alignment
            if (processTime.startTime < 10)
            {
                System.out.print(" ");
            }

            System.out.print(processTime.endTime);
        }

        System.out.println("");

        System.out.println("");

        // After the Gantt chart, the function will display the waiting time,
        // turnaround time, and CPU utilization of each process
        for (ProcessInfo processTime : processTimes)
        {
            System.out.println("Process " + processTime.pid);

            for (Process foundProcess : processArray)
            {
                if (foundProcess.pid == processTime.pid)
                {
                    // The waiting time is calculated as the difference between
                    // the process's start time and arrival time
                    System.out.println("Waiting Time: " +
                        (processTime.startTime - foundProcess.arrivalTime));

                    // The process's waiting time is added to an aggregate
                    // waiting time
                    avgWaitTime += processTime.startTime - foundProcess.arrivalTime;

                    // The turnaround time is calculated as the difference
                    // between the process's end time and arrival time
                    System.out.println("Turnaround Time: " +
                        (processTime.endTime - foundProcess.arrivalTime));

                    // The process's turnaround time is added to an aggregate
                    // turnaround time
                    avgTurnTime += processTime.endTime - foundProcess.arrivalTime;

                    // The CPU utilization is calculated as the percentage of
                    // the turnaround time taken up by the total CPU time,
                    // which is the difference between the process's end time
                    // and start time
                    System.out.println("CPU Utilization: " +
                        ((int) ((float) (processTime.endTime - processTime.startTime) /
                        (processTime.endTime - foundProcess.arrivalTime) * 100)) + "%");

                    System.out.println("");

                    break;
                }
            }
        }

        // The aggregate waiting time is divided by the number of processes to
        // determine the average waiting time, which is then displayed
        avgWaitTime /= processArray.length;

        System.out.println("Average Waiting Time: " + avgWaitTime);

        // The aggregate turnaround time is divided by the number of processes
        // to determine the average turnaround time, which is then displayed
        avgTurnTime /= processArray.length;

        System.out.println("Average Turnaround Time: " + avgTurnTime);
    }

    // The 'sjf' function takes an array containing Process objects as input,
    // and outputs how the corresponding processes would run if a Shortest Job
    // First scheduling algorithm was used
    public static void sjf(Process[] processArray)
    {
        // The 'timer' variable keeps track of the current time in CPU units as
        // processes run
        int timer = 0;

        // The 'readyQueue' ArrayList contains processes that have arrived and
        // are ready to run
        ArrayList<Process> readyQueue = new ArrayList<Process>();

        // The 'passedProcessesIndexes' ArrayList contains the indexes of
        // processes that don't have the highest priority and won't start
        // running until a later time
        ArrayList<Integer> passedProcessIndexes = new ArrayList<Integer>();

        // The 'doneProcesses' ArrayList contains processes that have finished
        // running
        ArrayList<Process> doneProcesses = new ArrayList<Process>();

        // The 'processTimes' ArrayList contains information about the
        // processes' start and end times
        ArrayList<ProcessInfo> processTimes = new ArrayList<ProcessInfo>();

        // The 'runningPID' variable tracks the PID of the currently running
        // process
        // A value of -1 indicates that no process is currently running
        int runningPID = -1;

        // The 'minBurstTime' variable tracks the shortest burst time when
        // determining the next process to run
        int minBurstTime = 0;

        // The 'maxPriority' variable tracks the largest explicit priority
        // value when determining the next process to run
        int maxPriority = 0;

        // The 'loopNum' variable tracks the number of loop cycles when
        // clearing the 'readyQueue' ArrayList
        int loopNum = 0;

        // The 'avgWaitTime' variable is used for calculating the average
        // waiting time of each process
        float avgWaitTime = 0.0f;

        // The 'avgTurnTime' variable is used for calculating the average
        // turnaround time of each process
        float avgTurnTime = 0.0f;

        // The 'done' variable is used to track whether each process has
        // finished running
        boolean done = true;

        while(true)
        {
            // At the start of each cycle, 'done' is set to 'true', and is only
            // set to 'false' if there is at least one process that has yet to
            // finish running
            done = true;

            // Entries in 'processTimes' are generated when a process starts
            // running, so there being less entries in 'processTimes' than
            // 'processArray' indicates that at least one process has not
            // started - and thus not finished - running
            if (processArray.length <= processTimes.size())
            {
                // Entries in 'processTimes' are generated with 'endTime' set
                // to -1, and are updated once a process finishes, so a process
                // whose end time is set to this value has yet to finish
                // running
                for (ProcessInfo times : processTimes)
                {
                    if (times.endTime == -1)
                    {
                        done = false;

                        break;
                    }
                }
            }

            else
            {
                done = false;
            }

            // If all processes have finished running, the cycle breaks
            if (done == true)
            {
                break;
            }

            // If a process is currently running, it's burst time is checked
            // each cycle to see if the process is finished yet
            for (int index = 0; index < processArray.length; index++)
            {
                // A process is found to be running if its unique PID matches
                // up with 'runningPID'
                if (runningPID == processArray[index].pid)
                {
                    // If the process's listed burst time isn't greater than
                    // the difference between the current time and the time the
                    // process started running, the process has finished
                    for (ProcessInfo times : processTimes)
                    {
                        if ((processArray[index].pid == times.pid) &&
                            (processArray[index].burstTime <= (timer - times.startTime)))
                        {
                            // With the running process now finished,
                            // 'runningPID' is reset to its default value
                            runningPID = -1;

                            // The time at which the process finished is
                            // recorded in its corresponding 'processTimes' entry
                            times.endTime = timer;

                            // The finished process is added to
                            // 'doneProcesses', signifying it as finished
                            doneProcesses.add(processArray[index]);

                            break;
                        }
                    }
                    
                    break;
                }
            }

            // The function will begin looking for a new process to run if no
            // process is currently running
            if (runningPID == -1)
            {
                for (int index = 0; index < processArray.length; index++)
                {
                    // Each process that was set to arrive by the current cycle
                    // and hasn't already finished running is added to
                    // 'readyQueue'
                    if ((processArray[index].arrivalTime <= timer) &&
                        (doneProcesses.contains(processArray[index]) == false))
                    {
                        readyQueue.add(processArray[index]);
                    }
                }

                // If only one process is added to 'readyQueue', that process
                // begins running
                if (readyQueue.size() == 1)
                {
                    // The PID of the selected process is marked as that of the
                    // currently running process
                    runningPID = readyQueue.get(0).pid;

                    // A new entry in 'processTimes' is made for the process,
                    // listing the starting time as the current time
                    processTimes.add(new ProcessInfo(readyQueue.get(0).pid, timer, -1));

                    // 'readyQueue' is cleared for the next time a new process
                    // to run is determined
                    readyQueue.remove(0);
                }

                // If more than one process is added to 'readyQueue', the
                // process to start running is determined by which process
                // has a shorter burst time
                else if (readyQueue.size() > 1)
                {
                    // The shortest burst time is initially set to that of
                    // the first process in 'readyQueue'
                    minBurstTime = readyQueue.get(0).burstTime;

                    // Each process in 'readyQueue' is examined to see if it
                    // has a shorter burst time
                    for (int index = 0; index < readyQueue.size(); index++)
                    {
                        // If a process has a longer burst time, its index in
                        // 'readyQueue' is stored in 'passedProcessIndexes' for
                        // later removal from 'readyQueue'
                        if (minBurstTime < readyQueue.get(index).burstTime)
                        {
                            passedProcessIndexes.add(index);
                        }

                        // If a process has a shorter burst time, its burst
                        // time is marked as the new minimum, and the indexes
                        // of all other processes before it in 'readyQueue' are
                        // stored in 'passedProcessIndexes'
                        else if (minBurstTime > readyQueue.get(index).burstTime)
                        {
                            minBurstTime = readyQueue.get(index).burstTime;

                            for (int passedIndex = index - 1; passedIndex >= 0; passedIndex--)
                            {
                                // To avoid duplicates, an index is not stored
                                // in 'passedProcessIndexes' if it is already
                                // present
                                if (passedProcessIndexes.contains(passedIndex) == false)
                                {
                                    passedProcessIndexes.add(passedIndex);
                                }
                            }
                        }
                    }

                    // The indexes in 'passedProcessIndexes' are sorted in
                    // ascending order such that their corresponding entries in
                    // 'readyQueue' can be removed sequentially
                    passedProcessIndexes.sort(null);

                    // For each index in 'passedProcessIndexes', the
                    // corresponding entry in 'readyQueue' is removed
                    for (int index : passedProcessIndexes)
                    {
                        // As each removal from 'readyQueue' causes the indexes
                        // in 'passedProcessIndexes' to refer to the entry one
                        // more index value ahead, 'loopNum' is incremeted
                        // after each removal to compensate
                        readyQueue.remove(index - loopNum);

                        loopNum++;
                    }

                    // 'loopNum' is reset and 'passedProcessIndexes' is cleared
                    // for the next time a new process to run is determined
                    loopNum = 0;

                    passedProcessIndexes.clear();

                    // If only one process remains in 'readyQueue', that
                    // process begins running
                    if (readyQueue.size() == 1)
                    {
                        // The PID of the selected process is marked as that of
                        // the currently running process
                        runningPID = readyQueue.get(0).pid;

                        // A new entry in 'processTimes' is made for the
                        // process, listing the starting time as the current
                        // time
                        processTimes.add(new ProcessInfo(readyQueue.get(0).pid, timer, -1));

                        // 'readyQueue' is cleared for the next time a new
                        // process to run is determined
                        readyQueue.remove(0);
                    }

                    // If multiple processes have the same burst time and
                    // remain in 'readyQueue', the process to start running is
                    // then determined by the explicit priority values each
                    // process contains
                    else if (readyQueue.size() > 1)
                    {
                        // The highest priority value is initially set to that
                        // of the first process in 'readyQueue'
                        maxPriority = readyQueue.get(0).priority;

                        // Each process in 'readyQueue' is examined to see if
                        // it has a higher priority value
                        for (int index = 0; index < readyQueue.size(); index++)
                        {
                            // If a process has a lower priority value, its
                            // index in 'readyQueue' is stored in
                            // 'passedProcessIndexes' for later removal from
                            // 'readyQueue'
                            if (maxPriority > readyQueue.get(index).priority)
                            {
                                passedProcessIndexes.add(index);
                            }

                            // If a process has a higher priority value, its
                            // priority is marked as the new maximum, and the
                            // indexes of all other processes before it in
                            // 'readyQueue' are stored in 'passedProcessIndexes'
                            else if (maxPriority < readyQueue.get(index).priority)
                            {
                                maxPriority = readyQueue.get(index).priority;

                                for (int passedIndex = index - 1; passedIndex >= 0; passedIndex--)
                                {
                                    // To avoid duplicates, an index is not
                                    // stored in 'passedProcessIndexes' if it
                                    // is already present
                                    if (passedProcessIndexes.contains(passedIndex) == false)
                                    {
                                        passedProcessIndexes.add(passedIndex);
                                    }
                                }
                            }
                        }

                        // The indexes in 'passedProcessIndexes' are sorted in
                        // ascending order such that their corresponding
                        // entries in 'readyQueue' can be removed sequentially
                        passedProcessIndexes.sort(null);

                        // For each index in 'passedProcessIndexes', the
                        // corresponding entry in 'readyQueue' is removed
                        for (int index : passedProcessIndexes)
                        {
                            // As each removal from 'readyQueue' causes the
                            // indexes in 'passedProcessIndexes' to refer to
                            // the entry one more index value ahead, 'loopNum'
                            // is incremeted after each removal to compensate
                            readyQueue.remove(index - loopNum);

                            loopNum++;
                        }

                        // 'loopNum' is reset and 'passedProcessIndexes' is
                        // cleared for the next time a new process to run is
                        // determined
                        loopNum = 0;

                        passedProcessIndexes.clear();

                        // The PID of the first remaining process in
                        // 'readyQueue' is marked as that of the currently
                        // running process
                        runningPID = readyQueue.get(0).pid;

                        // A new entry in 'processTimes' is made for the
                        // process, listing the starting time as the current
                        // time
                        processTimes.add(new ProcessInfo(readyQueue.get(0).pid, timer, -1));

                        // 'readyQueue' is cleared for the next time a new
                        // process to run is determined
                        readyQueue.clear();
                    }
                }
            }

            // 'timer' is incremented, signifying the beginning of the next
            // cycle
            timer++;
        }

        // Once all processes have finished running, the function will display
        // information about them, starting with a Gantt chart showcasing the
        // execution order
        System.out.print("|");

        // The top of the chart displays the number of each process
        for (ProcessInfo processTime : processTimes)
        {
            System.out.print(" P" + processTime.pid + " |");
        }

        // The bottom chart displays the start and end times of each process
        System.out.print("\n" + processTimes.get(0).startTime);

        // After the start time of the first process is displayed, the function
        // will display the end times of each process
        for (ProcessInfo processTime : processTimes)
        {
            System.out.print("   ");

            // Times with two digits have one less space in front of them in
            // order to maintain alignment
            if (processTime.startTime < 10)
            {
                System.out.print(" ");
            }

            System.out.print(processTime.endTime);
        }

        System.out.println("");

        System.out.println("");

        // After the Gantt chart, the function will display the waiting time,
        // turnaround time, and CPU utilization of each process
        for (ProcessInfo processTime : processTimes)
        {
            System.out.println("Process " + processTime.pid);

            for (Process foundProcess : processArray)
            {
                if (foundProcess.pid == processTime.pid)
                {
                    // The waiting time is calculated as the difference between
                    // the process's start time and arrival time
                    System.out.println("Waiting Time: " +
                        (processTime.startTime - foundProcess.arrivalTime));

                    // The process's waiting time is added to an aggregate
                    // waiting time
                    avgWaitTime += processTime.startTime - foundProcess.arrivalTime;

                    // The turnaround time is calculated as the difference
                    // between the process's end time and arrival time
                    System.out.println("Turnaround Time: " +
                        (processTime.endTime - foundProcess.arrivalTime));

                    // The process's turnaround time is added to an aggregate
                    // turnaround time
                    avgTurnTime += processTime.endTime - foundProcess.arrivalTime;

                    // The CPU utilization is calculated as the percentage of
                    // the turnaround time taken up by the total CPU time,
                    // which is the difference between the process's end time
                    // and start time
                    System.out.println("CPU Utilization: " +
                        ((int) ((float) (processTime.endTime - processTime.startTime) /
                        (processTime.endTime - foundProcess.arrivalTime) * 100)) + "%");

                    System.out.println("");

                    break;
                }
            }
        }

        // The aggregate waiting time is divided by the number of processes to
        // determine the average waiting time, which is then displayed
        avgWaitTime /= processArray.length;

        System.out.println("Average Waiting Time: " + avgWaitTime);

        // The aggregate turnaround time is divided by the number of processes
        // to determine the average turnaround time, which is then displayed
        avgTurnTime /= processArray.length;

        System.out.println("Average Turnaround Time: " + avgTurnTime);
    }
    
    public static void main(String[] args)
    {
        // Creates a Scanner object that reads user input
        Scanner userInput = new Scanner(System.in);

        // Creates a string variable to contain the user's selection
        String selection = "";

        // Asks the user for the path to the file
        System.out.print("Enter filepath: ");

        // Creates an array of Process objects based on the given file
        Process[] processArray = read_file(userInput.nextLine());

        // If Process objects were sucessfully generates, the program asks the
        // user for which scheduling algorithm to use is
        if (processArray.length > 0)
        {
            System.out.println("Select scheduling algorithm to use:");
            System.out.println("1: First-Come, First-Served");
            System.out.println("2: Shortest Job First");

            selection = userInput.nextLine();

            // If option 1 is selected, First-Come, First-Served is used
            if (selection.equals("1"))
            {
                fcfs(processArray);
            }

            // If option 2 is selected, Shortest Job First is used
            else if (selection.equals("2"))
            {
                sjf(processArray);
            }
            
            // If nether option is selected, an error message is printed
            else
            {
                System.out.println("Invalid input");
            }
        }
    }
}
