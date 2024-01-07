import java.util.*;
import java.lang.*;
import java.io.*;

public class Scheduler { // encapulsates process states and passes them to CPU. keeps track of results
    // for simulation
    public Scheduler() {
        jobs_processed = 0;
        total_time_elapsed = 0;

        time_quantum = 0;
        task_count = 0;

        newin_list = new ArrayList<Program>();
        ready_list = new ArrayList<Program>();
        waiting_list = new ArrayList<Program>();
        terminated_list = new ArrayList<Program>();

        throughput_tracker = new int[500];
    }

    public void process(String directory_name, int time_quantum) {
        this.time_quantum = time_quantum; // re-define local variable
        newin_process(directory_name); // dynamically instantiate Programs
        while (terminated_list.size() < task_count) { // while not all Programs terminated
            if (newin_list.size() > 0 || waiting_list.size() > 0) { // sort, enqueue and dequeue
                ready_process();
                running_process();
                waiting_process();
            }
        }

        tracer(); // prints output results once all Programs terminated

        // zero out the tester object for other time quantum tests
        jobs_processed = 0;
        total_time_elapsed = 0;

        time_quantum = 0;
        task_count = 0;

        newin_list = new ArrayList<Program>();
        ready_list = new ArrayList<Program>();
        waiting_list = new ArrayList<Program>();
        terminated_list = new ArrayList<Program>();

        throughput_tracker = new int[500];
    }

    private void tracer() { // print results of CPU simulation
        // call clock functions for each item in terminated & local throughput methods
        double fraction_count = 0; // calculate and print CPU utilization results
        for (int i = 0; i < terminated_list.size(); i++) {
            fraction_count += terminated_list.get(i).get_clock().individual_CPU_utilization();
        }
        fraction_count /= task_count;

        System.out.println("CPU Utilization: " + fraction_count);

        System.out.println("Throughput: "); // print throughoput
        print_throughput();

        System.out.println("Turnaround: " + total_time_elapsed); // print turnaround

        int waiting_time = 0; // calculate individual waiting time from each Program object, print total
        for (int j = 0; j < terminated_list.size(); j++) {
            waiting_time += terminated_list.get(j).get_waiting_time();
        }
        System.out.println("Waiting Time: " + waiting_time);

        System.out.println("--------------"); // break line
    }

    // the CPU and process state simulators
    private void CPU(Program process) {
        if (time_quantum > process.get_time_required()) { // if the time quantum is longer than the remaining time

            int update = 0; // hold value for incrementing and decrementing
            update = process.get_time_required(); // hold, to be easier to work with

            process.set_time_required(0); // line 58: if time quantum longer than remaining time, Program has 0 time
                                          // needed left

            if (waiting_list.size() != 0) { // if there are Programs in waiting queue
                for (int i = 0; i < waiting_list.size(); i++) { // for every Program that is not current process in
                    // waiting_list, update individual waiting_time by remaining time of current
                    // Program
                    if (process.get_pid() != waiting_list.get(i).get_pid()) {
                        waiting_list.get(i).set_waiting_time(waiting_list.get(i).get_waiting_time() + update);
                    }
                }
            }
            if (ready_list.size() != 0) { // if there are Programs in ready queue
                for (int j = 0; j < ready_list.size(); j++) { // same thing for remaining queue of readylist
                    if (process.get_pid() != ready_list.get(j).get_pid()) {
                        ready_list.get(j).set_waiting_time(ready_list.get(j).get_waiting_time() + update);
                    }
                }
            }
            jobs_processed++; // increment completed job counter
            terminated_process(process); // completed job is held in terminate_list for tracer()
        } else { // if there is more time needed than the time quantum will finish on this
            // iteration

            process.set_time_required(process.get_time_required() - time_quantum); // decrement needed time remaining
                                                                                   // for Program

            if (waiting_list.size() != 0) { // if there are Programs in waiting queue
                for (int i = 0; i < waiting_list.size(); i++) { // for every Program that is not current process in
                    // waiting_list, update individual waiting_time by remaining time of current
                    // Program
                    if (process.get_pid() != waiting_list.get(i).get_pid()) {
                        waiting_list.get(i).set_waiting_time(waiting_list.get(i).get_waiting_time() + time_quantum);
                    }
                }
            }
            if (ready_list.size() != 0) { // if there are Programs in ready queue
                for (int j = 0; j < ready_list.size(); j++) { // same thing for remaining queue of readylist
                    if (process.get_pid() != ready_list.get(j).get_pid()) {
                        ready_list.get(j).set_waiting_time(ready_list.get(j).get_waiting_time() + time_quantum);
                    }
                }
            }
            waiting_list.add(process); // add Program to waiting_list
        }
        throughput_tracker[total_time_elapsed] = jobs_processed; // track current throughput after each cycle of CPU
    }

    private void newin_process(String directory_name) { // dynamically instantiate Programs at run time
        int a, b, c, d;
        try {
            FileInputStream open = new FileInputStream(directory_name); // instantiate input stream for program.txt
            Scanner reader = new Scanner(open); // scanner to read program.txt
            while (reader.hasNextLine()) { // while the next line exists, take the pid, arrival time, required time, &
                                           // priority from the next line
                a = Integer.parseInt(reader.nextLine().substring(0, 1));
                b = Integer.parseInt(reader.nextLine().substring(1, 2));
                c = Integer.parseInt(reader.nextLine().substring(2, 3));
                d = Integer.parseInt(reader.nextLine().substring(3, 4));
                newin_list.add(new Program(a, b, c, d)); // instantiate Program from a,b,c,d
                task_count++; // add to total Program counter
                reader.nextLine();
            }
            reader.close(); // close scanner when done
        } catch (IOException e) { // catch error due to file reading
            e.printStackTrace();
        }
        total_time_elapsed++; // increment total time elapsed for "not using the CPU"
    }

    private void ready_process() { // queue in Programs from newin_list and waiting_list in order of priority
        if (newin_list.size() != 0) { // if there are remaining elementsin newin_list
            for (int i = 0; i < newin_list.size(); i++) { // for all programs whose arrival time is current or
                // past, enqueue
                if (newin_list.get(i).get_arrival_time() <= total_time_elapsed) {
                    ready_list.add(newin_list.get(i));
                    newin_list.remove(i); // remove entries from the new list which have been enqueued for future
                                          // runs
                    i--;
                }
            }
            total_time_elapsed++; // increment total time elapsed for "not using the CPU". ensures all newin_list
            // Programs will eventually have their arrival time reached
        }
        if (waiting_list.size() != 0) { // if there are remaining elements in waiting_list
            for (int i = 0; i < waiting_list.size(); i++) { // for all programs in waiting queue, enqueue into
                // ready
                ready_list.add(waiting_list.get(i));
                waiting_list.remove(i); // remove entries from the waiting list which have been enqueued for future
                                        // runs
                i--;
            }
            total_time_elapsed++; // increment total time elapsed for "not using the CPU". ensures all newin_list
            // Programs will eventually have their arrival time reached
        }
        Program swap; // hold for swapsort
        for (int a = 0; a < ready_list.size() - 1; a++) { // sort ready queue by order of priority
            for (int b = a + 1; b < ready_list.size(); b++) {
                if (ready_list.get(a).get_real_priority() < ready_list.get(b).get_real_priority()) {
                    swap = ready_list.get(a);
                    ready_list.set(a, ready_list.get(b));
                    ready_list.set(b, swap);
                }
            }
        }
        total_time_elapsed++; // increment total time elapsed for "not using the CPU"
    }

    private void running_process() { // dequeue Programs from ready_list into CPU
        for (int i = 0; i < ready_list.size(); i++) { // iterate through ready_list; sorted in order of descending
            // priority
            CPU(ready_list.get(i));
            ready_list.remove(i);
            i--;
        }
    }

    private void waiting_process() {
        if (waiting_list.size() != 0) { // if waiting_list is not empty
            if (waiting_list.size() >= 4) { // if waiting_list is at least 4 elements
                for (int i = 0; i < waiting_list.size() / 4; i++) { // if task is in lowest quarter, increase priority
                    waiting_list.get(i).increase_priority();
                }
                for (int i = (waiting_list.size() / 4) * 3; i < waiting_list.size(); i++) { // if task is in highest
                    // quarter, decrease
                    // priority if it has
                    // been increased before
                    waiting_list.get(i).decrease_priority(); // (checked by decrease_priority())
                }
            } else { // if waiting list is less than 4 numbers
                for (int i = 0; i < waiting_list.size() / 2; i++) {
                    waiting_list.get(i).increase_priority(); // increase priority of lower half
                }
            }
        }
    }

    private void terminated_process(Program terminate) { // add Program to terminated_list, will no longer be accessed
        // by other states
        terminated_list.add(terminate);
    }

    // throughput printer
    private void print_throughput() {
        System.out.println("");
        for (int i = 4 + time_quantum; i < throughput_tracker.length; i++) { // skip forward by +1 over minimum time
                                                                             // needed for a task to be done
            if (throughput_tracker[i] != 0) {
                System.out.print(" " + i);
            }
        }
        System.out.println("");
        for (int j = 4 + time_quantum; j < throughput_tracker.length; j++) { // ""
            if (throughput_tracker[j] != 0) {
                System.out.print(" " + throughput_tracker[j]);
            }
        }
    }

    // getters and setters
    public ArrayList get_newin_list() {
        return newin_list;
    }

    public ArrayList get_ready_list() {
        return ready_list;
    }

    public ArrayList get_waiting_list() {
        return waiting_list;
    }

    public ArrayList get_terminated_list() {
        return terminated_list;
    }

    public int[] get_throughput_tracker() {
        return throughput_tracker;
    }

    public void set_new_list(ArrayList<Program> newin_list) {
        this.newin_list = newin_list;
    }

    public void set_ready_list(ArrayList<Program> ready_list) {
        this.ready_list = ready_list;
    }

    public void set_waiting_list(ArrayList<Program> waiting_list) {
        this.waiting_list = waiting_list;
    }

    public void set_terminated_list(ArrayList<Program> terminated_list) {
        this.terminated_list = terminated_list;
    }

    public void set_throughput_tracker(int[] throughput_tracker) {
        this.throughput_tracker = throughput_tracker;
    }

    private ArrayList<Program> newin_list; // dynamically instantiated Program objects
    private ArrayList<Program> ready_list; // queue going into CPU; sorted in order of priority
    private ArrayList<Program> waiting_list; // queue after CPU but not finished- resort back into ready_list
    private ArrayList<Program> terminated_list; // hold completed programs for tracer() to read from. compare length
    // with task_count to determine program end

    private int[] throughput_tracker; // the index pos represents time & the held integer is the # of
                                      // jobs completed by that time
    private int jobs_processed; // calculate throughput

    private int total_time_elapsed;
    private int time_quantum; // holds time quantum for red robin
    private int task_count; // holds # of programs instantiated altogether. check against terminated_list
    // length to see if all have terminated
}