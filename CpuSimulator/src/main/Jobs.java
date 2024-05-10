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
public class Jobs {
    private int jobId;
    private int memory;
    private int numDevices;
    private int serviceTime;
    private int priority;
    private int arrivalTime;
    private int finishTime;

    public Jobs(int jobId, int memory, int numDevices, int serviceTime, int priority, int arrivalTime) {
        this.jobId = jobId;
        this.memory = memory;
        this.numDevices = numDevices;
        this.serviceTime = serviceTime;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.finishTime = 0;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getJobId() {
        return jobId;
    }

    public int getMemory() {
        return memory;
    }

    public int getNumDevices() {
        return numDevices;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getFinishTime() {
        return finishTime;
    }
}
