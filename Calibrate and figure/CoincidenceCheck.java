/*
* This program reads a finished csv file (timely-ordered)
* and generate the coincident events with a given time window
* */

import java.util.*;
import java.io.*;

public class CoincidenceCheck {
    static public void main(String[] args) throws Exception{
        double window = Double.parseDouble(args[1]); // in [ns]
        File file = new File(args[0]); // the merged file
        ArrayList<DAQEvent> list = readFile(file), coin;
        System.out.println(list.size());
        coin = getCoincidence(list,window);
        System.out.println(coin.size());
        Record(coin);
        System.out.println("finish");
    }
    static private ArrayList<DAQEvent> readFile(File f) throws Exception{
        ArrayList<DAQEvent> ret = new ArrayList<>(100000);
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line;
        String[] buf;
        while((line=reader.readLine())!=null){
            buf = line.split(",");
            int ch = Integer.parseInt(buf[0]);
            double t = Double.parseDouble(buf[1]), w = Double.parseDouble(buf[2]);
            ret.add(new DAQEvent(ch,t,w));
        }
        reader.close();
        return ret;
    }
    static void Record(ArrayList<DAQEvent> coinArr) throws Exception{
        File write = new File("Coincidence.csv");
        write.createNewFile();
        PrintWriter out = new PrintWriter(new FileWriter(write));
        for(DAQEvent e:coinArr){
            out.println(String.format("%d,%.1f,%.1f",e.channel,e.time,e.width));
        }
        out.flush();
        out.close();
    }
    static ArrayList<DAQEvent> getCoincidence(ArrayList<DAQEvent> arr, double window){
        ArrayList<DAQEvent> ans = new ArrayList<>();
        int i=0,j,len = arr.size();
        while(i<len){
            j = i+1;
            double time0 = arr.get(i).time, time1 = time0+window;
            while(j<len&&arr.get(j).time<time1) ++j;
            if(j-i>1){
                for(int k=i;k<j;k++) ans.add(arr.get(k));
            }
            i = j;
        }
        return ans;
    }
}
