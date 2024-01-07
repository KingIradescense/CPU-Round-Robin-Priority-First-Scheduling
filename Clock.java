import java.util.*;
import java.lang.*;
import java.io.*;

public class Clock {
    public Clock(int time_required, int arrival_time) { // takes required_time from the Program which instantiates a
                                                        // Clock to refer to as "total_working_time"
        this.total_working_time = time_required;
        this.total_elapsed_time = 0;
        this.total_waiting_time = 0;
        this.arrival_time = arrival_time;
    }

    // print results of simulation. these methods are only called once all programs
    // are terminated

    public double individual_CPU_utilization() { // get CPU efficiency for this specific Program -> average for the
                                                 // real CPU efficiency across all Programs
        total_elapsed_time = total_working_time + total_waiting_time;
        double fraction = total_working_time / total_elapsed_time;
        return fraction;
    }

    public void print_turnaround() {
        total_elapsed_time = total_waiting_time + total_working_time;
        System.out.println(total_elapsed_time);
    }

    public void print_waiting() {
        System.out.println(total_waiting_time);
    }

    // update functions

    public void update_total_elapsed_time(int time) {
        total_elapsed_time += time;
    }

    public void update_total_waiting_time(int time) {
        total_waiting_time += time;
    }

    public void update_total_working_time(int time) {
        total_working_time += time;
    }

    // getters & setters

    public void set_total_elapsed_time(int total_elapsed_time) {
        this.total_elapsed_time = total_elapsed_time;
    }

    public void set_total_waiting_time(int total_waiting_time) {
        this.total_waiting_time = total_waiting_time;
    }

    public void set_total_working_time(int total_working_time) {
        this.total_working_time = total_working_time;
    }

    public void set_arrival_time(int arrival_time) {
        this.arrival_time = arrival_time;
    }

    public int get_total_elapsed_time() {
        return total_elapsed_time;
    }

    public int get_total_waiting_time() {
        return total_waiting_time;
    }

    public int get_total_working_time() {
        return total_working_time;
    }

    public int get_arrival_time() {
        return arrival_time;
    }

    private int total_elapsed_time; // sum of total_waiting_time and time_required once a Program ends
    private int total_waiting_time; // total time spent waiting in queues
    private int total_working_time; // total time spent in CPU by the time the Program terminates
    private int arrival_time; // stores the arrival time of the Program which owns the Clock object. minimum
                              // time passed before Program in newin_list can queue into ready_list
}