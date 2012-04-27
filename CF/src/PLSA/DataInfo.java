package PLSA;

import java.util.ArrayList;

public class DataInfo {
	
	public static int numUsers = 179106; //6040;
	public static int numItems = 6046;//3952;
	
	public static int hidVariables = 5;   //hidden variables
	
	public static int trainNumber = 1000000;
	
	public static int rating = 2;
	public static int loops = 100;
	
	public static double beta = 0.5;
	
	//P(Z|U)
	public static double[][] Puz = new double[numUsers][hidVariables]; 
	public static double[][][] Psz = new double[numItems][hidVariables][2];
	
	public static int[][] userInfo = new int[numUsers][];
	public static int[][] itemInfo = new int[numItems][];
	
	public static ArrayList<Integer> userTest = new ArrayList<Integer>();
	public static ArrayList<Integer> itemTest = new ArrayList<Integer>();
	public static ArrayList<Double>  rateTest = new ArrayList<Double>();
}
