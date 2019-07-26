/*
* Takes in two csv files, one short csv to record which energies are used
* One long short csv record the peaks at different energies for each channel
*
* */
import java.util.*;
import java.io.*;

public class Calibrator {
    private ArrayList<double[]> channelPeak;
    private double[] Energies;
    private HashSet<Integer> broken;
    public Calibrator(File channelData, File usedPeaks) throws Exception{
        initialization(channelData,usedPeaks);
        if(channelPeak.size()<10){
            System.out.print("Wrong input");
            System.exit(0);
        }
    }
    public double getEnergy(int ch, double tot){
        if(broken.contains(ch)) return 0.0;
        int len = Energies.length;
        double[] pos = channelPeak.get(ch-1); // mind the index
        if(tot<=pos[0]) return insert(pos[0],Energies[0],pos[1],Energies[1],tot);
        if(tot>=pos[len-1]) return insert(pos[len-2],Energies[len-2],pos[len-1],Energies[len-1],tot);
        for(int i=0;i<len-1;i++){
            if(tot>=pos[i]&&tot<=pos[i+1]){
                return insert(pos[i],Energies[i],pos[i+1],Energies[i+1],tot);
            }
        }
        return 1.0;
    }
    private double insert(double x1, double y1, double x2, double y2, double t){
        if(Double.compare(x1,x2)==0) return y1;
        return y1+(t-x1)*(y2-y1)/(x2-x1);
    }
    private void initialization(File chData, File usedPeaks) throws Exception{
        broken = new HashSet<>();
        channelPeak = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(chData));
        String line; String[] buf;
        int cnt = 0;
        while((line=reader.readLine())!=null){
            buf = line.split(",");
            cnt++;
            int len = buf.length;
            double[] p = new double[len];
            boolean flag = true;
            for(int i=0;i<len;i++){
                p[i] = Double.parseDouble(buf[i]);
                flag &= p[i]>0.01;
            }
            if(!flag){
                broken.add(cnt); // this channel is broken. Cannot be used
            }
            Arrays.sort(p);
            channelPeak.add(p);
        }
        reader.close();
        reader = new BufferedReader(new FileReader(usedPeaks));
        line = reader.readLine();
        line = line.replace(" ","");
        buf = line.split(",");
        Energies = new double[buf.length];
        for(int i=0;i<Energies.length;i++) {
            try{
                buf[i] = buf[i].replace(" ","");
                buf[i] = buf[i].replace("\t","");
                Energies[i] = Double.parseDouble(buf[i]);
            } catch (NumberFormatException e){
                System.out.println(line);
                System.out.println(usedPeaks.getName());
                System.exit(0);
            }

        }
    }
    public boolean isBroken(int ch){return broken.contains(ch);}
    public double maxEnergy(){return Energies[Energies.length-1];}
}
