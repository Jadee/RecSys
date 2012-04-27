package ALS;

import java.util.Random;

public class InitAls {
	
	//用户特征矩阵和项目特征矩阵只需要初始化一个
	
	public static void initItemFeature() {  

		double sum;
		
		for(int i = 0; i < DataInfo.num_of_item;i++) {		
			sum = 0;
			for(int j = 0; j < DataInfo.itemlist[i].length; j++) {
				int a = DataInfo.itemlist[i][j];
				sum += a % 10;
			}
			DataInfo.itemFeature[i][0] = sum / DataInfo.num_of_user;
		}
		
		Random rand = new Random();
		for (int i = 0; i != DataInfo.num_of_item; ++i) {
			for (int j = 1; j != DataInfo.featureNumber; ++j) {
				DataInfo.itemFeature[i][j] = rand.nextDouble();    //其他的空用随机数填
			}
		}
	}
	
	public static void initUserFeature() {

		double sum;
		
		for(int i = 0; i < DataInfo.num_of_user;i++) {
			sum = 0;
			for(int j = 0; j < DataInfo.userlist[i].length; j++) {
				int a = DataInfo.userlist[i][j];
				sum += a % 10;
			}
			
			DataInfo.userFeature[i][0] = sum / DataInfo.num_of_item;
		}
		
		Random rand = new Random();
		for (int i = 0; i != DataInfo.num_of_user; ++i) {
			for (int j = 1; j != DataInfo.featureNumber; ++j) {
				DataInfo.userFeature[i][j] = rand.nextDouble();    //其他的空用随机数填
			}
		}
	}

}
