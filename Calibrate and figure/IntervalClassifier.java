/*
* -This class reads the sorted merged file, and takes out time interval of two consecutive
* events that happen in different layers
* -Print the results to txt file for python to analyze and draw
*
* */

import java.util.*;
import java.io.*;

public class IntervalClassifier {
    private int numOfLayers;
    public HashMap<String,List<Double>> map;
    private int pixelsNumOneLayer;
    public IntervalClassifier(int nLayer, int p){
        this.numOfLayers = nLayer; this.pixelsNumOneLayer = p;
        map = new HashMap<>();
        mapIni();
    }
    static public void main(String[] args) throws Exception{
        // args = {merged csv file, numOfLayer, pixelNumLayer
        int num = Integer.parseInt(args[1]), p = Integer.parseInt(args[2]);
        IntervalClassifier IC = new IntervalClassifier(num,p);
        IC.processFile(new File(args[0]));
        for(String key:IC.map.keySet()){
            List<Double> list = IC.map.get(key);
            File write = new File(key+".csv");
            write.createNewFile();
            PrintWriter out = new PrintWriter(new FileWriter(write));
            for(double w:list){
                out.println(String.format("%.1f",w));
            }
            out.flush();
            out.close();
            System.out.println(String.format("Write to %s finished",key+".csv"));
        }
    }
    private void mapIni(){
        for(int i=0;i<numOfLayers;i++){
            for(int j=i+1;j<numOfLayers;j++){
                map.put(i+"-"+j,new ArrayList<>());
            }
        }
    }
    public void processFile(File f) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line = reader.readLine();
        DAQEvent prev = new DAQEvent(line);
        int preMod = (prev.channel-1)/pixelsNumOneLayer;
        while((line=reader.readLine())!=null){
            DAQEvent cur = new DAQEvent(line);
            int curMod = (cur.channel-1)/pixelsNumOneLayer;
            if(curMod!=preMod){ // two different layers
                String key = Math.min(curMod,preMod)+"-"+Math.max(curMod,preMod);
                double dt = cur.time-prev.time;
                if(map.get(key)==null){
                    System.out.println(key);
                    System.exit(0);
                }
                map.get(key).add(dt);
            }
            preMod = curMod;
            prev = cur;
        }
        reader.close();
        System.out.println("Reading the merged file finished");
    }

}
