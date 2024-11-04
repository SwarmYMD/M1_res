import java.util.*;

import java.io.FileWriter;
import java.io.IOException;

public class PositionBroad{
    public static void main(String[] args) {
        Grid grid = new Grid();

        int initial_pos;

        ArrayList<Integer> randomList = new ArrayList<Integer>();

        for(int n = 0; n < 1 ; n++){
            for(int i = 0 ; i < Constants.M * Constants.N ; i++) {
                
                if(grid.table[i/Constants.M][i%Constants.M] == 0){
                    
                    //if((i%Constants.M > 30 && i%Constants.M < 173) && (i/Constants.M > 30 && i/Constants.M < 173)){
                    //} else {
                    
                        randomList.add(i);
                    //}
                }
                
                //randomList.add(i);
            }
            Collections.shuffle(randomList);

            Agent[] agents = new Agent[Constants.AGENT_NUM];
            for (int i=0; i<Constants.AGENT_NUM; i++){
                initial_pos = randomList.get(i);
                agents[i] = new Agent(initial_pos%Constants.M, initial_pos/Constants.M);
                grid.recordPos(agents[i]);
                //System.out.printf("(%d, %d)\n", agents[i].row, agents[i].col);
            }

            try{
                FileWriter f = new FileWriter("./"+String.valueOf(n+1)+"/initial_pos_broad.csv");
                for (int j=0; j<Constants.AGENT_NUM; j++){
                    f.append(String.valueOf(agents[j].col));
                    f.append(",");
                    f.append(String.valueOf(agents[j].row));
                    f.append("\n");
                }
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            randomList.clear();
        }
    }
}
