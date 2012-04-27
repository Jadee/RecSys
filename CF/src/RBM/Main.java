package RBM;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

	static String trainPath   = "/home/starry/DataSet/competions/track/act_eng/val_ttrain_1_user.csv";
	static String testPath    = "/home/starry/DataSet/competions/track/act_eng/valid_test_cf.csv";
	static String split_Sign  = ",";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		for(int i = 0; i < DataInfo.numUsers; i++)
			DataInfo.TestSet[i] = new ArrayList<Integer>();
		
		ReadData.readTrainData(trainPath, split_Sign);
		ReadData.ReadTestData(testPath, split_Sign);
		
		System.out.println("Begin Rbm!!!");
		long start = System.nanoTime();
		RBM.initScore();
		RBM.train();
		long end   = System.nanoTime();
		System.out.println("Time: " + (end - start));
	}
	
	public static void Init(int loops, int featureNumber) {
		
	}

}
