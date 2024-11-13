import java.util.*;

import java.io.*;
import java.nio.charset.Charset;

public class Resist_multi {
    public static void main(String[] args){
        for(int n=0; n<1; n++){
            int count = 0;
            int finish_agent = 0;

            double achieved_count = 0.0;
            double achieve_percent = 0.0;

            Random random = new Random();

            int pattern_num = 0;

            Grid grid = new Grid();
            List<Agent> agentList = new ArrayList<Agent>();
            FileWriter[] fw = new FileWriter[Constants.T_max];
            FileWriter[] direction_recorder = new FileWriter[Constants.T_max];

            Agent ag;

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
                        
                        Agent a = new Agent(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                        a.number = index;
                        agentList.add(a);
                        grid.recordPos(a);
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
                FileWriter f = new FileWriter("./"+String.valueOf(n+1)+"/csv_multi/step0.csv");
                Map<Integer, Agent> numberMap = new HashMap<>();
                for (int j=0; j<Constants.AGENT_NUM; j++){
                    ag = agentList.get(j);
                    numberMap.put(ag.number, ag);
                }
                for (int j=0; j<Constants.AGENT_NUM; j++){
                    ag = numberMap.get(j);
                    f.append(String.valueOf(ag.col));
                    f.append(",");
                    f.append(String.valueOf(ag.row));
                    f.append(",");
                    f.append(String.valueOf(ag.state));
                    f.append(",");
                    f.append(String.valueOf(ag.dir_flag));
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

                FileWriter percent_recorder = new FileWriter("./"+String.valueOf(n+1)+"/percent_multi/percent.csv");

                achieve_percent = achieved_count / Constants.AGENT_NUM * 100;
                percent_recorder.append(String.valueOf(0));
                percent_recorder.append(",");
                percent_recorder.append(String.valueOf(achieve_percent));
                percent_recorder.append("\n");

                achieved_count = 0;
                
                for(int i=0; i<Constants.T_max; i++){
                
                    System.out.printf("Now: step %d\n", i+1);
                    Collections.shuffle(agentList);
                    
                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        ag = agentList.get(j);
                        ag.areaNo = ag.getAreaNo(ag.row, ag.col);

                        // PLEASE WRITE THE CONTENT OF SYSTEM HERE!!!!!!!!!
                        if(ag.state.equals("t")){
                            // enhance neighbor and sometimes move
                            //enhance(grid, ag, agentList);
                            /*
                            if(ag.not_move_count >= 5){
                                move(grid, ag, agentList);
                            }
                            */
                            move(grid, ag, agentList);
                            //exploration(grid, ag, agentList);
                            if(agentList.get(j).col == ag.col && agentList.get(j).row == ag.row){
                                ag.not_move_count++;
                            }else{
                                ag.not_move_count = 0;
                            }
                        }else if(ag.state.equals("e")){
                            // exploration
                            exploration(grid, ag, agentList);
                        }else if(ag.state.equals("d")){
                            // dispersion
                            dispersion(grid, ag, agentList);
                        }

                        agentList.set(j, ag);
                    }

                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        ag = agentList.get(j);
                        if(ag.state.equals("t")){
                            if(grid.table[ag.row][ag.col] == 0){
                                ag.state = "d";
                                agentList.set(j, ag);
                            }
                        }
                    }

                    finish_agent = 0;
                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        ag = agentList.get(j);
                        if(ag.state.equals("t")){
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

                    
                    fw[i] = new FileWriter("./"+String.valueOf(n+1)+"/csv_multi/step"+String.valueOf(i+1)+".csv");
                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        ag = numberMap.get(j);
                        fw[i].append(String.valueOf(ag.col));
                        fw[i].append(",");
                        fw[i].append(String.valueOf(ag.row));
                        fw[i].append(",");
                        fw[i].append(String.valueOf(ag.state));
                        fw[i].append(",");
                        fw[i].append(String.valueOf(ag.dir_flag));
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
                            ag = agentList.get(j);
                            System.out.print(ag.state);
                        }
                        System.out.printf("\n");


                        for(int k=0; k<Constants.N; k++){
                            for(int s=0; s<Constants.M; s++){
                                System.out.print(grid.agent_pos[k][s]);
                            }
                            System.out.println();
                        }
                        System.out.println();

                        for (int j=0; j<Constants.AGENT_NUM; j++){
                            ag = agentList.get(j);
                            if(grid.agent_pos[ag.row][ag.col] != 1){
                                System.out.printf("Strange pos: (%d, %d)\n", ag.row, ag.col);
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

                    for (int j=0; j<Constants.AGENT_NUM; j++){
                        ag = agentList.get(j);
                        if(i != Constants.T_max-1){
                            ag.dir_flag = 0;
                        }
                        agentList.set(j, ag);
                    }

                }
                percent_recorder.close();

                for(int k=0; k<Constants.N; k++){
                    for(int s=0; s<Constants.M; s++){
                        System.out.print(grid.agent_pos[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();

                /*

                for(int k=0; k<Constants.n; k++){
                    for(int s=0; s<Constants.m; s++){
                        System.out.printf("%2.2f, ", grid.disPherData[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();

                for(int k=0; k<Constants.n; k++){
                    for(int s=0; s<Constants.m; s++){
                        System.out.printf("%2.3f, ", agentList.get(0).disIndicMatrix[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();

                for(int k=0; k<Constants.n; k++){
                    for(int s=0; s<Constants.m; s++){
                        System.out.printf("%d, ", Math.abs(agentList.get(0).row - (k*Constants.H + Constants.H/2 )) + Math.abs(agentList.get(0).col - (s*Constants.W + Constants.W/2 )));
                    }
                    System.out.println();
                }
                System.out.println();

                int[][] dir_list = new int[Constants.N][Constants.M];
                String[][] state_list = new String[Constants.N][Constants.M];

                for(int k=0; k<Constants.N; k++){
                    for(int s=0; s<Constants.M; s++){
                        dir_list[k][s] = 0;
                        state_list[k][s] = "0";
                    }
                }
                
                for (int j=0; j<Constants.AGENT_NUM; j++){
                    ag = agentList.get(j);
                    dir_list[ag.row][ag.col] = 1;
                    state_list[ag.row][ag.col] = ag.state;
                }

                
                for(int k=0; k<Constants.N; k++){
                    for(int s=0; s<Constants.M; s++){
                        System.out.print(dir_list[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();
                */
                

                /*
                for(int k=0; k<Constants.N; k++){
                    for(int s=0; s<Constants.M; s++){
                        System.out.print(state_list[k][s]);
                    }
                    System.out.println();
                }
                System.out.println();
                */

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void exploration(Grid grid, Agent r, List<Agent> agentList){
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

        int pre_col = r.col;
        int pre_row = r.row;

        // if all pattern grids are occupied, changing mode(otherwise, continuing dispersion mode)
        if(remain_num == 0){
            r.state = "d";
            grid.no_vacancy[r.areaNo] = true;
            //System.out.printf("mode changed to dispersion.\n");
        } else if(r.state.equals("t")){
            plan_t(grid, leftEnd, rightEnd, upperEnd, lowerEnd, r, agentList);
            // pheromone update will be written here?
        } else if(grid.table[r.row][r.col] != 1){
            plan_exp(grid, leftEnd, rightEnd, upperEnd, lowerEnd, r, agentList);
            grid.no_vacancy[r.areaNo] = false;
            // pheromone update will be written here?
        }else{
            grid.no_vacancy[r.areaNo] = false;
        }

        if(r.col == pre_col && r.rpw == pre_row){
            r.not_move_count++;
        }

        if(r.length_move != 0){
            r.delta_tau = Constants.Q / r.length_move;
        }
        remain_num = 0;
    }

    public static void plan_exp(Grid grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd, Agent r, List<Agent> agentList){
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

        int enhanced_eps = Constants.epsiron * r.not_move_count;

        // RECONFIG!!!!!!
        // I will add the pattern when these two values are "=" later. 

        /*
        if(Math.abs(dif_col) >= Math.abs(dif_row)){
            if(dif_col < 0){
                if((r.col + Constants.dir_col[2]) >= leftEnd){
                    r.dir_flag = 1; // left
                    if(grid.agent_pos[r.row][r.col + Constants.dir_col[2]] != 1){
                        grid.deletePos(r);
                        r.col = r.col + Constants.dir_col[2];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_col > 0){
                if((r.col + Constants.dir_col[3]) <= rightEnd){
                    r.dir_flag = 2; // right
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
                    r.dir_flag = 3; // up
                    if(grid.agent_pos[r.row + Constants.dir_row[0]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[0];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_row > 0){
                if((r.row + Constants.dir_row[1]) <= lowerEnd){
                    r.dir_flag = 4; // down
                    if(grid.agent_pos[r.row + Constants.dir_row[1]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[1];
                        grid.recordPos(r);
                    }
                }
            }
        }
        */

        double col_rand = (double) (Math.abs(dif_col) / (Math.abs(dif_col) + Math.abs(dif_row)));
        Random random = new Random();
        double pso_rand = random.nextDouble();
        r.rand = random.nextDouble();

        int next_col = r.col;
        int next_row = r.row;

        //if(r.rand >= Constants.epsiron){
        if(r.rand >= enhanced_eps){
            r.rand = random.nextDouble();
            if(r.rand <= col_rand){
                if(dif_col < 0){
                    r.v_col = ((Constants.w * r.v_col + Constants.c_1 * pso_rand * (r.pld_col - r.col)));
                    if(r.v_col < -3){
                        r.v_col = -3;
                    }
                    r.x_col = r.col + (int)(Math.ceil(r.v_col));
                    for(int i=r.col - 1; i >= r.x_col; i--){
                        if(i >= leftEnd){
                            if(grid.agent_pos[r.row][i] != 1){
                                next_col = i;
                                r.dir_flag = 1; // left
                            }else{
                                r.v_col = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.col = next_col;
                    grid.recordPos(r);
                }else if(dif_col > 0){
                    r.v_col = ((Constants.w * r.v_col + Constants.c_1 * pso_rand * (r.pld_col - r.col)));
                    if(r.v_col > 3){
                        r.v_col = 3;
                    }
                    r.x_col = r.col + (int)(Math.ceil(r.v_col));
                    for(int i=r.col + 1; i <= r.x_col; i++){
                        if(i <= rightEnd){
                            if(grid.agent_pos[r.row][i] != 1){
                                next_col = i;
                                r.dir_flag = 2; // right
                            }else{
                                r.v_col = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.col = next_col;
                    grid.recordPos(r);
                }
            } else {
                if(dif_row < 0){
                    r.v_row = ((Constants.w * r.v_row + Constants.c_1 * pso_rand * (r.pld_row - r.row)));
                    if(r.v_row < -3){
                        r.v_row = -3;
                    }
                    r.x_row = r.row + (int)(Math.ceil(r.v_row));
                    for(int i=r.row - 1; i >= r.x_row; i--){
                        if(i >= upperEnd){
                            if(grid.agent_pos[i][r.col] != 1){
                                next_row = i;
                                r.dir_flag = 3; // up
                            }else{
                                r.v_row = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.row = next_row;
                    grid.recordPos(r);
                }else if(dif_row > 0){
                    r.v_row = ((Constants.w * r.v_row + Constants.c_1 * pso_rand * (r.pld_row - r.row)));
                    if(r.v_row > 3){
                        r.v_row = 3;
                    }
                    r.x_row = r.row + (int)(Math.ceil(r.v_row));
                    for(int i=r.row + 1; i <= r.x_row; i++){
                        if(i <= lowerEnd){
                            if(grid.agent_pos[i][r.col] != 1){
                                next_row = i;
                                r.dir_flag = 4; // down
                            }else{
                                r.v_row = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.row = next_row;
                    grid.recordPos(r);
                }
            }
        } else {
            r.v_col = 0;
            r.v_row = 0;
            randomMove(grid, r, agentList);
        }

        if(grid.table[r.row][r.col] == 1){
            r.state = "t";

            int h = r.areaNo/Constants.m;
            int w = r.areaNo%Constants.m;
            calc_sum_pher(agentList, r, grid);
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

    

    public static void plan_t(Grid grid, int leftEnd, int rightEnd, int upperEnd, int lowerEnd, Agent r, List<Agent> agentList){
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
        /*
        if(Math.abs(dif_col) >= Math.abs(dif_row)){
            if(dif_col < 0){
                if((r.col + Constants.dir_col[2]) >= leftEnd){
                    r.dir_flag = 1; // left
                    if(grid.agent_pos[r.row][r.col + Constants.dir_col[2]] != 1){
                        grid.deletePos(r);
                        r.col = r.col + Constants.dir_col[2];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_col > 0){
                if((r.col + Constants.dir_col[3]) <= rightEnd){
                    r.dir_flag = 2; // right
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
                    r.dir_flag = 3; // up
                    if(grid.agent_pos[r.row + Constants.dir_row[0]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[0];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_row > 0){
                if((r.row + Constants.dir_row[1]) <= lowerEnd){
                    r.dir_flag = 4; // down
                    if(grid.agent_pos[r.row + Constants.dir_row[1]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[1];
                        grid.recordPos(r);
                    }
                }
            }
        }
        */

        double col_rand = (double) (Math.abs(dif_col) / (Math.abs(dif_col) + Math.abs(dif_row)));
        Random random = new Random();
        r.rand = random.nextDouble();

        if(r.rand >= Constants.epsiron){
            r.rand = random.nextDouble();
            if(r.rand <= col_rand){
                if(dif_col < 0){
                    if((r.col + Constants.dir_col[2]) >= leftEnd){
                        r.dir_flag = 1; // left
                        if(grid.agent_pos[r.row][r.col + Constants.dir_col[2]] != 1){
                            grid.deletePos(r);
                            r.col = r.col + Constants.dir_col[2];
                            grid.recordPos(r);
                        }
                    }
                }else if(dif_col > 0){
                    if((r.col + Constants.dir_col[3]) <= rightEnd){
                        r.dir_flag = 2; // right
                        if(grid.agent_pos[r.row][r.col + Constants.dir_col[3]] != 1){
                            grid.deletePos(r);
                            r.col = r.col + Constants.dir_col[3];
                            grid.recordPos(r);
                        }
                    }
                }
            } else {
                if(dif_row < 0){
                    if((r.row + Constants.dir_row[0]) >= upperEnd){
                        r.dir_flag = 3; // up
                        if(grid.agent_pos[r.row + Constants.dir_row[0]][r.col] != 1){
                            grid.deletePos(r);
                            r.row = r.row + Constants.dir_row[0];
                            grid.recordPos(r);
                        }
                    }
                }else if(dif_row > 0){
                    if((r.row + Constants.dir_row[1]) <= lowerEnd){
                        r.dir_flag = 4; // down
                        if(grid.agent_pos[r.row + Constants.dir_row[1]][r.col] != 1){
                            grid.deletePos(r);
                            r.row = r.row + Constants.dir_row[1];
                            grid.recordPos(r);
                        }
                    }
                }
            }
        } else {
            randomMove(grid, r, agentList);
        }

        if(grid.table[r.row][r.col] == 1){
            r.state = "t";

            int h = r.areaNo/Constants.m;
            int w = r.areaNo%Constants.m;
            calc_sum_pher(agentList, r, grid);
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

    public static void dispersion(Grid grid, Agent r, List<Agent> agentList){
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
            calc_sum_pher(agentList, r, grid);
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

        int pre_col = r.col;
        int pre_row = r.row;

        if(r.areaNo == r.next_area){
            if(grid.no_vacancy[r.areaNo] == false){
                r.state = "e";
            } else {
                plan_dis(grid, r, agentList);
            }
        } else {
            plan_dis(grid, r, agentList);
        }

        if(r.col == pre_col && r.rpw == pre_row){
            r.not_move_count++;
        }

        if(r.length_move != 0){
            r.delta_tau = Constants.Q / r.length_move;
        }
        remain_num = 0;
    }

    public static void plan_dis(Grid grid, Agent r, List<Agent> agentList){
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

        r.pgd_col = area_c * Constants.W + Constants.W/2;
        r.pgd_row = area_r * Constants.H + Constants.H/2;

        dif_col = r.pgd_col - r.col;
        dif_row = r.pgd_row - r.row;

        int next_col = r.col;
        int next_row = r.row;

        int enhanced_eps = Constants.epsiron * r.not_move_count;

        // I will add the pattern when these two values are "=" later.
        
        /*
        if(Math.abs(dif_col) >= Math.abs(dif_row)){
            if(dif_col < 0){
                if((r.col + Constants.dir_col[2]) >= 0){
                    r.dir_flag = 1; // left
                    if(grid.agent_pos[r.row][r.col + Constants.dir_col[2]] != 1){
                        grid.deletePos(r);
                        r.col = r.col + Constants.dir_col[2];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_col > 0){
                if((r.col + Constants.dir_col[3]) <= Constants.M){
                    r.dir_flag = 2; // right
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
                    r.dir_flag = 3; // up
                    if(grid.agent_pos[r.row + Constants.dir_row[0]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[0];
                        grid.recordPos(r);
                    }
                }
            }else if(dif_row > 0){
                if((r.row + Constants.dir_row[1]) <= Constants.N){
                    r.dir_flag = 4; // down
                    if(grid.agent_pos[r.row + Constants.dir_row[1]][r.col] != 1){
                        grid.deletePos(r);
                        r.row = r.row + Constants.dir_row[1];
                        grid.recordPos(r);
                    }
                }
            }
        }
        */

        double col_rand = (double) (Math.abs(dif_col) / (Math.abs(dif_col) + Math.abs(dif_row)));
        Random random = new Random();
        double pso_rand = random.nextDouble();
        r.rand = random.nextDouble();
        
        /*
        if(r.number == 1){
            System.out.println(r.rand);
            System.out.printf("%d, %d\n", dif_col, dif_row);
        }
        */
        
        //if(r.rand >= Constants.epsiron){
        if(r.rand >= enhanced_eps){
            r.rand = random.nextDouble();
            if(r.rand <= col_rand){
                //if(r.number == 1) System.out.println("rand <= col_rand");
                if(dif_col < 0){
                    r.v_col = ((Constants.w * r.v_col + Constants.c_2 * pso_rand * (r.pgd_col - r.col)));
                    if(r.v_col < -3){
                        r.v_col = -3;
                    }
                    r.x_col = r.col + (int)(Math.ceil(r.v_col));
                    for(int i=r.col - 1; i >= r.x_col; i--){
                        if(i >= 0){
                            if(grid.agent_pos[r.row][i] != 1){
                                next_col = i;
                                r.dir_flag = 1; // left
                            }else{
                                r.v_col = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.col = next_col;
                    grid.recordPos(r);
                }else if(dif_col > 0){
                    r.v_col = ((Constants.w * r.v_col + Constants.c_2 * pso_rand * (r.pgd_col - r.col)));
                    if(r.v_col > 3){
                        r.v_col = 3;
                    }
                    r.x_col = r.col + (int)(Math.ceil(r.v_col));
                    for(int i=r.col + 1; i <= r.x_col; i++){
                        if(i < Constants.M){
                            if(grid.agent_pos[r.row][i] != 1){
                                next_col = i;
                                r.dir_flag = 2; // right
                            }else{
                                r.v_col = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.col = next_col;
                    grid.recordPos(r);
                }
            } else {
                if(dif_row < 0){
                    r.v_row = ((Constants.w * r.v_row + Constants.c_2 * pso_rand * (r.pgd_row - r.row)));
                    if(r.v_row < -3){
                        r.v_row = -3;
                    }
                    r.x_row = r.row + (int)(Math.ceil(r.v_row));
                    for(int i=r.row - 1; i >= r.x_row; i--){
                        if(i >= 0){
                            if(grid.agent_pos[i][r.col] != 1){
                                next_row = i;
                                r.dir_flag = 3; // up
                            }else{
                                r.v_row = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.row = next_row;
                    grid.recordPos(r);
                }else if(dif_row > 0){
                    r.v_row = ((Constants.w * r.v_row + Constants.c_2 * pso_rand * (r.pgd_row - r.row)));
                    if(r.v_row > 3){
                        r.v_row = 3;
                    }
                    r.x_row = r.row + (int)(Math.ceil(r.v_row));
                    for(int i=r.row + 1; i <= r.x_row; i++){
                        if(i < Constants.M){
                            if(grid.agent_pos[i][r.col] != 1){
                                next_row = i;
                                r.dir_flag = 4; // down
                            }else{
                                r.v_row = 0;
                                break;
                            }
                        }
                    }
                    grid.deletePos(r);
                    r.row = next_row;
                    grid.recordPos(r);
                }
            }
        } else {
            r.v_col = 0;
            r.v_row = 0;
            randomMove(grid, r, agentList);
        }

        if(grid.table[r.row][r.col] == 1){
            //System.out.printf("this agent already reached goal.\n");
            r.state = "t";

            int h = r.areaNo/Constants.m;
            int w = r.areaNo%Constants.m;
            calc_sum_pher(agentList, r, grid);
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
    }

    public static void enhance(Grid grid, Agent r, List<Agent> agentList){
        for(int j = 0; j < 4; j++){
            int a = r.row + Constants.dir_row[j];
            int b = r.col + Constants.dir_col[j];
            calc_sum_pher(agentList, r, grid);
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

    public static void move(Grid grid, Agent r, List<Agent> agentList){
        double[] indicator = new double[4]; 
        for(int j = 0; j < 4; j++){
            int a = r.row + Constants.dir_row[j];
            int b = r.col + Constants.dir_col[j];

            int dis = 1;
            if(a >= 0 && a < Constants.N && b >= 0 && b < Constants.M){
                if(grid.table[a][b] != 1){
                    indicator[j] = 0;
                } else {
                    indicator[j] = Math.exp(0-grid.expPherData[a][b]) / dis;
                }
            }
        }
        int maxIndex = maxIndex2(indicator);
        // resistance
        /*
        int next_r = r.row - Constants.dir_row[maxIndex];
        int next_c = r.col - Constants.dir_col[maxIndex];
        */

        int next_r = r.row + Constants.dir_row[maxIndex];
        int next_c = r.col + Constants.dir_col[maxIndex];

        if(next_c - r.col == -1){
            r.dir_flag = 1; //left
        } else if (next_c - r.col == 1){
            r.dir_flag = 2; //right
        } else if (next_r - r.row == -1){
            r.dir_flag = 3; //up
        } else if (next_r -r.row == 1){
            r.dir_flag = 4; //down
        }

        if(grid.agent_pos[next_r][next_c] != 1 && grid.table[next_r][next_c] == 1){
            grid.deletePos(r);
            r.row = next_r;
            r.col = next_c;

            calc_sum_pher(agentList, r, grid);
            grid.expPherData[next_r][next_c] = grid.expPherData[next_r][next_c] + Constants.c * r.sum_pher;
                if(grid.expPherData[next_r][next_c] > Constants.tau_max){
                    grid.expPherData[next_r][next_c] = Constants.tau_max;
                }
            grid.alreadyUpdateExp[next_r][next_c] = true;

            grid.recordPos(r);
            //r.not_move_count = -1;
        } else {
            List<Integer> candidates = new ArrayList<Integer>();
            for(int j = 0; j < 4; j++){
                int a = r.row + Constants.dir_row[j];
                int b = r.col + Constants.dir_col[j];
    
                if(a >= 0 && a < Constants.N && b >= 0 && b < Constants.M){
                    if(grid.table[a][b] == 1 && grid.agent_pos[a][b] != 1){
                        candidates.add(a*Constants.N + b);
                    }
                }
            }

            if(candidates.size() != 0){
                Collections.shuffle(candidates);
                int next = candidates.get(0);

                grid.deletePos(r);
                r.row = next / Constants.N;
                r.col = next % Constants.N;

                calc_sum_pher(agentList, r, grid);
                grid.expPherData[r.row][r.col] = grid.expPherData[r.row][r.col] + Constants.c * r.sum_pher;
                    if(grid.expPherData[r.row][r.col] > Constants.tau_max){
                        grid.expPherData[r.row][r.col] = Constants.tau_max;
                    }
                grid.alreadyUpdateExp[r.row][r.col] = true;

                grid.recordPos(r);
            }

            candidates.clear();
        }
    }

    public static void randomMove(Grid grid, Agent r, List<Agent> agentList){
        double[] indicator = new double[4]; 
        for(int j = 0; j < 4; j++){
            int a = r.row + Constants.dir_row[j];
            int b = r.col + Constants.dir_col[j];

            int dis = 1;
            if(a >= 0 && a < Constants.N && b >= 0 && b < Constants.M){
                indicator[j] = dis;
            }
        }
        int maxIndex = maxIndex2(indicator);
        int next_r = r.row + Constants.dir_row[maxIndex];
        int next_c = r.col + Constants.dir_col[maxIndex];

        if(grid.agent_pos[next_r][next_c] != 1 && grid.table[next_r][next_c] == 1){
            grid.deletePos(r);
            r.row = next_r;
            r.col = next_c;

            grid.recordPos(r);
            //r.not_move_count = -1;
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

    public static int maxIndex2(double[] indic){
        double max = 0;
        List<Integer> maxIndexList = new ArrayList<Integer>();
        for(int i=0; i<indic.length; i++){
            if(max < indic[i]){
                max = indic[i];
            } 
        }

        for(int i=0; i<indic.length; i++){
            if(max == indic[i]){
                maxIndexList.add(i);
            } 
        }

        Collections.shuffle(maxIndexList);

        return maxIndexList.get(0);
    }

    public static void calc_sum_pher(List<Agent> agentList, Agent r, Grid grid) {
        //r.sum_pher = 0;
        r.sum_pher = r.delta_tau;
    
        for(int i = r.row - Constants.range; i <= r.row + Constants.range; i++){
            for(int j = r.col - Constants.range; j <= r.col + Constants.range; j++){
                for(int num = 0; num < Constants.AGENT_NUM; num++){
                    if(i >= 0 && i < Constants.N && j >= 0 && j < Constants.M && agentList.get(num) != r){
                        Agent a = agentList.get(num);
                        if(a.row == i && a.col == j){
                            if(Math.sqrt((Math.abs(r.col - a.col)^2 + Math.abs(r.row - a.row)^2)) < Constants.range){
                                r.sum_pher += a.delta_tau;
                            }
                        }
                    }
                }
            }
        }
    }
}
