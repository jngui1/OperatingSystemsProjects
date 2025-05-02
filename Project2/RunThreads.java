// Importing the 'File' class, which will be used for reading the file
// containing process information
import java.io.File;

// Importing 'FileNotFountException', which will be raised if the specified
// file cannot be found
import java.io.FileNotFoundException;

// Importing the 'Scanner' class, which will be used for reading the file
// containing thread information
import java.util.Scanner;

// Importing the 'ArrayList' class, which will be used for creating arrays that
// can added to and deleted from
import java.util.ArrayList;

public class RunThreads
{
    public static ProcessThread[] readThreadFile(String filepath)
    {
        // The 'returnList' ArrayList will contain each of the ProcessThread objects
        ArrayList<ProcessThread> returnList = new ArrayList<ProcessThread>();

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
                (fileReader.next().equals("Priority")) &&
                (fileReader.next().equals("Is_Producer")))
            {
                // For each row of information in the file, a new ProcessThread object
                // is created with values for the PID, arrival time, burst
                // time, and priority
                while (fileReader.hasNextInt())
                {
                    returnList.add(new ProcessThread(fileReader.nextInt(),
                        fileReader.nextInt(), fileReader.nextInt(),
                        fileReader.nextInt(), fileReader.nextInt()));
                }
            }

            fileReader.close();

            // Returns the list of Process objects as a traditional array
            //rather than as an ArrayList
            return returnList.toArray(new ProcessThread[returnList.size()-1]);
        }

        // If a file is not found at the specified filepath, an error message
        // is printed
        catch (FileNotFoundException e)
        {
            System.out.println("File not found.");
        }

        // In the event of an invalid filepath being given, an empty array is
        // returned
        return new ProcessThread[0];
    }
    

    public static void main(String[] args)
    {
        // Creates a Scanner object that reads user input
        Scanner userInput = new Scanner(System.in);

        // Creates a string variable to contain the user's selection
        String selection = "";

        // Asks the user for the path to the file
        System.out.print("Enter filepath: ");

        // Creates an array of ProcessThread objects based on the given file
        ProcessThread[] threadArray = readThreadFile(userInput.nextLine());

        // Starts the threads based on the data in the file
        for (int i = 0; i < threadArray.length; i++)
        {
            threadArray[i].start();
        }
    }
}
