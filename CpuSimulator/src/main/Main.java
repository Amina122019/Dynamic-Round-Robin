package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Initialize lists to store jobs and events time
        List<Jobs> jobs = new ArrayList<>();
        List<Integer> eventsTime = new ArrayList<>();

        // Create an instance of SchedulerSimulator
        SchedulerSimulator schedulerSimulator = new SchedulerSimulator();

        // Set the file path for input
        String filepath = "input3.txt";
        File inputFile = new File(filepath);

        // Check if the file exists
        if (!inputFile.exists()) {
            System.out.println("File not found: " + filepath);
            return; // Exit the program if file not found
        }

        try {
            // Create a scanner to read the input file
            Scanner scanner = new Scanner(inputFile);

            // Flags to track if jobs and events have been added
            boolean jobsAdded = false;
            boolean eventsAdded = false;
            int countNewLine = 0;
            int counterLines = 1 ;            
            // Read the input file line by line
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
               if(line.isEmpty()) break;
               
                String[] parts = line.split(" ");
                
                // Get the command from the first part of the line
                char command = parts[0].charAt(0);

                // If the command is 'C', reset the simulator and clear the lists
                if (Objects.equals(command, 'C')) {
                    if (jobsAdded || eventsAdded) {
                        schedulerSimulator.processJobArrival(jobs, eventsTime);
                        System.out.println("\n");
                        schedulerSimulator.activeQueue.clear();
                        schedulerSimulator.queueSize = 0;
                        schedulerSimulator.readyQueue.clear();
                        schedulerSimulator.holdQueues1.clear();
                        schedulerSimulator.holdQueues2.clear();
                        eventsTime.clear();
                        jobs.clear();
                    }
                }

                // Process the command
                switch (command) {
                    case 'C':
                        // Configure the system
                        int currentTime = Integer.parseInt(parts[1]);
                        int mainMemory = Integer.parseInt(parts[2].split("=")[1]);
                        int devices = Integer.parseInt(parts[3].split("=")[1]);
                        schedulerSimulator.configureSystem(currentTime, mainMemory, devices);
                        break;
                    case 'A':
                        // Add a job
                        Jobs jobs1 = getJobs(parts);
                        jobs.add(jobs1);
                        jobsAdded = true;
                        break;
                    case 'D':
                        // Add an event time
                        int displayTime = Integer.parseInt(parts[1]);
                        eventsTime.add(displayTime);
                        eventsAdded = true;
                        break;
                    default:
                        System.err.println("Error occurred");
                        break;
                }
            }

            // Process the job arrival if jobs or events have been added
            if (jobsAdded || eventsAdded) {
                schedulerSimulator.processJobArrival(jobs, eventsTime);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a Jobs object from the given parts
     *
     * @param parts the parts of the line
     * @return a Jobs object
     */
    private static Jobs getJobs(String[] parts) {
        int arrivalTime = Integer.parseInt(parts[1]);
        int jobId = Integer.parseInt(parts[2].split("=")[1]);
        int memory = Integer.parseInt(parts[3].split("=")[1]);
        int numDevices = Integer.parseInt(parts[4].split("=")[1]);
        int serviceTime = Integer.parseInt(parts[5].split("=")[1]);
        int priority = Integer.parseInt(parts[6].split("=")[1]);
        return new Jobs(jobId, memory, numDevices, serviceTime, priority, arrivalTime);
    }
}
