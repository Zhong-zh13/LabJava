import java.util.*;

import static java.lang.Math.PI;

class SolidAngle{
    // solve the problem where the interested area is rectangle
    // put the center of rectangle at the xOy origin
    double length,width,height;
    int binNum, iter;
    public static void main(String[] args){
        // first consider a simple version, the source is over the center
        double hei = Double.parseDouble(args[0]);
        double len = Double.parseDouble(args[1]);
        double wid = Double.parseDouble(args[2]);
        int bin = Integer.parseInt(args[3]);
        int it = Integer.parseInt(args[4]);
        SolidAngle sa = new SolidAngle(hei,len,wid,bin,it);
        double sourceLength = Double.parseDouble(args[5]), sourceWidth = Double.parseDouble(args[6]);
        double average = sa.averageSolidAngleOverRectangle(sourceLength,sourceWidth);
        System.out.println(String.format("The efficiency is %.2f%%",average*25/PI));
    }
    public SolidAngle(double hei, double len, double wid, int bin, int it){
        this.height = hei; this.length = len; this.width = wid;
        this.binNum = bin; this.iter = it;
    }
    public double SolidAngleRandomApex(double apexX, double apexY){
        double ans = 0.0, dx = length/binNum, dy = width/binNum;
        double r2,h2 = Math.pow(height,2), dS=dx*dy;
        for(double x=-length/2;x<length/2;x+=dx){
            for(double y=-width/2;y<width/2;y+=dy){
                r2 = Math.pow(apexX-x,2)+Math.pow(apexY-y,2)+h2;
                ans += dS/Math.pow(r2,1.5);
            }
        }
        ans *= height;
        return ans; // returns the solid angle value in radian 3
    }
    public double averageSolidAngleOverRectangle(double sourceLength, double sourceWidth){
        // consider the surface to be rectangle, with its center put at origin
        // symmetry, calculating 1/4 of area is enough
        // use randomized origin
        Random rd = new Random();
        double ans = 0.0, halfLength = sourceLength/2.0, halfWidth = sourceWidth/2.0;
        for(int i=0;i<iter;i++){
            double x=halfLength*rd.nextDouble(),y=halfWidth*rd.nextDouble();
            double cur = SolidAngleRandomApex(x,y);
            ans += cur;
        }
        ans /= iter;
        return ans;
    }
}
