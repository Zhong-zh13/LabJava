/*
 * The DAQ system uses 44 bits to record the time stamp at the frequency of 400 MHz
 * Suppose it's in the form of signed integer, then 43 bits can be actually used
 * The maximum number would be 2^43-1=8.79e12
 * Each timestamp covers 2.5 ns
 * The maximum time length would be 22000 sec = 6 hrs, which is long enough for most of our measurement
 * It makes sense to assume that the time stamp value increases with the real time
 *
 */

import java.util.*;
import java.io.*;

public class MergeTwoDAQ {
    // args = {path1, path2, synChannel, period(ns)}
    public static void main(String[] args) throws Exception{
        int synCha = Integer.parseInt(args[2]); // which channel is used as the syn in .csv files
        double period = Double.parseDouble(args[3]); // period of sync signal in nanoseconds
        ArrayList<DAQEvent> result = new ArrayList<>();
        long time0 = System.currentTimeMillis(), time1;
        for(int i=1;i<3;i++){
            SingleDAQHandler handler = new SingleDAQHandler(args[i-1],synCha,period);
            handler.setDAQName("DAQ"+i);
            ArrayList<DAQEvent> curr = handler.solve();
            result.addAll(curr);
            System.out.println(curr.size());
            time1 = System.currentTimeMillis();
            System.out.println("Handle case No. "+i+" uses: "+(time1-time0)+" ms.");
            time0 = time1;
        }
        Collections.sort(result);
        time1 = System.currentTimeMillis();
        System.out.println("Sort the big csv file uses: "+(time1-time0)+" ms.");
        time0 = time1;
        File write = new File("merged.csv");
        write.createNewFile();
        PrintWriter out = new PrintWriter(new FileWriter(write));
        for(DAQEvent e:result){
            out.println(String.format("%d,%.1f,%.1f",e.channel,e.time,e.width));
        }
        out.flush();
        out.close();
        System.out.println("Write to target csv file uses: "+(System.currentTimeMillis()-time0)+" ms.");
    }
    static ArrayList<DAQEvent> checkCoincidence(ArrayList<DAQEvent> arr){
        double threshold = 5.0;
        ArrayList<DAQEvent> ans = new ArrayList<>();
        int i=0,j,len = arr.size();
        while(i<len){
            j = i+1;
            double time0 = arr.get(i).time, time1 = time0+threshold;
            while(j<len&&arr.get(j).time<time1) ++j;
            if(j-i>1){
                for(int k=i;k<j;k++) ans.add(arr.get(k));
            }
            i = j;
        }
        return ans;
    }

}
