package PLSA;

import java.io.IOException;
import java.util.Random;

import ALS.ReadData;

public class Main {

	//输入文件的userID，itemID都是从0开始
	//userPath的格式是userID，itemID，rate，组织形式是先按userID排序，再按itemID排序
	//itemPath的格式是itemID，userID，rate，组织形式是先按itemID排序，再按userID排序
	//testPath的格式是userID，itemID，rate
	//split_Sign表示的是输入文件中三元组的分隔符号
	static String userPath   = "/home/starry/DataSet/competions/track/act_eng/val_ttrain_1_user.csv";
	static String itemPath   = "/home/starry/DataSet/competions/track/act_eng/val_ttrain_1_item.csv";
	static String testPath   = "/home/starry/DataSet/competions/track/act_eng/valid_test_cf.csv";
	static String split_Sign = ",";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		long startTime=System.nanoTime();
		try {
			ReadData.init(userPath, itemPath, testPath, split_Sign);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long midTime=System.nanoTime();
		System.out.println("The time of reading Train file： " + (midTime-startTime) + "ns");
		
		init();
		
		
		System.out.println("Begin Training ! ! !");
		
		for(int i = 1; i <= DataInfo.loops; i++) {
			/*if(i % 5 == 0)
	    		DataInfo.beta *= 0.8;*/
			Plsa.init();
			Plsa.computePuz();
			Plsa.computePsz();
			System.out.println("round:  " + i);
			System.out.println("loss: " + Plsa.eval());
		}

	}
	
	private static void init()
	{
		Random random = new Random();
		// Init P(Z|U);
		for(int i = 0; i < DataInfo.numUsers; i++) {
			double norm = 0;
			for(int j = 0; j < DataInfo.hidVariables; j++) {
				DataInfo.Puz[i][j] = random.nextDouble();
				norm += DataInfo.Puz[i][j];
			}
			
			for(int j = 0; j < DataInfo.hidVariables; j++) {
				DataInfo.Puz[i][j] /= norm;
			}
			
		}
		
		double max_d = (1 + DataInfo.rating) / 2;
		max_d = (1 - max_d) * (1 - max_d);
		
		for(int i = 0; i < DataInfo.numItems; i++) {
			for(int j = 0; j < DataInfo.hidVariables; j++) {
				DataInfo.Psz[i][j][0] = random.nextDouble() * (DataInfo.rating - 1) + 1;
				DataInfo.Psz[i][j][1] = random.nextDouble() * 0.01;
			}
		}
	}
}
