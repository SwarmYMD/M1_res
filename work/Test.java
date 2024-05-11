import java.util.*;

import java.io.*;
import java.nio.charset.Charset;

public class Test {
    public static void main(String[] args){
        for(int n=0; n<1; n++){
            int count = 0;
            int finish_agent = 0;

            double achieved_count = 0.0;
            double achieve_percent = 0.0;

            Grid grid = new Grid();
            Agent[] agents = new Agent[Constants.AGENT_NUM];
            FileWriter[] fw = new FileWriter[Constants.T_max];

            // getting initial positions of agents
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("./"+String.valueOf(n+1)+"/initial_pos.csv"), Charset.forName("Shift-JIS")))) {
            
                String line;
                int index = 0;
                while ((line = reader.readLine()) != null) {
                    if (index >= 0) {
                        String[] data = line.split(",");
                        
                        agents[index] = new Agent(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                        grid.recordPos(agents[index]);
                    }
                    index++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(int i=0; i<Constants.N; i++){
                for(int j=0; j<Constants.M; j++){
                    System.out.print(grid.agent_pos[i][j]);
                }
                System.out.println();
            }

            try{
                FileWriter f = new FileWriter("./"+String.valueOf(n+1)+"/csv/step0.csv");
                for (int j=0; j<Constants.AGENT_NUM; j++){
                    f.append(String.valueOf(agents[j].col));
                    f.append(",");
                    f.append(String.valueOf(agents[j].row));
                    f.append("\n");
                }
                f.close();

                for(int k=0; k<Constants.N; k++){
                    for(int s=0; s<Constants.M; s++){
                        if(grid.table[k][s] == 1){
                            if(grid.agent_pos[k][s] == 1){
                                achieved_count += 1;
                            }
                        }
                    }
                }

                FileWriter percent_recorder = new FileWriter("./"+String.valueOf(n+1)+"/percent/percent.csv");

                achieve_percent = achieved_count / Constants.AGENT_NUM * 100;
                percent_recorder.append(String.valueOf(0));
                percent_recorder.append(",");
                percent_recorder.append(String.valueOf(achieve_percent));
                percent_recorder.append("\n");

                achieved_count = 0;
                
                for(int i=0; i<Constants.T_max; i++){
                
                    System.out.printf("Now: step %d\n", i+1);
                    
                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        agents[j].areaNo = agents[j].getAreaNo(agents[j].row, agents[j].col);
                        calc_sum_pher(agents, agents[j], grid);

                        // PLEASE WRITE THE CONTENT OF SYSTEM HERE!!!!!!!!!

                        /* 
                        if(agents[j].state.equals("t")){
                            // stop
                        }else if(agents[j].state.equals("e")){
                            // exploration
                        }else if(agents[j].state.equals("d")){
                            // dispersion
                        }
                        */
                    }

                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        if(agents[j].state.equals("t")){
                            if(grid.table[agents[j].row][agents[j].col] == 0){
                                agents[j].state = "d";
                            }
                        }
                    }

                    finish_agent = 0;
                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        if(agents[j].state.equals("t")){
                            finish_agent += 1;
                        }
                    }

                    for(int k=0; k<Constants.N; k++){
                        for(int s=0; s<Constants.M; s++){
                            if(grid.table[k][s] == 1){
                                if(grid.agent_pos[k][s] == 1){
                                    achieved_count += 1;
                                }
                            }
                        }
                    }

                    achieve_percent = achieved_count / pattern_num * 100;
                    percent_recorder.append(String.valueOf(i+1));
                    percent_recorder.append(",");
                    percent_recorder.append(String.valueOf(achieve_percent));
                    percent_recorder.append("\n");

                    /*
                    if(agents[0].state.equals(("d"))){
                        for(int k=0; k<Constants.H; k++){
                            for(int s=0; s<Constants.W; s++){
                                System.out.printf("%f,", agents[0].subPherMatrix[k][s]);
                            }
                            System.out.println();
                        }
                        System.out.println();
                    }
                    */

                    
                    fw[i] = new FileWriter("./"+String.valueOf(n+1)+"/csv/step"+String.valueOf(i+1)+".csv");
                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        fw[i].append(String.valueOf(agents[j].col));
                        fw[i].append(",");
                        fw[i].append(String.valueOf(agents[j].row));
                        fw[i].append("\n");
                    }
                    fw[i].close();
                    
                    
                    for(int k=0; k<Constants.N; k++){
                        for(int s=0; s<Constants.M; s++){
                            if(grid.agent_pos[k][s] == 1){
                                count++;
                            }
                        }
                    }
                    
                    if(count != Constants.AGENT_NUM){
                        System.out.printf("Duplicate occured in step %d !\n", i+1);
                    }

                    count = 0;

                    if(finish_agent == Constants.AGENT_NUM){
                        System.out.printf("PF completes.\n");
                        System.out.printf("total steps: %d\n", i+1);
                        for (int j=0; j<Constants.AGENT_NUM; j++){
                            System.out.print(agents[j].state);
                        }
                        System.out.printf("\n");

                        /*
                        for (int j=0; j<Constants.AGENT_NUM; j++){
                            grid.recordPos(agents[j]);
                        }
                        */

                        for(int k=0; k<Constants.N; k++){
                            for(int s=0; s<Constants.M; s++){
                                System.out.print(grid.agent_pos[k][s]);
                            }
                            System.out.println();
                        }
                        System.out.println();

                        for (int j=0; j<Constants.AGENT_NUM; j++){
                            if(grid.agent_pos[agents[j].row][agents[j].col] != 1){
                                System.out.printf("Strange pos: (%d, %d)\n", agents[j].row, agents[j].col);
                            }
                        }
                        

                        break;
                    }

                    
                    achieved_count = 0;
                    

                    //fw[i].close();

                    for(int k=0; k<Constants.N; k++){
                        for(int s=0; s<Constants.M; s++){
                            if(grid.alreadyUpdateExp[k][s] == false){
                                grid.expPherData[k][s] = Constants.alpha * grid.expPherData[k][s];
                                if(grid.expPherData[k][s] > Constants.tau_max){
                                    grid.expPherData[k][s] = Constants.tau_max;
                                }
                            }else{
                                grid.alreadyUpdateExp[k][s] = false;
                            }
                        }
                    }

                    for(int k=0; k<Constants.n; k++){
                        for(int s=0; s<Constants.m; s++){
                            if(grid.alreadyUpdateDis[k][s] == false){
                                grid.disPherData[k][s] = Constants.alpha * grid.disPherData[k][s];
                                if(grid.disPherData[k][s] > Constants.tau_max){
                                    grid.disPherData[k][s] = Constants.tau_max;
                                }
                            }else{
                                grid.alreadyUpdateDis[k][s] = false;
                            }
                        }
                    }

                }

                for(int k=0; k<Constants.N; k++){
                    for(int s=0; s<Constants.M; s++){
                        System.out.print(grid.agent_pos[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void exploration(){

    }

    public static void calc_sum_pher(Agent[] agents, Agent r, Grid grid) {
        //r.sum_pher = 0;
        r.sum_pher = r.delta_tau;
    
        for(int i = r.row - Constants.range; i <= r.row + Constants.range; i++){
            for(int j = r.col - Constants.range; j <= r.col + Constants.range; j++){
                for(int num = 0; num < Constants.AGENT_NUM; num++){
                    if(i >= 0 && i < Constants.N && j >= 0 && j < Constants.M && agents[num] != r){
                        if(agents[num].row == i && agents[num].col == j){
                            if(Math.sqrt((Math.abs(r.col - agents[num].col)^2 + Math.abs(r.row - agents[num].row)^2)) < Constants.range){
                                r.sum_pher += agents[num].delta_tau;
                            }
                        }
                    }
                }
            }
        }
    }
}
