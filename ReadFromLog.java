/*
* Input a folder that contains all the log files
* Each event is an 9-byte sequence
* Get the info of channel, stamp, width
* Save into the txt file
* Can't treat as a normal file.
* */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ReadFromLog {
    static String folderName;
    static PrintWriter out;
    static int cnt=0;
    static public void main(String[] args) throws Exception{
        folderName = args[0];
        if(!folderName.endsWith("/")){
            folderName = folderName + "/";
        }
        File folder = new File(folderName);
        File[] list = folder.listFiles();
        File toWrite = new File("converted.txt");
        toWrite.createNewFile();
        out = new PrintWriter(new FileWriter(toWrite));
        for(File fi:list){
            readBinaryFile(fi);
        }
        out.flush();
        out.close();
    }
    // log file format
    /*
    * Address : センサ位置 8bit 1～144(0x01~0x90)
      Time Stamp : タイムスタンプ 44bit 0～243 Max 120min 2.5ns 単位
    * Length : パルス長さ 20bit 0～219 10.0 ns ~ 400.0 us 2.5 ns 単位
    * Too long to be recorded by a Long
    * Treat as a String
    *
    * Channel 192 is somehow special, should be excluded
    * */
    static void readBinaryFile(File file) throws Exception{
        String s = file.getName();
        if(!s.endsWith(".log")) return;

        Path path = Paths.get(folderName+s);
        byte[] content = Files.readAllBytes(path);
        for(int i=0;i<content.length;i+=9){
            StringBuilder sb = new StringBuilder();
            for(int j=i;j<i+9;j++) {
                String temp = byteTobinary(content[j]);
                /*if(cnt++<50){
                    System.out.println(temp);
                }*/
                sb.append(temp);
            }
            long[] cur = ChStampWidth(sb.toString());
            double sta = 2.5*cur[1], wid = 2.5*cur[2];
            if(cur[0]!=192) {
                out.println(String.format("%d,%.1f,%.1f",cur[0],sta,wid));
            }
        }
    }
    // Unsigned binary numbers
    // 8 bits should be able to represent up to 144
    static long[] ChStampWidth(String line){
        String chString = line.substring(0,8);
        String stampString = line.substring(8,52);
        String widString = line.substring(52,72);
        String[] temp = new String[]{chString,stampString,widString};
        long[] ans = new long[3];
        for(int i=0;i<3;i++) ans[i]=Long.parseUnsignedLong(temp[i],2);
        return ans;
    }
    static String byteTobinary(byte by){
        int va = Byte.toUnsignedInt(by);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<8;i++){
            if((va&1)==1) sb.append(1);
            else sb.append(0);
            va = va >>1;
        }
        return sb.reverse().toString();
    }
}
