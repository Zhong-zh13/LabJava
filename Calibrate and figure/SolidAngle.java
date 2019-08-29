import java.util.*;

class SolidAngle{
    // solve the problem where the interested area is rectangle
    // put the center of rectangle at the xOy origin
    double length,width,height;
    double binNum;
    public static void main(String[] args){
        // first consider a simple version, the source is over the center
        double height = Double.parseDouble(args[0]);
        double a = Double.parseDouble(args[1]);
        double b = Double.parseDouble(args[2]);
        int bin = Integer.parseInt(args[3]);
        double dx = a/bin, dy = b/bin, h2 = height*height, dS = dx*dy;
        double ans = 0.0,r2;
        for(double x=-a/2+dx/2;x<a/2;x+=dx){
            for(double y=-b/2+dy/2;y<b/2;y+=dy){
                r2 = x*x+y*y+h2;
                ans += dS/Math.pow(r2,1.5);
            }
        }
        ans *= height;
        System.out.println(ans/Math.PI/4);
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

}
