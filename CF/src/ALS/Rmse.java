package ALS;

import java.util.Iterator;

public class Rmse {
	
	public static double calcRmse() {
		double sum = 0;
		
		@SuppressWarnings("rawtypes")
		Iterator userIter = DataInfo.userTest.iterator();
		@SuppressWarnings("rawtypes")
		Iterator itemIter = DataInfo.itemTest.iterator();
		@SuppressWarnings("rawtypes")
		Iterator rateIter = DataInfo.rateTest.iterator();
		
		while(userIter.hasNext() && itemIter.hasNext() && rateIter.hasNext()) {
			int a = (Integer) userIter.next();
			int b = (Integer) itemIter.next();
			double c = (Double) rateIter.next();
			
			double rate = calcRate(a,b) - c;
			sum += rate * rate;

		}
		
		return Math.sqrt(sum / DataInfo.userTest.size());
	}
	
	private static double calcRate(int a, int b) {
		double rate = 0;
		
		for(int i = 0 ; i < DataInfo.featureNumber; i++) {
			
			rate += DataInfo.userFeature[a][i] * DataInfo.itemFeature[b][i];
		}
		return rate;
	}
}