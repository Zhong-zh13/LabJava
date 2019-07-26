/*
* This class handles the data from single DAQ system and do the following things:
* - Read data from all the .csv files and merge them into one big array of objects (channel, timestamp [ns], width)
* - Sort
* - Rearrange the "ill-formed" signal from synchronizing signal
* - Calibrate using the synchronizing signal
* */

import java.util.*;
import java.io.*;

public class SingleDAQHandler {
    private final String path;    // the absolute path of the folder which holds all csv files from one DAQ
    private int synChannel; // which channel is used as the synchronizing signal
    private double period;  // in [ns]: the period of synchronizing channel
    // private ArrayList<DAQEvent> beforeCali;
    public String DAQName;

    public SingleDAQHandler(String Path, int SynCha, double Period){
        this.path=Path ;this.synChannel = SynCha;this.period = Period;
    }
    public void setDAQName(String s){
        this.DAQName = s;
    }
    public ArrayList<DAQEvent> solve() throws Exception{
        ArrayList<DAQEvent> beforeCali = readAllFiles();
        Collections.sort(beforeCali);
        ArrayList<DAQEvent> ret = new ArrayList<>();
        int start = 0, end = beforeCali.size()-1, len = beforeCali.size();
        while(start<len&&(beforeCali.get(start).channel!=synChannel)) start++;
        while(end>=0&&(beforeCali.get(end).channel!=synChannel)) end--;
        // start is the index of first syn event
        // end is the index of the last syn event
        // may contain duplicate
        int left=getLastDupSyn(start,beforeCali),right;
        double timeBase = 0.0; // consider the syn signal to be 100% actual
        while(left<end){
            right = left+1;
            while(right<end&&beforeCali.get(right).channel!=synChannel) ++right;
            right = getLastDupSyn(right,beforeCali);
            double time0 = beforeCali.get(left).time,interval = beforeCali.get(right).time-time0;
            double dt = period/interval;
            for(int j=left+1;j<right;j++){
                DAQEvent cur = beforeCali.get(j);
                if(cur.channel==synChannel) continue;
                double after = timeBase+(cur.time-time0)*dt;
                cur.time = after;
                ret.add(cur);
            }
            left = right;
            timeBase += period;
        }
        File toWrite = new File(DAQName+".csv");
        toWrite.createNewFile();
        PrintWriter out = new PrintWriter(new FileWriter(toWrite));
        for(DAQEvent e:ret){
            out.println(String.format("%d,%.1f,%.1f",e.channel,e.time,e.width));
        }
        out.flush();
        out.close();
        return ret;
    }

    private ArrayList<DAQEvent> readAllFiles() throws Exception{
        File targetFolder = new File(path);
        File[] csvList = targetFolder.listFiles();
        ArrayList<DAQEvent> ret = new ArrayList<>(100000);
        for(File f:csvList){
            BufferedReader reader = new BufferedReader(new FileReader(f));
            if(f.getName().startsWith(".")||!f.getName().endsWith(".csv")){
                reader.close();
                continue;
            }
            String line;
            String[] buf;
            while((line=reader.readLine())!=null){
                buf = line.split(",");
                try{
                    int ch = Integer.parseInt(buf[0]);
                    double t = Double.parseDouble(buf[1]), w = Double.parseDouble(buf[2]);
                    ret.add(new DAQEvent(ch,t,w));
                } catch (NumberFormatException e){
                    System.out.println(line);
                    System.out.println(f.getName());
                    System.exit(0);
                }
            }
            reader.close();
        }
        return ret;
    }
    private int getLastDupSyn(int idx, ArrayList<DAQEvent> list){
        double curTime = list.get(idx).time;
        int ans = idx+1;
        while(ans<list.size()&&list.get(ans).time==curTime) ++ans;
        --ans;
        return ans;
    }

}
