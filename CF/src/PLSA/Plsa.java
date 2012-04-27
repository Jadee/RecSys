package PLSA;

import java.util.Iterator;

public class Plsa {
	
	static double[][] tPuz   = new double[DataInfo.numUsers][DataInfo.hidVariables]; 
	static double[][][] tPsz = new double[DataInfo.numItems][DataInfo.hidVariables][2];
	
	public static void init()
	{
		for(int i = 0; i < DataInfo.numUsers; i++)
			for(int j = 0; j < DataInfo.hidVariables; j++)
				tPuz[i][j] = DataInfo.Puz[i][j];
		
		for(int i = 0; i < DataInfo.numItems; i++)
			for(int j = 0; j < DataInfo.hidVariables; j++) {
				tPsz[i][j][0] = DataInfo.Psz[i][j][0];
				tPsz[i][j][1] = DataInfo.Psz[i][j][1];
			}
	}
	
	public static void computePuz()
	{
		for(int i = 0; i < DataInfo.numUsers; i++) {
			int numrate = DataInfo.userInfo[i].length;
			
			double[] temp = new double[DataInfo.hidVariables];
			for(int k = 0; k < DataInfo.hidVariables; k++)
				temp[k] = 0;
			
			for(int j = 0; j < numrate; j++)
			{
				int item = DataInfo.userInfo[i][j] / 10;
				int rate = DataInfo.userInfo[i][j] % 10;
				
				double norm = 0;
				for(int k = 0; k < DataInfo.hidVariables; k++)
				{
					double part = Pvyz(rate, item, k) * tPuz[i][k];
					norm += Math.pow(part, DataInfo.beta);
				}
				
				for(int k = 0; k < DataInfo.hidVariables; k++)
				{
					double part = Pvyz(rate, item, k) * tPuz[i][k];
					if(norm != 0) 
						temp[k] += part / norm;
				}
			}
			
			double norm = 0;
			for(int k = 0; k < DataInfo.hidVariables; k++)
			{
				norm += temp[k];
			}
			
			for(int k = 0; k < DataInfo.hidVariables; k++)
			{
				if(norm != 0) 
					DataInfo.Puz[i][k] = temp[k] / norm;
			}
		}
	}
	
	public static void computePsz()
	{
		for(int i = 0; i < DataInfo.numItems; i++)
		{
			int numrate = DataInfo.itemInfo[i].length;
			double[] temp1 = new double[DataInfo.hidVariables];
			double[] temp2 = new double[DataInfo.hidVariables];
			double[] temp3 = new double[DataInfo.hidVariables];
			
			for(int k = 0; k < DataInfo.hidVariables; k++) {
				temp1[k] = 0;
				temp2[k] = 0;
				temp3[k] = 0;
			}
			
			for(int j = 0; j < numrate; j++)
			{
				int user = DataInfo.itemInfo[i][j] / 10;
				int rate = DataInfo.itemInfo[i][j] % 10;
				
				double norm = 0;
				for(int k = 0; k < DataInfo.hidVariables; k++)
				{
					double part = Pvyz(rate, i, k) * tPuz[user][k];
					norm += Math.pow(part, DataInfo.beta);
				}
				
				for(int k = 0; k < DataInfo.hidVariables; k++)
				{
					double part = Pvyz(rate, i, k) * tPuz[user][k];
					if(norm != 0) {
						temp1[k] += part / norm;
						temp2[k] += rate * part / norm;
						temp3[k] += (rate - tPuz[user][k]) * (rate - tPuz[user][k]) * part / norm ;
					}
				}
			}
			
			for(int k = 0; k < DataInfo.hidVariables; k++)
			{
				if(temp1[k] != 0) {
					DataInfo.Psz[i][k][0] = temp2[k] / temp1[k];
					DataInfo.Psz[i][k][1] = temp3[k] / temp1[k];
				}
			}
				
		}
	}
	
	//P(V:U,variance)
	public static double Pvyz(int rate, int item, int label) {
		
		double value = tPsz[item][label][0];
		double variance = tPsz[item][label][1];
		
		double temp = 0 - (rate - value) * (rate - value);
		
		double ans = 0;
		if(variance != 0)
			ans = Math.exp(temp / 2 / variance) / Math.sqrt(Math.PI * 2 * variance );
		return ans;
	}
	
	
	public static double predict(int user, int item) {
		double ans = 0;
		for(int z = 0; z < DataInfo.hidVariables; z++)
			ans += DataInfo.Puz[user][z] * DataInfo.Psz[item][z][0];
		ans -= 1;
		ans = 1 / (1 + Math.exp(-ans));
		return ans;
	}
	
	public static double eval() {
		double loss = 0;
		@SuppressWarnings("rawtypes")
		Iterator userIter = DataInfo.userTest.iterator();
		@SuppressWarnings("rawtypes")
		Iterator ItemIter = DataInfo.itemTest.iterator();
		@SuppressWarnings("rawtypes")
		Iterator rateIter = DataInfo.rateTest.iterator();
		
		
		while(userIter.hasNext() && ItemIter.hasNext() && rateIter.hasNext()) {
			int a = (Integer) userIter.next();
			int b = (Integer) ItemIter.next();
			double c = (Double) rateIter.next();
			
			double p = predict(a, b);
			if(c == 0) {
				double x = -Math.log10(1-p);
				if(Double.isNaN(x)){
					System.err.println("real:  " + c + "    predict: " + p);
				}
				loss -= Math.log10(1-p);
			}
			else {
				double x = -Math.log10(p);
				if(Double.isNaN(x)){
					System.err.println("real:  " + c + "    predict: " + p);
				}
				loss -= Math.log10(p);
			}

		}
		return loss / DataInfo.userTest.size();
	}

}
