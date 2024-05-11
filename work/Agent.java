import java.util.*;
import Constants;
import origin_env.Variable;

public class Agent {
    public int col;
    public int row;
    public String state;

    public double[][] expPherMatrix = new double[Variable.H][Variable.W];
    public double[][] expIndicMatrix = new double[Variable.H][Variable.W];
    public double[][] disPherMatrix = new double[Variable.n][Variable.m];
    public double[][] disIndicMatrix = new double[Variable.n][Variable.m];

    public int areaNo;

    public int d_exp;
    public int d_dis;
    public int pld_col;
    public int pld_row;
    public int pgd_col;
    public int pgd_row;

    public double delta_tau;
    public double sum_pher;

    public Agent(int r, int c){
        this.col = c;
        this.row = r;
        this.state = "e";

        for(int i=0; i<Variable.H; i++){
            for(int j=0; j<Variable.W; j++){
                expPherMatrix[i][j] = 0;
                expIndicMatrix[i][j] = 0;
            }
        }
        for(int i=0; i<Variable.n; i++){
            for(int j=0; j<Variable.m; j++){
                disPherMatrix[i][j] = 0;
                disIndicMatrix[i][j] = 0;
            }
        }

        this.areaNo = c / Constants.W + (r / Constants.H) * Constants.m;

        this.d_exp = 0;
        this.d_dis = 0;
        this.pld_col = 0;
        this.pld_row = 0;

        this.delta_tau = 0.0;
        this.sum_pher = 0.0;
    }

    public int getAreaNo(int row, int col){
        int x = col / Constants.W;
        int y = row / Constants.H;
        return x + y * Constants.m;
    }
}
