/*
* - args = [mergedFile, timeWindow, channel data, used peaks]
* -This program takes in the merged csv file.
* -Time window is set by the input argument
* -A extendable inclusion of events as long as the interval of two consecutive hit is
* within the window
* -records consecutive events happening in different layers (1-2,1-3,2-3,...)
*
*
*
* */

import java.util.*;
import java.io.*;

public class CoinDiffLayer {
    static double timeWindow;
    static Calibrator Cali;
    static int TotalLayer;
    static int pixelNumEachLayer = 64;
    static HashSet<String> legalOrder;
    // static HashMap<String,List<String>> diffLayerMap;
    public static void main(String[] args) throws Exception{
        File toRead = new File(args[0]);
        timeWindow = Double.parseDouble(args[1]);
        Cali = new Calibrator(new File(args[2]), new File(args[3]));
        TotalLayer = 3;


        ArrayList<DAQEvent> allList = readFromCSV(toRead);
        System.out.println("Read csv finished");
        List<String> printCan = getCoincidence(allList);
        System.out.println(allList.size()+" "+printCan.size());
        File toWrite = new File("coin.txt");
        toWrite.createNewFile();
        PrintWriter out = new PrintWriter(new FileWriter(toWrite));
        for(String s:printCan) out.println(s);
        out.flush();
        out.close();
    }
    static List<String> getCoincidence(ArrayList<DAQEvent> allEvents){
        List<String> ans = new ArrayList<>();
        List<DAQEvent> temp = new ArrayList<>();
        int left=0,right,len = allEvents.size();
        while(left<len){
            temp.add(allEvents.get(left));
            right = left+1;
            while(right<len&&allEvents.get(right).time<allEvents.get(right-1).time+timeWindow){
                temp.add(allEvents.get(right));
                right++;
            }
            String curString = fromTempList(temp);
            if(curString.length()>0) ans.add(curString);
            temp.clear();
            left = right;
        }
        return ans;
    }
    static String fromTempList(List<DAQEvent> temp){
        if(temp.size()!=2) return "";
        DAQEvent eve1 = temp.get(0), eve2 = temp.get(1);

        int ch1 = eve1.channel, ch2 = eve2.channel;
        if(Cali.isBroken(ch1)||Cali.isBroken(ch2)) return "";
        if((ch1-1)%pixelNumEachLayer==(ch2-1)%pixelNumEachLayer) return "";

        double es = Cali.getEnergy(ch1,eve1.width),ea = Cali.getEnergy(ch2,eve2.width);
        if(ch1>ch2){
            double mid = es;
            es = ea; ea = mid;
        }
        return String.format("%.1f,%.1f",es,ea);
    }

    static ArrayList<DAQEvent> readFromCSV(File file) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<DAQEvent> ans = new ArrayList<>();
        String line;
        String[] buf;
        while((line=reader.readLine())!=null){
            buf = line.split(",");
            int ch = Integer.parseInt(buf[0]);
            double stamp = Double.parseDouble(buf[1]), width = Double.parseDouble(buf[2]);
            DAQEvent cur = new DAQEvent(ch,stamp,width);
            ans.add(cur);
        }
        reader.close();
        return ans;
    }
    /*
    static HashMap<String,List<String>> getCandidateOrder(int numLay){
        HashMap<String,List<String>> ans = new HashMap<>();
        for(int i=0;i<numLay;i++){
            for(int j=i+1;j<numLay;j++){
                ans.put(i+" "+j,new ArrayList<>());
            }
        }
        return ans;
    }*/
}
