/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author mac
 */
public class Process {
    // The job associated with this process
    private Jobs jobs;
    // The remaining service time for this process
    private int remainingServiceTime;

    /**
     * Constructor to create a new process with a job
     *
     * @param jobs the job associated with this process
     */
    public Process(Jobs jobs) {
        this.jobs = jobs;
        this.remainingServiceTime = jobs.getServiceTime();
    }

    /**
     * Default constructor
     */
    public Process() {
    }

    /**
     * Set the job associated with this process
     *
     * @param jobs the job to set
     */
    public void setJobs(Jobs jobs) {
        this.jobs = jobs;
    }

    /**
     * Get the job associated with this process
     *
     * @return the job associated with this process
     */
    public Jobs getJobs() {
        return jobs;
    }

    /**
     * Execute the process for a given time quantum
     *
     * @param timeQuantum the time quantum to execute for
     * @return the remaining service time for this process
     */
    public int execute(int timeQuantum) {
        if (remainingServiceTime <= timeQuantum) {
            // If the remaining service time is less than or equal to the time quantum,
            // set the remaining service time to 0 and return the original remaining service time
            int remainingTime = remainingServiceTime;
            remainingServiceTime = 0;
            return remainingTime;
        } else {
            // Otherwise, subtract the time quantum from the remaining service time
            remainingServiceTime -= timeQuantum;
            return remainingServiceTime;
        }
    }

    public int getRemainingServiceTime() {
        return remainingServiceTime;
    }

    public void setRemainingServiceTime(int remainingServiceTime) {
        this.remainingServiceTime = remainingServiceTime;
    }
    
    
}