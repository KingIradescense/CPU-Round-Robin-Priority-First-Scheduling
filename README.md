Hello. This repository holds an operating systems project from the fall of 2023. I've copy/pasted
the readme which I used for the original report below. The program ultimately does not run as
intended, but adequately displayed understanding of CPU task scheduling and various methods
where new processes are chosen. In this case, it is a hybrid of Round Robin and Priority First.

The attached program is intended to simulate Priority First Round Robin CPU scheduling. The instructions 
to run and its explained implementation are as follows: The simulation will use “program.txt” for programs. 
Each row is a program and the number order is “pid” “arrival time” “time required” “initial priority”.

In order to run the program, open Priority_First_Round_Robin.java, which is the location of the main program. 
It will instantiate a Scheduler object and simulate running the programs represented in program.txt with time quantums 1-5.

There are three classes in the program: Scheduler.java, Program.java, and Clock.java. Scheduler’s primary purpose 
is to encapsulate the simulation of the process states and the activity of the CPU itself while keeping track of some 
results of the simulation, Program.java is responsible for keeping track of details about individual programs, and 
Clock.java is responsible for keeping track of simulation results which Scheduler does not (and every Program has a Clock).

The process is:

Newly instantiated Scheduler object is given the directory for program.txt and the currently tested time quantum. Scheduler
will retain the time quantum for itself and give the directory to a method “newin_process()” which simulates the new state of 
a program’s lifecycle. Here, the program will read the file line by line and dynamically instantiate Program objects at runtime. 
In addition to the four given values, Program will also keep track of a “starvation” counter and a “real priority” value. These 
objects will be kept in a “new state” ArrayList. When the “new state” is complete, the program will move on to the method 
representing the ready state, “ready_process()”. The method will enqueue any waiting elements from the “new state” ArrayList 
and the “waiting state” ArrayList (which will be 0 until after the first run state) into the “ready” ArrayList, and then 
swapsort the queue based on each Program’s “real priority”. When the sort is completed, the Program will move on to the running state. 
The running state method “running_process()” will dequeue Programs from the “ready” ArrayList into the CPU() method in order.

The CPU() method will check if the current time quantum is greater than the remaining time needed for a Program to complete
execution (which is kept track of by the Program object). If it is, the total time elapsed and the total time working will be
incremented by that remaining time, the Program object will be added to the “terminated” ArrayList, and the completed jobs counter 
will increase. If the time quantum is not greater than the remaining time needed, the total time elapsed and total working time will 
be incremented by the time quantum, the Program’s remaining time will be decremented by the time quantum, and the Program object will
be added to the “waiting” ArrayList.

The waiting state method “waiting_process()” will run after the “ready” ArrayList has fully dequeued. This method will increase
or decrease the priority or remaining elements based on the order of the “waiting” ArrayList via the “starvation” counter of 
a Program. It will first check if there are any elements in the waiting list at all. If there are, then it will check if there 
are more than or equal to 4 elements. If an element falls into the lower 1/4th percentile, their starvation counter will increase 
and the “real priority” will update (which is the initial priority plus starvation). If an element follows into the top 1/4th 
percentile, their starvation counter will decrease if it is not already 0 and update real priority (which doesn’t allow a Program 
to have less priority than it instantiated with, and also doesn’t allow a starved Program to take high precedence over a program 
with initial high priority). If there are less than 4 elements, it will increase the priority of the lower half.

The Scheduler will loop these methods until all methods have been terminated. At that point the program will use the terminated 
list to access private variables of the Program objects in order to obtain some of the results.
