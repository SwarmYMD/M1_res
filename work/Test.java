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


            int pattern_num = 0;

            Grid grid = new Grid();
            Agent[] agents = new Agent[Constants.AGENT_NUM];
            FileWriter[] fw = new FileWriter[Constants.T_max];

            for(int i = 0 ; i < Constants.M * Constants.N ; i++) {
                
                if(grid.table[i/Constants.M][i%Constants.M] == 0){
                } else {
                    pattern_num++;
                }
            }

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
                        //calc_sum_pher(agents, agents[j], grid);

                        // PLEASE WRITE THE CONTENT OF SYSTEM HERE!!!!!!!!!

                        
                        if(agents[j].state.equals("t")){
                            // stop
                            enhance(grid, agents[j], agents);
                        }else if(agents[j].state.equals("e")){
                            // exploration
                            exploration(grid, agents[j], agents);
                        }else if(agents[j].state.equals("d")){
                            // dispersion
                            dispersion(grid, agents[j], agents);
                        }
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

    public static void exploration(Grid grid, Agent r, Agent[] agents){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (r.areaNo % Constants.m) * Constants.W;
        rightEnd = leftEnd + Constants.W - 1;
        upperEnd = (r.areaNo / Constants.m) * Constants.H;
        lowerEnd = upperEnd + Constants.H - 1;

        int remain_num = 0;

        // count the number of pattern on grid
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                if(grid.table[i][j] == 1){
                    remain_num++;
                }
            }
        }

        // check the number of occupied grids
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                if(grid.table[i][j] == 1){
                    if(grid.agent_pos[i][j] == 1){
                        remain_num--;
                    }
                }
            }
        }

        // if all pattern grids are occupied, changing mode(otherwise, continuing dispersion mode)
        if(remain_num == 0){
            r.state = "d";
            grid.no_vacancy[r.areaNo] = true;
            //System.out.printf("mode changed to dispersion.\n");
        } else if(grid.table[r.row][r.col] != 1){
            plan_exp(grid, leftEnd, rightEnd, upperEnd, lowerEnd, r, agents);
            grid.no_vacancy[r.areaNo] = false;
            // pheromone update will be written here?
        }else{
            grid.no_vacancy[r.areaNo] = false;
        }

        if(r.length_move != 0){
            r.delta_tau = Constants.Q / r.length_move;
        }
        remain_num = 0;
    }

    public static void plan_exp(Grid grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd, Agent r, Agent[] agents){
        // copy pheromone data of the area which this agent exists
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                r.expPherMatrix[i-upperEnd][j-leftEnd] = grid.expPherData[i][j];
            }
        }

        for(int i=0; i<Constants.H; i++){
            for(int j=0; j<Constants.W; j++){
                r.d_exp = Math.abs(r.row - i - upperEnd) + Math.abs(r.col - j - leftEnd);
                if(r.d_exp != 0){
                    r.expIndicMatrix[i][j] = Math.exp(r.expPherMatrix[i][j]) / r.d_exp;
                } else {
                    r.expIndicMatrix[i][j] = 0;
                }
            }
        }

        int maxIndex = maxIndex(r.expIndicMatrix);
        int a = maxIndex % Constants.W;
        int b = maxIndex / Constants.W;

        r.pld_col = leftEnd + a;
        r.pld_row = upperEnd + b;

        int dif_col, dif_row;

        dif_col = r.pld_col - r.col;
        dif_row = r.pld_row - r.row;

        // I will add the pattern when these two values are "=" later. 
        if(Math.abs(dif_col) >= Math.abs(dif_row)){
            if(dif_col < 0){
                if((r.col + Constants.dir_col[2]) >= leftEnd){
                    if(grid.agent_pos[r.row][r.col + Constants.dir_col[2]] != 1){
                        grid.deletePos(r);
                        r.col = r.col + Constants.dir_col[2];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_col > 0){
                if((r.col + Constants.dir_col[3]) <= rightEnd){
                    if(grid.agent_pos[r.row][r.col + Constants.dir_col[3]] != 1){
                        grid.deletePos(r);
                        r.col = r.col + Constants.dir_col[3];
                        grid.recordPos(r);
                    }
                }
            }
        }else{
            if(dif_row < 0){
                if((r.row + Constants.dir_row[0]) >= upperEnd){
                    if(grid.agent_pos[r.row + Constants.dir_row[0]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[0];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_row > 0){
                if((r.row + Constants.dir_row[1]) <= lowerEnd){
                    if(grid.agent_pos[r.row + Constants.dir_row[1]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[1];
                        grid.recordPos(r);
                    }
                }
            }
        }

        if(grid.table[r.row][r.col] == 1){
            r.state = "t";

            int h = r.areaNo/Constants.m;
            int w = r.areaNo%Constants.m;
            calc_sum_pher(agents, r, grid);
            grid.expPherData[r.row][r.col] = Constants.alpha * grid.expPherData[r.row][r.col] + Constants.c * r.sum_pher;
            if(grid.expPherData[r.row][r.col] > Constants.tau_max){
                grid.expPherData[r.row][r.col] = Constants.tau_max;
            }
            grid.disPherData[h][w] =  Constants.alpha * grid.disPherData[h][w] + Constants.c * r.sum_pher;
            if(grid.disPherData[h][w] > Constants.tau_max){
                grid.disPherData[h][w] = Constants.tau_max;
            }
            grid.alreadyUpdateExp[r.row][r.col] = true;
            grid.alreadyUpdateDis[h][w] = true;
        }
    }

    public static void dispersion(Grid grid, Agent r, Agent[] agents){
        int leftEnd, rightEnd;
        int upperEnd, lowerEnd;
        leftEnd = (r.areaNo % Constants.m) * Constants.W;
        rightEnd = leftEnd + Constants.W - 1;
        upperEnd = (r.areaNo / Constants.m) * Constants.H;
        lowerEnd = upperEnd + Constants.H - 1;

        int remain_num = 0;

        if(grid.table[r.row][r.col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            r.state = "t";

            int h = r.areaNo/Constants.m;
            int w = r.areaNo%Constants.m;
            calc_sum_pher(agents, r, grid);
            grid.expPherData[r.row][r.col] = Constants.alpha * grid.expPherData[r.row][r.col] + Constants.c * r.sum_pher;
            if(grid.expPherData[r.row][r.col] > Constants.tau_max){
                grid.expPherData[r.row][r.col] = Constants.tau_max;
            }
            grid.disPherData[h][w] =  Constants.alpha * grid.disPherData[h][w] + Constants.c * r.sum_pher;
            if(grid.disPherData[h][w] > Constants.tau_max){
                grid.disPherData[h][w] = Constants.tau_max;
            }
            grid.alreadyUpdateExp[r.row][r.col] = true;
            grid.alreadyUpdateDis[h][w] = true;
            return;
        }

        // count the number of pattern on grid
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                if(grid.table[i][j] == 1){
                    remain_num++;
                }
            }
        }

        // check the number of occupied grids
        for(int i=upperEnd; i<=lowerEnd; i++){
            for(int j=leftEnd; j<=rightEnd; j++){
                if(grid.table[i][j] == 1){
                    if(grid.agent_pos[i][j] == 1){
                        remain_num--;
                    }
                }
            }
        }

        // if all pattern grids are occupied, changing mode(otherwise, continuing dispersion mode)
        if(remain_num == 0){
            grid.no_vacancy[r.areaNo] = true;
        }else{
            grid.no_vacancy[r.areaNo] = false;
        }

        if(r.areaNo == r.next_area){
            if(grid.no_vacancy[r.areaNo] == false){
                r.state = "e";
            } else {
                plan_dis(grid, r, agents);
            }
        }

        if(r.length_move != 0){
            r.delta_tau = Constants.Q / r.length_move;
        }
        remain_num = 0;
    }

    public static void plan_dis(Grid grid, Agent r, Agent[] agents){
        for(int i=0; i<Constants.n; i++){
            for(int j=0; j<Constants.m; j++){
               r.disPherMatrix[i][j] = grid.disPherData[i][j];
            }
        }

        for(int i=0; i<Constants.n; i++){
            for(int j=0; j<Constants.m; j++){
                r.d_dis = Math.abs(r.row - (i*Constants.H + Constants.H/2 )) + Math.abs(r.col - (j*Constants.W + Constants.W/2 ));
                if (grid.no_vacancy[i*Constants.m+j] == true){
                    r.disIndicMatrix[i][j] = 0;
                } else if(r.d_dis != 0){
                    r.disIndicMatrix[i][j] = Math.exp(0-r.disPherMatrix[i][j]) / r.d_dis;
                } else {
                    r.disIndicMatrix[i][j] = 0;
                }
            }
        }

        int disIndex = maxIndex(r.disIndicMatrix);
        int area_c = disIndex % Constants.m;
        int area_r = disIndex / Constants.m;

        r.next_area = r.getAreaNo(area_r, area_c);

        int dif_col, dif_row;

        dif_col = (area_c * Constants.W + Constants.W/2) - r.col;
        dif_row = (area_r * Constants.H + Constants.H/2) - r.row;

        // I will add the pattern when these two values are "=" later. 
        if(Math.abs(dif_col) >= Math.abs(dif_row)){
            if(dif_col < 0){
                if((r.col + Constants.dir_col[2]) >= 0){
                    if(grid.agent_pos[r.row][r.col + Constants.dir_col[2]] != 1){
                        grid.deletePos(r);
                        r.col = r.col + Constants.dir_col[2];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_col > 0){
                if((r.col + Constants.dir_col[3]) <= Constants.M){
                    if(grid.agent_pos[r.row][r.col + Constants.dir_col[3]] != 1){
                        grid.deletePos(r);
                        r.col = r.col + Constants.dir_col[3];
                        grid.recordPos(r);
                    }
                }
            }
        }else{
            if(dif_row < 0){
                if((r.row + Constants.dir_row[0]) >= 0){
                    if(grid.agent_pos[r.row + Constants.dir_row[0]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[0];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_row > 0){
                if((r.row + Constants.dir_row[1]) <= Constants.N){
                    if(grid.agent_pos[r.row + Constants.dir_row[1]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[1];
                        grid.recordPos(r);
                    }
                }
            }
        }
    }

    public static void enhance(Grid grid, Agent r, Agent[] agents){
        for(int j = 0; j < 4; j++){
            int a = r.row + Constants.dir_row[j];
            int b = r.col + Constants.dir_col[j];
            calc_sum_pher(agents, r, grid);
            if(a >= 0 && a < Constants.N && b >= 0 && b < Constants.M){
                if(grid.agent_pos[a][b] == 0 && grid.table[a][b] == 1){
                    grid.expPherData[a][b] = grid.expPherData[a][b] + Constants.c * r.sum_pher;
                    if(grid.expPherData[a][b] > Constants.tau_max){
                        grid.expPherData[a][b] = Constants.tau_max;
                    }
                    grid.alreadyUpdateExp[a][b] = true;
                }
            }
        }
    }

    public static int maxIndex(double[][] indic){
        double max = 0;
        List<Integer> maxIndexList = new ArrayList<Integer>();
        for(int i=0; i<indic.length; i++){
            for(int j=0; j<indic[i].length; j++){
                if(max < indic[i][j]){
                    max = indic[i][j];
                } 
            }
        }

        for(int i=0; i<indic.length; i++){
            for(int j=0; j<indic[i].length; j++){
                if(max == indic[i][j]){
                    maxIndexList.add(i*indic[i].length + j);
                } 
            }
        }

        Collections.shuffle(maxIndexList);

        return maxIndexList.get(0);
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
