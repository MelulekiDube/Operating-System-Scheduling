
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki Dube
 */
public class SystemTimer {

    private static int mins = 0, hours = 9;

    public static void incrementMinutes(int a) {
        int temp = mins + a;
        mins = temp % 60;
        hours += temp / 60;
    }

    public static String getCurrentTime() {
        return (hours > 9 ? hours : "0" + hours) + ":" + (mins > 9 ? mins : "0" + mins);
    }

    @Override
    public String toString() {
        return (hours > 10 ? hours : "0" + hours) + ":" + (mins > 10 ? mins : "0" + mins);
    }

}
