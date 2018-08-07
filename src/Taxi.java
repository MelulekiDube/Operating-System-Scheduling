
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki Dube
 */
public class Taxi extends Thread {

    private static Semaphore[] branches;
    private static int currentIndex;
    private static int passenger;
    private static boolean moreJobs = true;
    private static Taxi taxi;
    private boolean outBound;
    private final Queue<Person>[] branchBusstop; // this stores the pickup requests for the individual branches
    private final ArrayList<Person>[] dropRequest; // this stores the drop request for the individual branches

    @SuppressWarnings("unchecked")
    public Taxi(int nOB, int nop) {
        taxi = this;
        branches = new Semaphore[nOB];
        branchBusstop = new Queue[nOB];
        dropRequest = new ArrayList[nOB];
        for (int i = 0; i < nOB; ++i) {
            branches[i] = new Semaphore(0);
            branchBusstop[i] = new ArrayDeque<>();
            dropRequest[i] = new ArrayList<>();
        }
        currentIndex = 0;
        passenger = nop;
        outBound = true;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        System.out.println("Engines have started");
        while (passenger > 0) {
            if (checkWaiting() || dropRequestExist()) {
                branches[currentIndex].release(passenger);
                dropBy();
                pickUp();
                try {
                    //sleep here
                    Thread.sleep(33);
                    SystemTimer.incrementMinutes(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Taxi.class.getName()).log(Level.SEVERE, null, ex);

                }
                branches[currentIndex].drainPermits();//acquire all permits available
                if (checkWaiting() || dropRequestExist()) {
                    Trace.printTrace(Trace.ACTIVITY.DEPART, currentIndex);
                    moveToNextBranch();
                }
            }
        }
        System.out.println("Taxi Engine stopped");
    }

    private void moveToNextBranch() {
        if (outBound) {
            ++currentIndex;
            outBound = (currentIndex != branches.length - 1);// only change outbound when we reach the n-1th index 
        } else {
            --currentIndex;
            outBound = (currentIndex == 0); //only change the value of outbound when we reach the 1st destination
        }
        try {
            Thread.sleep(66);
            SystemTimer.incrementMinutes(2);
        } catch (InterruptedException ex) {
            Logger.getLogger(Taxi.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (branchBusstop[currentIndex].isEmpty() && dropRequest[currentIndex].isEmpty()) {
            moveToNextBranch();
        } else {
            Trace.printTrace(Trace.ACTIVITY.ARRIVE, currentIndex);
        }
    }

    public boolean pickUp() {
        List<Person> toRemove = new ArrayList<>();
        if (!branchBusstop[currentIndex].isEmpty()) { // if we have drop requests for this particular branch
            branchBusstop[currentIndex].forEach((tempPassenger) -> { // for each passenger waiting to drop off at this branch
                toRemove.add(tempPassenger);
//                }
            });
        }
        toRemove.forEach((temp) -> {
            branchBusstop[currentIndex].remove(temp);
        });
        return !toRemove.isEmpty();
    }

    public boolean dropBy() {
        List<Person> toRemove = new ArrayList<>();
        if (!dropRequest[currentIndex].isEmpty()) { // if we have drop requests for this particular branch
            dropRequest[currentIndex].forEach((tempPassenger) -> { // for each passenger waiting to drop off at this branch
//                Pair<Integer, Integer> passInfo = tempPassenger.getNexDestination(); // get the travelling info for the person
//                if ((passInfo != null) && passInfo.getKey() == currentIndex) { //
                Trace.printTrace(Trace.ACTIVITY.DISEMBARK, currentIndex, tempPassenger);
                toRemove.add(tempPassenger);
//                }
            });
        }
        toRemove.forEach((temp) -> {
            dropRequest[currentIndex].remove(temp);
        });
        return !toRemove.isEmpty();
    }

    public void RequestTaxi(Person p, int branchAt) throws InterruptedException { //change syncrhonized to using a semaphore

        if (!checkWaiting() && !dropRequestExist()) { // meaning the taxi was not moving.F
            if (branchAt > currentIndex) {
                this.outBound = true;
            } else if (branchAt < currentIndex) {
                this.outBound = false;
            }
        }
//        System.out.println("Request by " + p);
        branchBusstop[branchAt].add(p);
        branches[branchAt].acquire();
    }

    public void requestDrop(Person p, int branchAt) throws InterruptedException {
        dropRequest[branchAt].add(p);
        Trace.printTrace(Trace.ACTIVITY.REQUEST, currentIndex, p, branchAt);
        branches[branchAt].acquire();
    }

    public static Semaphore getBranchSemaphore(int branchNumber) {
        return branches[branchNumber];
    }

    /**
     * check if there are people waiting at other branches
     *
     * @return true if there are still more requests and false if the requests
     * are finished
     */
    public synchronized boolean checkWaiting() {
        for (Queue b : branchBusstop) {
            if (!b.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void done() {
        passenger--;
    }

    public static Taxi getTaxi() {
        return taxi;
    }

    private synchronized boolean dropRequestExist() {
        for (ArrayList list : dropRequest) {
            if (list.size() > 0) {
                return true;
            }
        }
        return false;
    }
}
