package ALS;

import java.io.IOException;

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
	
	public static void main(String[] args) {
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
		
		System.out.println("Begin Training ! ! !");
		
		InitAls.initItemFeature();
		Als als = new Als();
		
		for(int i = 0; i < DataInfo.round; i++) {
			als.genU();
			als.genM();
			System.out.println("RMSE = " + Rmse.calcRmse() + "\n");
		}
		
		long endTime=System.nanoTime();
		System.out.println("The time of Training： " + (endTime-midTime) + "ns");
	}

}
