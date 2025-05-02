// Importing the "Semaphore" class, which will be used for controlling when
// threads can access the bounded buffer
import java.util.concurrent.Semaphore;

// A class used for defining the threads to be ran and the data they will access
public class ProcessThread extends Thread
    {
        // Initializes the variables used to stored each thread's PID, CPU
        // burst length, time of arrival, and priority
        public int pid, burstTime, arrivalTime, priority;

        // Initializes the variable used to track whether a thread is a
        // producer thread or consumer thread
        // A value of 'false' indicates a consumer thread
        public boolean isProducer;

        // Initializes the list of characters that producers will generate data
        // with
        public static char[] charList = {'q', 'w', 'e', 'r', 't', 'y', 'u',
            'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z',
            'x', 'c', 'v', 'b', 'n', 'm'};

        // Initializes the buffer that the thread will access and modify
        public static String[] buffer = {"", "", "", "", ""};

        // Initializes the 'Semaphore' object that controls the number of
        // consumer threads that can read from the buffer at a time
        // Being initialized with one permit allows only one thread to read
        // from the buffer at a time
        public static Semaphore canRead = new Semaphore(1);

        // Initializes the 'Semaphore' object that controls the number of
        // producer threads that can write to the buffer at a time
        // Being initialized with one permit allows only one thread to write to
        // the buffer at a time
        public static Semaphore canWrite = new Semaphore(1);

        // Initializes the 'Semaphore' object that indicates how many fields in
        // the buffer contain data
        // Producer threads can't write to the buffer if all fields are full
        public static Semaphore full = new Semaphore(0);

        // Initializes the 'Semaphore' object that indicates how many fields in
        // the buffer do not contain data
        // Consumer threads can't read from the buffer if all fields are empty
        public static Semaphore empty = new Semaphore(buffer.length);

        // Intitializes the variables that will point to the next index in the
        // buffer that producers should write to and that consumers should read
        // from, respectively
        public static int next_in, next_out = 0;

        // Initializes the variable that will contain the data that producer
        // threads will write to the buffer
        public static String writeString = "";

        // Initializes the variable that will contain the data that consumer
        // threads will read from the buffer
        public static String readString = "";

        // Initializes the variable that will be used for formatting thread
        // information display in the console
        public String spacing = "";
        
        // Constructor for ProcessThread objects
        public ProcessThread(int pid, int arrivalTime, int burstTime, int priority, int isProducer)
        {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;

            // If the integer value given for the "isProducer" parameter equals
            // '1', the boolean value for the corresponding class attribute is
            // set to 'true'
            // Otherwise, it is set to 'false'
            if (isProducer == 1)
            {
                this.isProducer = true;
            }

            else
            {
                this.isProducer = false;
            }

            // If the created thread's PID is a single digit value, an extra
            // space will be added when formatting thread information display
            //Otherwise, no space is added
            if ((this.pid < 10) && (this.pid > -1))
            {
                this.spacing = " ";
            }

            else
            {
                this.spacing = "";
            }
        }
        
        // The function that define's thread behavoior upon startup
        public void run()
        {
            // The priority of the thread is set to the priority of the
            // corresponding "ProcessThread" object
            this.setPriority(this.priority);

            // The thread is put to sleep to simulate the time it takes to
            // arrive
            try
            {
                Thread.sleep(this.arrivalTime * 1000);
            }
            
            catch (InterruptedException e) {}

            System.out.println("[Process " + this.spacing + this.pid +
                "] Arrived");

            // If the thread is a producer thread, it will prepare data to be
            // written to the buffer
            if (this.isProducer == true)
            {
                // To keep only one producer thread writing to the buffer at
                // a time, the thread acquires a permit from the "canWrite"
                // semaphore
                // If no permit is available, the thread will block until it's
                // available again
                try
                {
                    System.out.println("[Process " + this.spacing +  this.pid +
                        "] Waiting to write to buffer...");

                    ProcessThread.canWrite.acquire();
                }

                catch (InterruptedException e) {}

                // To generate data, the thread randomly selects 5 characters
                // from the "charList" array and combines them into a string
                for (int i = 0; i < 5; i++)
                {
                    ProcessThread.writeString = ProcessThread.writeString +
                        ProcessThread.charList[(int)(Math.random() *
                        ProcessThread.charList.length)];
                }

                // To ensure producer threads don't write in buffer fields
                // already containing data, the thread acquires a permit from
                // the "empty" semaphore
                // If no permit is available, the thread will block until one
                // is available again
                try
                {
                    System.out.println("[Process " + this.spacing + this.pid +
                        "] Waiting for free space to write...");

                    ProcessThread.empty.acquire();
                }

                catch (InterruptedException e) {}

                System.out.println("[Process " + this.spacing + this.pid +
                    "] Writing to buffer...");
            }

            // If the thread is a producer thread, it will prepare to read data
            // from the buffer
            else
            {
                // To keep only one consumer thread reading from the buffer at
                // a time, the thread acquires a permit from the "canRead"
                // semaphore
                // If no permit is available, the thread will block until it's
                // available again
                try
                {
                    System.out.println("[Process " + this.spacing + this.pid +
                        "] Waiting to read from buffer...");

                    ProcessThread.canRead.acquire();
                }

                catch (InterruptedException e) {}

                // To ensure consumer threads don't read from buffer fields
                // that don't contain data, the thread acquires a permit from
                // the "full" semaphore
                // If no permit is available, the thread will block until one
                // is available again
                try
                {
                    System.out.println("[Process " + this.spacing + this.pid +
                        "] Waiting for data to read...");

                    ProcessThread.full.acquire();
                }

                catch (InterruptedException e) {}

                System.out.println("[Process " + this.spacing + this.pid +
                    "] Reading from buffer...");
            }

            // The thread is put to sleep to simulate its CPU time
            try
            {
                Thread.sleep(this.burstTime * 1000);
            }
            
            catch (InterruptedException e) {}

            // If the thread is a producer thread, the data the thread
            // generated is written to the buffer
            if (this.isProducer == true)
            {
                // Writes to the buffer at the index pointed at by "next_in"
                ProcessThread.buffer[ProcessThread.next_in] = ProcessThread.writeString;

                // If "next_in" is at the end of the buffer, it is moved to
                // the start for the next producer thread
                // Otherwise, "next_in" is incremented
                if (ProcessThread.next_in >= (buffer.length - 1))
                {
                    ProcessThread.next_in = 0;
                }

                else
                {
                    ProcessThread.next_in++;
                }

                // Resets "writeString" for the next producer thread
                ProcessThread.writeString = "";

                System.out.println("[Process " + this.spacing + this.pid +
                    "] Finished writing to buffer");

                // To allow the next producer thread to begin writing data, the
                // thread releases a permit to the "canWrite" semaphore
                ProcessThread.canWrite.release();

                // To allow for any waiting consumer threads to begin reading
                // data, the thread releases a permit to the "full" semaphore
                ProcessThread.full.release();
            }

            // If the thread is a consumer thread, data is read from the buffer
            else
            {
                // Reads from the buffer at the index pointed at by "next_out"
                ProcessThread.readString = ProcessThread.buffer[ProcessThread.next_out];

                // If "next_out" is at the end of the buffer, it is moved to
                // the start for the next consumer thread
                // Otherwise, "next_out" is incremented
                if (ProcessThread.next_out >= (buffer.length - 1))
                {
                    ProcessThread.next_out = 0;
                }

                else
                {
                    ProcessThread.next_out++;
                }

                // Prints the data read to the console
                System.out.println("[Process " + this.spacing + this.pid +
                    "] Read " + ProcessThread.readString + " from buffer");

                // To allow the next consumer thread to begin reading data, the
                // thread releases a permit to the "canRead" semaphore
                ProcessThread.canRead.release();

                // To allow for any waiting producer threads to begin writing
                // data, the thread releases a permit to the "empty" semaphore
                ProcessThread.empty.release();
            }
            
            System.out.println("[Process " + this.spacing + this.pid +
            "] Finished");
        }
    }