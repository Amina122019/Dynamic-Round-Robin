package main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mac
 */
import java.util.ArrayList;
import java.util.List;
import main.Jobs;

public class SchedulerSimulator {

    // System time
    private int systemTime;
    // Main memory
    private int mainMemory;
    // Devices
    private int devices;
    // Available main memory
    private int mainMemoryAvailable;
    // Available devices
    private int devicesAvailable;
    // Hold queues
    public final Queue holdQueues1;
    public final Queue holdQueues2;
    // Complete queue
    public final Queue completeQueue;
    // Ready queue
    public final Queue readyQueue;
    // Active queue
    public final Queue activeQueue;
    // System turnaround time
    private double systemTurnaroundTime;
    // SR (not sure what this is)
    public int SR = 0;
    // Queue size
    public int queueSize = 0;

    public Process cpuProcess = null;
    public SchedulerSimulator() {
        // Initialize queues
        this.activeQueue = new Queue();
        this.mainMemoryAvailable = 0;
        this.devicesAvailable = 0;
        this.holdQueues1 = new Queue();
        this.holdQueues2 = new Queue();
        this.completeQueue = new Queue();
        this.readyQueue = new Queue();
    }

    /**
     * Configure the system
     *
     * @param time system time
     * @param mainMemory main memory
     * @param devices devices
     */
    public void configureSystem(int time, int mainMemory, int devices) {
        this.systemTime = time;
        this.mainMemory = mainMemory;
        this.devices = devices;
        this.mainMemoryAvailable = mainMemory;
        this.devicesAvailable = devices;
    }

    /**
     * Process job arrival
     *
     * @param jobsList list of jobs
     * @param eventsTime list of event times
     */
    public void processJobArrival(List<Jobs> jobsList, List<Integer> eventsTime) {
        int burstTime = 0;
        Process activeProcess = new Process();
        while (!jobsList.isEmpty() || !activeQueue.isEmpty() || !holdQueues1.isEmpty() || !holdQueues2.isEmpty() || !readyQueue.isEmpty()) {
            // Remove jobs that have arrived
            List<Jobs> jobsToRemove = new ArrayList<>();
            for (Jobs jobs : jobsList) {
                if (jobs.getArrivalTime() == systemTime) {
                    if (jobs.getMemory() > mainMemory || jobs.getNumDevices() > devices) {
                        System.out.println("Skipping process: " + jobs.getJobId());
                    } else if (jobs.getMemory() > mainMemoryAvailable || jobs.getNumDevices() > devicesAvailable) {
                        Process process = new Process(jobs);
                        if (jobs.getPriority() == 1) {
                            holdQueues1.enqueueForHoldQueue1(process);
                        } else {
                            holdQueues2.enqueueForHoldQueue2(process);
                        }
                    } else {
                        Process process = new Process(jobs);
                        readyQueue.enqueue(process);
                    }

                    jobsToRemove.add(jobs);
                }
            }

            // Check if active queue is empty
            if (activeQueue.isEmpty()) {
                // Add process to active queue
                if (!readyQueue.isEmpty()) {
                    queueSize += readyQueue.size();
                    activeProcess = readyQueue.dequeue();
                    cpuProcess =activeProcess ;
                    activeQueue.enqueue(activeProcess);
                    mainMemoryAvailable = mainMemoryAvailable - activeProcess.getJobs().getMemory();
                    devicesAvailable = devicesAvailable - activeProcess.getJobs().getNumDevices();
                    burstTime = activeProcess.getJobs().getServiceTime();
                    int finish_time = dynamicRR(burstTime, activeProcess);
                    if (systemTime == finish_time) {
                        completeQueue.enqueue(activeProcess);
                        activeQueue.dequeue();
                        mainMemoryAvailable = mainMemoryAvailable + activeProcess.getJobs().getMemory();
                        devicesAvailable = devicesAvailable + activeProcess.getJobs().getNumDevices();
                        checkHoldQueues();
                    }
                }
            } else {
                // Process active process
                if (activeProcess.getJobs() == null) {
                    System.out.println("Active process is null");
                } else {
                    int finish_time = dynamicRR(burstTime, activeProcess);
                    if (systemTime == finish_time) {
                        completeQueue.enqueue(activeProcess);
                        activeQueue.dequeue();
                        mainMemoryAvailable = mainMemoryAvailable + activeProcess.getJobs().getMemory();
                        devicesAvailable = devicesAvailable + activeProcess.getJobs().getNumDevices();
                        checkHoldQueues();
                    }
                }
            }

            // Check for events
            for (Integer events : eventsTime) {
                if (events == systemTime) {
                    dumpTable();
                }
            }

            // Remove jobs that have been processed
            jobsList.removeAll(jobsToRemove);
            systemTime++;
        }

        // Display system status
        System.out.println("\n");
        displaySystemStatus(999999);
        readyQueue.clear();
        completeQueue.clear();
        holdQueues1.clear();
        holdQueues2.clear();
    }

    /**
     * Dynamic RR algorithm
     *
     * @param burstTime burst time
     * @param activeProcess active process
     * @return finish time
     */
    private int dynamicRR(int burstTime, Process activeProcess) {
        int TQ;
        int waitTime;
        SR += burstTime;
        // Average of burst times
        double AR = (double) SR / queueSize;
        if (queueSize == 1) {
            TQ = burstTime;
            waitTime = 0;
        } else {
            TQ = (int) AR;
            if (!completeQueue.isEmpty()) {
                waitTime = completeQueue.peekLast().getJobs().getFinishTime() - activeProcess.getJobs().getArrivalTime();
            } else {
                waitTime = 0;
            }
        }
        activeProcess.execute(TQ);
        int finish_time = (activeProcess.getJobs().getArrivalTime() + waitTime + activeProcess.getJobs().getServiceTime());
        activeProcess.getJobs().setFinishTime(finish_time);
        return finish_time;
    }

    /**
     * Check hold queues
     */
    public void checkHoldQueues() {
        // Check Hold Queue 1
        while (!holdQueues1.isEmpty() && holdQueues1.peekLast().getJobs().getMemory() <= mainMemoryAvailable) {
            Process process = holdQueues1.dequeueLast();
            readyQueue.enqueue(process);
            queueSize++;
        }

        // Check Hold Queue 2 (FIFO)
        while (!holdQueues2.isEmpty() && (holdQueues1.isEmpty() || holdQueues2.peekLast().getJobs().getMemory() <= mainMemoryAvailable)) {
            Process process = holdQueues2.dequeue();
            readyQueue.enqueue(process);
            queueSize++;
        }
    }

    /**
     * Dump table
     */
    private void dumpTable() {
        System.out.println("<< At time " + systemTime + ":");
        System.out.println("Current available Main Memory = " + mainMemoryAvailable);
        System.out.println("Current devices = " + devicesAvailable);
        System.out.print("\n");
        System.out.println("Completed jobs:");
        for (int i = 0; i < 20; i++) {
            System.out.print("-");
        }
        System.out.println();
        completeQueueDisplay();
        System.out.println("\n");
        System.out.println("Hold queue 1:");
        for (Process p : holdQueues1) {
            System.out.println(p.getJobs().getJobId());
        }
        System.out.println("\n");
        System.out.println("Hold queue 2:");
        for (Process p : holdQueues2) {
            System.out.println(p.getJobs().getJobId());
        }
        System.out.println("\n");
        System.out.println("Ready queue :");
        for (Process p : readyQueue) {
            System.out.println(p.getJobs().getJobId());
        }
        System.out.println("\n");
        System.out.println("Process running on CPU");
        System.out.println("Job ID \t  Run time\tTime left");
        if (cpuProcess != null) {
            System.out.printf("   %d %10d %11d\n", cpuProcess.getJobs().getJobId(), cpuProcess.getJobs().getServiceTime(), cpuProcess.getRemainingServiceTime());
        } else {
            System.out.println();
        }
    }

    /**
     * Display system status
     *
     * @param currentTime current time
     */
    private void display(int currentTime) {
        System.out.println("<< At time " + currentTime + ":");
        System.out.println("Current available Main Memory = " + mainMemoryAvailable);
        System.out.println("Current devices = " + devicesAvailable);
        System.out.println("Completed Jobs:");
        completeQueueDisplay();
    }

    /**
     * Display complete queue
     */
    private void completeQueueDisplay() {

        System.out.println("  Job ID   Burst Time  Arrival Time    Finish Time  Turnaround Time");
        for (int i = 0; i < 80; i++) {
            System.out.print("-");
        }
        System.out.println();
        for (Process process : completeQueue) {
            Jobs job = process.getJobs();
            int turnaroundTime = Math.max(0, job.getFinishTime() - job.getArrivalTime());
            System.out.printf(" %5d%10d%13d%15d%15d\n", job.getJobId(), job.getServiceTime(),  job.getArrivalTime(), job.getFinishTime(), turnaroundTime);
        }
    }

    /**
     * Display system status
     *
     * @param integer integer
     */
    public void displaySystemStatus(Integer integer) {
        display(integer);
        for (Process process : completeQueue) {
            Jobs job = process.getJobs();
            int turnaroundTime = Math.max(0, job.getFinishTime() - job.getArrivalTime());
            systemTurnaroundTime += turnaroundTime;
        }
        systemTurnaroundTime = systemTurnaroundTime / completeQueue.size();
        System.out.printf("System turnaround time: %.3f\n", systemTurnaroundTime);
    }
}
