package RBM;

import java.util.ArrayList;

public class DataInfo {
	
	public static int numItems = 6046;
	public static int numUsers = 179106;
	
	public static int totalFeatures = 100;
	public static int softmax = 2;
	
	public static int trainNumber = 100000000;
	public static int loops = 100;
	
	public static double epsilonw   = 0.001; /** Learning rate for weights */
	public static double epsilonvb  = 0.08; /** Learning rate for biases of visible units */
	public static double epsilonhb  = 0.06; /** Learning rate for biases of hidden units */
	public static double weightCost = 0.01;
	public static double momentum   = 0.8;
	public static double finalMomentum = 0.9;
	
	public static int[][] TrainSet = new int[numUsers][];
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] TestSet = new ArrayList[numUsers];
	
	public static double[][][] Weights   = new double[numItems][softmax][totalFeatures];
	public static double[][]   visbiases = new double[numItems][softmax];
	public static double[]     hidbiases = new double[totalFeatures];

}
