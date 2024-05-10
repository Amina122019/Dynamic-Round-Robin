package main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Iterator;
import java.util.LinkedList;

public class Queue implements Iterable<Process>{
    private final LinkedList<Process> processes;

    public Queue() {
        this.processes = new LinkedList<>();
    }
    public void enqueue(Process process) {
        processes.addLast(process);
    }
    public void enqueueForHoldQueue1(Process process) {
        int index = 0;
        while (index < processes.size() && processes.get(index).getJobs().getMemory() < process.getJobs().getMemory()) {
            index++;
        }
        processes.add(index, process);
    }
    public void enqueueForHoldQueue2(Process process) {
        processes.addLast(process);
    }

    public Process dequeue() {
        return processes.pollFirst();
    }
    public Process dequeueLast(){
        return processes.pollLast();
    }
    public Process peekLast() {
        return processes.peekLast();
    }
    public Process peekFirst(){
        return processes.peekFirst();
    }

    public boolean isEmpty() {
        return processes.isEmpty();
    }


    public int size() {
        return processes.size();
    }

    public Process poll() {
        return processes.poll();
    }
    public boolean offer(Process process){
        return processes.offer(process);
    }

    public void remove(Process process) {
        processes.remove(process);
    }
    public void clear(){
        processes.clear();
    }
    @Override
    public Iterator<Process> iterator() {
        return processes.iterator();
    }
}