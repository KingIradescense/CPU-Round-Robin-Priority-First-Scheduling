import java.util.*;
import java.lang.*;
import java.io.*;

public class Program { // Program abstraction from file lines
    public Program(int pid, int arrival_time, int time_required, int initial_priority) {
        this.pid = pid;
        this.initial_priority = initial_priority;
        starvation = 0;
        real_priority = initial_priority;
        this.clock = new Clock(time_required, arrival_time);
    }

    public Program() { // use only to instantiate Program swap (Scheduler line 156)
        pid = 0;
        initial_priority = 0;
        starvation = 0;
        time_waiting = 0;
        clock = new Clock(0, 0);
    }

    public void increase_priority() { // increase priority if a program is starving
        starvation += 1;
        calculate_priority();
    }

    public void decrease_priority() { // decrease priority if a previously starving program has reached the top
        // percentile of the queue to make sure it doesn't take priority over tasks
        // which
        if (starvation > 0) { // begin as high priority. doesn't allow a program to lose more priority than it
            // began with
            starvation -= 1;
            calculate_priority();
        }
    }

    private void calculate_priority() { // calculate the operable priority value if starvation updates
        real_priority = initial_priority + starvation;
    }

    public void update_total_elapsed_time(int update) {
        this.clock.update_total_elapsed_time(update);
    }

    // getters & setters
    public int get_pid() {
        return pid;
    }

    public int get_arrival_time() {
        return clock.get_arrival_time();
    }

    public int get_time_required() {
        return time_required;
    }

    public int get_total_time_required() {
        return clock.get_total_working_time();
    }

    public int get_initial_priority() {
        return initial_priority;
    }

    public int get_starvation() {
        return starvation;
    }

    public int get_real_priority() {
        return real_priority;
    }

    public int get_time_elapsed() {
        return time_elapsed;
    }

    public int get_waiting_time() {
        return clock.get_total_waiting_time();
    }

    public Clock get_clock() {
        return clock;
    }

    public void set_pid(int pid) {
        this.pid = pid;
    }

    public void set_arrival_time(int arrival_time) {
        clock.set_arrival_time(arrival_time);
    }

    public void set_time_required(int time_required) {
        this.time_required = time_required;
    }

    public void set_total_working_time(int time_required) {
        clock.set_total_working_time(time_required);
    }

    public void set_initial_priority(int initial_priority) {
        this.initial_priority = initial_priority;
    }

    public void set_starvation(int starvation) {
        this.starvation = starvation;
    }

    public void set_real_priority(int real_priority) {
        this.real_priority = real_priority;
    }

    public void set_time_elapsed(int time_elapsed) {
        this.time_elapsed = time_elapsed;
    }

    public void set_waiting_time(int waiting_time) {
        clock.set_total_waiting_time(waiting_time);
    }

    public void set_clock(Clock clock) {
        this.clock = clock;
    }

    private int pid; // use as key for CPU() to ignore the Program it's currently working on when
    // incrementing time_waiting
    private int time_required; // how long the Program must spend in CPU()
    private int initial_priority; // priority originally defined for the Program
    private int starvation; // counter used to increase priority when in the lowest percentile of
    // waiting_list, or decrease when in the top percentile of waiting_list if
    // previously increased
    private int real_priority; // calculation between initial_priority and starvation to ensure that a Program
    // cannot have less priority than it started with

    private int time_elapsed; // total time from first pass through CPU() to last pass through CPU across all
    // other Programs ran in the meantime. should be greater than initial value of
    // time_required
    private Clock clock;
}