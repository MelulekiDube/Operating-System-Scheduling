
import java.util.Queue;
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
public class Person extends Thread {

    private static final int SLEEPING_TIME_FACTOR = 33;
    private final int ID;
    private int currentBranch;
    private final Queue<Pair<Integer, Integer>> branchestoworkAt;


    public Person(int ID, Queue<Pair<Integer, Integer>> btw) {
        this.ID = ID;
        this.branchestoworkAt = btw;
        this.currentBranch = 0;
    }

    @Override
    public void run() {
        try {
            while (branchestoworkAt.size() > 0) {
                Pair<Integer, Integer> taskInfo = branchestoworkAt.poll();
                if (currentBranch != taskInfo.getKey()) {//since everyone starts at branc 0 this check is to deal with that to get them to their required destination
                    Trace.printTrace(Trace.ACTIVITY.HAIL, currentBranch, this);
                    Taxi.getTaxi().RequestTaxi(this, currentBranch); //Hail the taxi
                    Taxi.getTaxi().requestDrop(this, taskInfo.getKey()); //Putting a request
                    currentBranch = taskInfo.getKey(); // since we have arrived at our final destination.
                }
                Thread.sleep(SLEEPING_TIME_FACTOR * taskInfo.getValue()); //perform work at current branch 
                SystemTimer.incrementMinutes(taskInfo.getValue()); // record ammount of time spent on that branch
            }
            Taxi.getTaxi().done();
        } catch (InterruptedException ex) {
            Logger.getLogger(Person.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Pair<Integer, Integer> getNexDestination() {
        return branchestoworkAt.peek();
    }

    @Override
    public String toString() {
        return "person " + this.ID;
    }

}
