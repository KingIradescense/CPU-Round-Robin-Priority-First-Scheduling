import java.util.*;
import java.lang.*;
import java.io.*;

public class Priority_First_Round_Robin {
    public static void main(String[] args) {
        Scheduler tester = new Scheduler();
        for (int i = 1; i <= 5; i++) {
            tester.process(
                    "program.txt",
                    i); // full program runs from process()
            System.out.println(""); // space in output results between different time quantums
        }
    }
}