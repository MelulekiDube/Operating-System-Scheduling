
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki Dube
 */
public class Simulator {

    private Person[] workers;
    private static int numberOfBranches;
    private static int NUMBER_OF_WORKERS;
    private Taxi taxi;

    public void readFile(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));
            NUMBER_OF_WORKERS = Integer.parseInt(sc.nextLine());
            workers = new Person[NUMBER_OF_WORKERS];
            numberOfBranches = Integer.parseInt(sc.nextLine());
	    int count=0;
            while (sc.hasNextLine()) {
                Pair<Integer, Queue<Pair<Integer, Integer>>> info = createBranchList(sc.nextLine());
                workers[count] = new Person(info.getKey(), info.getValue());
//                workers[info.getKey()].start();
		count++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        taxi = new Taxi(numberOfBranches, NUMBER_OF_WORKERS);
    }

    private Pair<Integer, Queue<Pair<Integer, Integer>>> createBranchList(String s) {
        Queue<Pair<Integer, Integer>> result = new ArrayDeque<>();
        int personId = Integer.parseInt(s.split(" ")[0]);
        String new_s = s.replaceFirst("" + personId + " ", "");
        // Removing the unncessar characters from the string
        new_s = new_s.replace("(", "");
        new_s = new_s.replace(")", "");
        new_s = new_s.replace(",", "");
        String[] destination = new_s.split(" ");
        for (int i = 0; i < destination.length; i++) {
            Pair temp = new Pair<>(Integer.parseInt(destination[i]), Integer.parseInt(destination[++i]));
            result.offer(temp);
        }
        return new Pair<>(personId, result);
    }

    public synchronized static void signalDone(Person p) throws InterruptedException {
//        BINARY__SEMAPHORE.acquire(); //acquire lock to update the numberOfDoneworkers count
//        numberOfDoneWorkers++; //update the variable
//        System.out.println(numberOfDoneWorkers);

//        BINARY__SEMAPHORE.release(); //release the lock to update the variable incase any other threads wants to
    }

    private void startSimulate() {
        taxi.start();
        for (Person p : workers) {
            p.start();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*****************************************");
        System.out.println("Simulation of Taxi service");
        System.out.println("Welcome to Meluleki's Taxi Service!!!!!");
        System.out.println("*****************************************");
        System.out.println("Please enter filename with the company's information");
        String filename = sc.nextLine();
        System.out.println("Trace for the Taxi movement is below");
        System.out.println("*****************************************");
        Simulator sim = new Simulator();
        sim.readFile(filename);
        sim.startSimulate();
    }

}
