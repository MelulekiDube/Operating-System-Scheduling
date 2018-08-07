
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki Dube
 */
public class Trace {

    //<event description> ::= hail |request <destination branch> | disembark | arrive | depart
    public enum ACTIVITY {
        HAIL, ARRIVE, DEPART, REQUEST, DISEMBARK
    }
    private static List<String> log_file = Collections.synchronizedList(new ArrayList<>());

    /**
     *
     * @param a
     * @param vargs
     */
    public static void printTrace(ACTIVITY a, Object... vargs) {
        String time = SystemTimer.getCurrentTime();
        Integer branchEventHappens = (Integer) vargs[0];
        Person passengerCausingEvent;
        String details = "";
        switch (a) {
            case HAIL: {
                passengerCausingEvent = (Person) vargs[1];
                details += time + " branch " + branchEventHappens.toString() + ": " + passengerCausingEvent.toString() + " Hail";
                break;
            }
            case ARRIVE: {
                details += time + " branch " + branchEventHappens.toString() + " taxi Arrives";
                break;
            }
            case DEPART: {
                details += time + " branch " + branchEventHappens.toString() + " taxi Depart";
                break;
            }
            case REQUEST: {
                passengerCausingEvent = (Person) vargs[1];
                int requestTo = (Integer) vargs[2];
                details += time + " branch " + branchEventHappens.toString() + ": " + passengerCausingEvent.toString() + " request " + requestTo;
                break;
            }
            case DISEMBARK: {
                //index 1 = person
                passengerCausingEvent = (Person) vargs[1];
                details += time + " branch " + branchEventHappens.toString() + ": " + passengerCausingEvent.toString() + " disembarks ";
                break;
            }
            default: {
                break;
            }
        }
        System.out.println(details);
//        log_file.add(details);
    }

    public static void printLog() {
        log_file.forEach((log) -> {
            System.out.println(log);
        });
    }
}
