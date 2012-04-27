package PMF;

import java.util.ArrayList;

public class DataInfo {
	
	public static int userNumber = 49290;//9439;//88238;//4696;  //1049511;//6040; 49290
	
	public static int itemNumber = 139738;// 139738; //66726;//3952; 139738
	
	public static short featureNumber = 10;
	
	public static double alpha = 0.001;
	
	public static double lambda = 0.01;
	
	public static int  round = 1000;
	
	public static double mean_rating = 0;
	
	public static int score_record = 0;
	
	public static double[][] userFeature = new double[userNumber][featureNumber];
	
	public static double[][] itemFeature = new double[itemNumber][featureNumber];
	
	public static int trainNumber = 532274;//4851475;
	public static int testNumber  = 132550;//93100	
	
	public static int[] user_record = new int[trainNumber];
	public static int[] item_record = new int[trainNumber];
	public static double[] rate_record = new double[trainNumber];
	public static ArrayList<Integer> userTest = new ArrayList<Integer>();
	public static ArrayList<Integer> itemTest = new ArrayList<Integer>();
	public static ArrayList<Double>  rateTest = new ArrayList<Double>();

}
