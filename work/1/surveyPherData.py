import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as patches

width = 100
height = 100
m = 10
n = 10
final_step = 50

def getPherMap():

    for k in range(1,final_step+1):
        fig=plt.figure()
        ax=plt.axes()

        df1 = pd.read_table("./expPher/step"+str(k)+".csv", delimiter=",", header=None)

        im = plt.imshow(df1, vmin=0, vmax=50)
        #カラーバーの表示
        plt.colorbar(im)
        
        plt.axis("scaled")
        ax.set_aspect("equal")
        ax.axis("off")
        plt.subplots_adjust(bottom=0.05, top=0.95)
        fig.savefig("./pics_survey/expPher/step"+str(k)+".png")

        plt.close()
        
    for k in range(1,final_step+1):
        fig=plt.figure()
        ax=plt.axes()
        
        df2 = pd.read_table("./disPher/step"+str(k)+".csv", delimiter=",", header=None)

        im2 = plt.imshow(df2, vmin=0, vmax=50)
        #カラーバーの表示
        plt.colorbar(im2)
        
        plt.axis("scaled")
        ax.set_aspect("equal")
        ax.axis("off")
        plt.subplots_adjust(bottom=0.05, top=0.95)
        fig.savefig("./pics_survey/disPher/step"+str(k)+".png")

        plt.close()

getPherMap()
    