import os,sys
import numpy as np
import matplotlib.pylab as plt
import glob
import time

def main():
    file = sys.argv[1]

    data = np.genfromtxt(file,delimiter=',')
    print('Load finished')
    sca = []
    abs = []
    for i in range(0,len(data)):
        e1 = float(data[i][0])
        e2 = float(data[i][1])
        sca.append(e1)
        abs.append(e2)

    plt.figure()
    plt.title("Correlation plot")
    xmin,xmax,xbin = 0,int(1000),10
    if len(sys.argv)>2:
        xmax = int(sys.argv[2])
    X = np.arange(xmin,xmax,xbin)
    xx = np.arange((xmax-xmin)/xbin)
    h,x,y=np.histogram2d(abs,sca,bins=X)
    plt.imshow(np.log(h+1)[:-1,:-1],interpolation='nearest')
    ax = plt.gca()
    ax.invert_yaxis()
    plt.colorbar()
    plt.xlabel('First [keV]')
    plt.ylabel('Second [keV]')
    plt.xticks(xx[::20],X[::20])
    plt.yticks(xx[::20],X[::20])
    plt.show()

if __name__=='__main__':
    main()
    sys.exit("Fin")
