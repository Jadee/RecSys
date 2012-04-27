package ALS;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

public class Als {
	
	static DoubleMatrix2D subItemFeature;

	static DoubleMatrix2D subUserFeature;
	
	DoubleMatrix1D Ri, Rj;

	DoubleMatrix2D E;
	
	
	/*
	 * generate the userFeature matrix
	 */
	public void genU() {

		for (int i = 0; i != DataInfo.num_of_user; i++) {
			genUi(i);		
		}
	}

	/*
	 * generate the itemFeature matrix
	 */
	public void genM() {

		for (int j = 0; j != DataInfo.num_of_item; j++) {
			genMj(j);
		}
	}
	
	private void initSubItemFeature(int[] column, int userRateNumber) {
		
		int i,j;
		double feature;
		
		for(i = 0; i < userRateNumber; i++) {
			for(j = 0; j < DataInfo.featureNumber; j++) {
				feature = DataInfo.itemFeature[column[i]][j];
				subItemFeature.setQuick(j, i, feature);
			}
		}
	}
	
	
	private void genUi(int userId) {
		
		int userRatedNumber = DataInfo.userlist[userId].length;   //用户评过分的项目个数
		
		if (userRatedNumber == 0) {
			
			for (int j = 0; j != DataInfo.featureNumber; j++) {
				DataInfo.userFeature[userId][j] = 0;
			}
			return;
		}
		
		int[] columnIndexes = new int[userRatedNumber];
		double[] rate = new double[userRatedNumber];
		
		for(int j = 0; j < DataInfo.userlist[userId].length; j++) {
			int a = DataInfo.userlist[userId][j];
			columnIndexes[j] = a / 10;
			rate[j] = a % 10;
		}
				
		
		Ri = DataInfo.factory1D.make(userRatedNumber);
		subItemFeature = DataInfo.factory2D.make(DataInfo.featureNumber, userRatedNumber);
		
		Ri.assign(rate);
		initSubItemFeature(columnIndexes, userRatedNumber);


		Algebra algebra = new Algebra(0);
		Algebra algebra2 = new Algebra(0);
		
		E = DataInfo.factory2D.identity(DataInfo.featureNumber);
		
		DoubleMatrix1D Ui;
		Ui = DataInfo.factory1D.make(DataInfo.featureNumber, 0);
		
		Ui = algebra.mult(
				// inverse of Ai
				algebra2.inverse(subItemFeature.zMult(subItemFeature.viewDice(), E,
						1, DataInfo.lambda * userRatedNumber, false,false)),
				// Vi
				algebra.mult(subItemFeature, Ri));
		
		for (int k = 0; k < DataInfo.featureNumber; k++) {
			DataInfo.userFeature[userId][k] = Ui.getQuick(k);
		}
	}
	
	private void initSubUserFeature(int[] column, int movieRateNumber) {
		
		int i,j;
		double feature;
		
		for(i = 0; i < movieRateNumber; i++) {
			for(j = 0; j < DataInfo.featureNumber; j++) {
				feature = DataInfo.userFeature[column[i]][j];
				subUserFeature.setQuick(j, i, feature);
			}
		}
	}
	
	private void genMj(int itemId) {
		
		int itemRatedNumber = DataInfo.itemlist[itemId].length;   //项目被评过分的个数
		
		if (itemRatedNumber == 0) {
			
			for (int j = 0; j != DataInfo.featureNumber; j++) {
				DataInfo.itemFeature[itemId][j] = 0;
			}
			return;
		}
		
		int[] columnIndexes = new int[itemRatedNumber];
		double[] rate = new double[itemRatedNumber];
		
		for(int j = 0; j < DataInfo.itemlist[itemId].length; j++) {
			int a = DataInfo.itemlist[itemId][j];
			columnIndexes[j] = a / 10;
			rate[j] = a % 10;
		}
		
		Rj = DataInfo.factory1D.make(itemRatedNumber);
		subUserFeature = DataInfo.factory2D.make(DataInfo.featureNumber, itemRatedNumber);
		
		Rj.assign(rate);
		initSubUserFeature(columnIndexes, itemRatedNumber);

		Algebra algebra = new Algebra(0);
		Algebra algebra2 = new Algebra(0);
		
		
		E = DataInfo.factory2D.identity(DataInfo.featureNumber);
		
		DoubleMatrix1D Mj;
		Mj = DataInfo.factory1D.make(DataInfo.featureNumber, 0);
		
		Mj = algebra.mult(
				// inverse of Ai
						algebra2.inverse(subUserFeature.zMult(subUserFeature.viewDice(), E,
								1, DataInfo.lambda * itemRatedNumber, false,false)),
				// Vi
						algebra.mult(subUserFeature, Rj));
		
		for (int k = 0; k < DataInfo.featureNumber; k++) {
			DataInfo.itemFeature[itemId][k] = Mj.getQuick(k);
		}
	}

}
