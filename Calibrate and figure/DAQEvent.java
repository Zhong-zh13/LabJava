public class DAQEvent implements Comparable<DAQEvent>{
    double time;
    double width;
    int channel;
    public DAQEvent(int ch, double t, double w){
        this.channel = ch; this.time = t; this.width = w;
    }
    public DAQEvent(String line){ // assume a valid line from CSV file
        String[] buf =line.split(",");
        int ch = Integer.parseInt(buf[0]);
        double stamp = Double.parseDouble(buf[1]), tot = Double.parseDouble(buf[2]);
        this.channel = ch; this.time = stamp; this.width = tot;
    }
    @Override
    public int compareTo(DAQEvent o1){
        int ans = Double.compare(this.time,o1.time);
        if(ans!=0) return ans;
        else return this.channel-o1.channel;
    }
}
