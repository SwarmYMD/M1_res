import java.util.ArrayList;

public class Constants{
    public static final int M = 100;
    public static final int N = 100;
    public static final int m = 10;
    public static final int n = 10;
    public static final int W = M/m;
    public static final int H = N/n;
    
    public static final int AGENT_NUM = 500;
    
    // paramaters of this system
    public static final int T_max = 200;
    public static final double alpha = 0.9;
    public static final double c = 0.8;
    public static final double Q = 1.0;
    public static final double tau_max = 100;

    public static final double epsiron = 0.1;
    
    // paramaters of agents
    public static final int range = 3;
    public static final double w = 0.6;
    public static final double c_1 = 1.0;
    public static final double c_2 = 1.0;
    public static final int T = 10;
    public static final double S_g = 0.9;

    public static int speed = 5;
    public static double penalty = 10;

    public static final int[] dir_row = {-1, 1, 0, 0};
    public static final int[] dir_col = {0, 0, -1, 1};
}