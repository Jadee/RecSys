package RBM;

public class RecordErrors {
	
	public static void rmse() {
		double nrmse = 0, prmse = 0;
		int tc = 0,pc = 0; 
		
		double[][] negvisprobs = new double[DataInfo.numItems][DataInfo.softmax];
		double[]   poshidprobs = new double[DataInfo.totalFeatures];
		
		for(int user = 0; user < DataInfo.numUsers; user++) {
			int trainNumber = DataInfo.TrainSet[user].length;
			int testNumber  = DataInfo.TestSet[user].size();
			
			tc += trainNumber;
			pc += testNumber;
			
			double[] sumW = new double[DataInfo.totalFeatures];
			Function.zero(sumW, DataInfo.totalFeatures);
			Function.zero(negvisprobs, DataInfo.numItems, DataInfo.softmax);
			
			for(int i = 0; i < trainNumber; i++) {
				int item = DataInfo.TrainSet[user][i] / 10;
				int rate = DataInfo.TrainSet[user][i] % 10;
				
				for(int h = 0; h < DataInfo.totalFeatures; h++) {
					sumW[h] += DataInfo.Weights[item][rate][h];
				}
			}
			
			
			for(int h = 0; h < DataInfo.totalFeatures; h++) {
				poshidprobs[h] = 1.0 / (1.0 + Math.exp(0 - sumW[h] - DataInfo.hidbiases[h]));
			}
			
			for(int i = 0; i < trainNumber + testNumber; i++) {
				int item;
				if(i < trainNumber)
					item = DataInfo.TrainSet[user][i] / 10;
				else
					item = DataInfo.TestSet[user].get(i - trainNumber) / 10;
				for(int h = 0; h < DataInfo.totalFeatures; h++) {
					for(int r = 0; r < DataInfo.softmax; r++) 
						negvisprobs[item][r] += poshidprobs[h] * DataInfo.Weights[item][r][h];
				}
				
				for(int r = 0; r < DataInfo.softmax; r++) 
					negvisprobs[item][r] = 1.0 / (1.0 + Math.exp(0 - negvisprobs[item][r] - DataInfo.visbiases[item][r]));
				
				double tsum = 0;
				for(int r = 0; r < DataInfo.softmax; r++) {
					tsum += negvisprobs[item][r];
				}
			
				if(tsum != 0) {
					for(int r = 0; r < DataInfo.softmax; r++) {
						negvisprobs[item][r] /= tsum;
					}
				}
			}
			
			for(int i = 0; i < trainNumber; i++) {
				int item = DataInfo.TrainSet[user][i] / 10;
				int rate = DataInfo.TrainSet[user][i] % 10;
				
				double predict = 0;
				for(int r = 0; r < DataInfo.softmax; r++) {
					predict += r * negvisprobs[item][r];
				}
				double errors = rate - predict;
				nrmse += errors * errors;
			}
			
			for(int i = 0; i < testNumber; i++) {
				int item = DataInfo.TestSet[user].get(i) / 10;
				int rate = DataInfo.TestSet[user].get(i) % 10;
				
				double predict = 0;
				for(int r = 0; r < DataInfo.softmax; r++) {
					predict += r * negvisprobs[item][r];
				}
				
				double p = predict;
				if(p > 1)
					System.out.println(p);
				
				if(rate == 0) {
					double x = -Math.log10(1-p);
					if(Double.isNaN(x)){
						System.err.println("real:  " + rate + "    predict: " + p);
					}
					prmse -= Math.log10(1-p);
				}
				else {
					double x = -Math.log10(p);
					if(Double.isNaN(x)){
						System.err.println("real:  " + rate + "    predict: " + p);
					}
					prmse -= Math.log10(p);
				}
				
				/*double errors = rate - predict;
				prmse += errors * errors;*/
			}
		}

		System.out.println(tc + "	" + pc);
		System.out.println("Train Rmse: " + Math.sqrt(nrmse / tc) + "		Test Rmse: " + Math.sqrt(prmse / pc));
		
	}
}
