/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Scanner;
import simulator.*;

/**
 *
 * @author Meluleki Dube
 */
public class SimulateFCFS {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        System.out.println("*** FCFS Simulator ***");
        System.out.print("Enter configuration file name: ");
        String configName = in.nextLine();
        System.out.print("Enter cost of system call: ");
        int sysCallCost = Integer.parseInt(in.nextLine());
        System.out.print("Enter cost of context switch: ");
        int contextCost = Integer.parseInt(in.nextLine());
        System.out.print("Enter trace level: ");
        int traceLevel = Integer.parseInt(in.nextLine());
        TRACE.SET_TRACE_LEVEL(traceLevel);
        Kernel kernel = new FCFSKernel();
        Config.init(kernel, contextCost, sysCallCost);
        Config.buildConfiguration(configName);
        Config.run();
        SystemTimer st = Config.getSystemTimer();
        System.out.println(st);
        System.out.println("Context switches: "+Config.getCPU().getContextSwitches());
        System.out.printf("CPU utilization: %.2f\n", ((double) st.getUserTime()/st.getSystemTime()*100));
        
    }

}
