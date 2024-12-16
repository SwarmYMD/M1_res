import java.util.*;

public class Agent {
    public int col;
    public int row;
    public String state;
    public int number;

    public double[][] expPherMatrix = new double[Constants.H][Constants.W];
    public double[][] expIndicMatrix = new double[Constants.H][Constants.W];
    public double[][] disPherMatrix = new double[Constants.n][Constants.m];
    public double[][] disIndicMatrix = new double[Constants.n][Constants.m];

    public int areaNo;
    public int next_area;
    public int pre_next_area;

    public int objPos;

    public int d_exp;
    public int d_dis;
    public int pld_col;
    public int pld_row;
    public int pgd_col;
    public int pgd_row;

    public double v_col = 0;
    public double v_row = 0;
    public int x_col = 0;
    public int x_row = 0;

    public double delta_tau;
    public double sum_pher;

    public int length_move;

    public int dir_flag;

    int not_move_count;

    double rand;

    public Agent(int r, int c){
        this.col = c;
        this.row = r;
        this.state = "e";
        this.number = 0;

        for(int i=0; i<Constants.H; i++){
            for(int j=0; j<Constants.W; j++){
                expPherMatrix[i][j] = 0;
                expIndicMatrix[i][j] = 0;
            }
        }
        for(int i=0; i<Constants.n; i++){
            for(int j=0; j<Constants.m; j++){
                disPherMatrix[i][j] = 0;
                disIndicMatrix[i][j] = 0;
            }
        }

        this.areaNo = c / Constants.W + (r / Constants.H) * Constants.m;
        this.next_area = -1;
        this.pre_next_area = -2;

        this.objPos = 0;

        this.d_exp = 0;
        this.d_dis = 0;
        this.pld_col = -1;
        this.pld_row = -1;
        this.pgd_col = -1;
        this.pgd_row = -1;

        this.delta_tau = 1.0;
        this.sum_pher = 0.0;

        this.length_move = 0;

        this.not_move_count = 0;

        this.dir_flag = 0;
    }

    public int getAreaNo(int row, int col){
        int x = col / Constants.W;
        int y = row / Constants.H;
        return x + y * Constants.m;
    }
}
