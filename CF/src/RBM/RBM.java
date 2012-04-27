package RBM;

import java.util.Random;

public class RBM {
	
	private static double[][][] CDpos = new double[DataInfo.numItems][DataInfo.softmax][DataInfo.totalFeatures];
	private static double[][][] CDneg = new double[DataInfo.numItems][DataInfo.softmax][DataInfo.totalFeatures];
	private static double[][][] CDinc = new double[DataInfo.numItems][DataInfo.softmax][DataInfo.totalFeatures];
	
	private static double[] poshidact    = new double[DataInfo.totalFeatures];
	private static double[] neghidact    = new double[DataInfo.totalFeatures];
	private static char[]   poshidstates = new char[DataInfo.totalFeatures];
	private static char[]   neghidstates = new char[DataInfo.totalFeatures];
	private static double[] hidbiasinc   = new double[DataInfo.totalFeatures];
	
	private static char[] curposhidstates = new char[DataInfo.totalFeatures];
	
	private static double[][] posvisact  = new double[DataInfo.numItems][DataInfo.softmax];
	private static double[][] negvisact  = new double[DataInfo.numItems][DataInfo.softmax];
	private static double[][] visbiasinc = new double[DataInfo.numItems][DataInfo.softmax];
	private static double[][] negvisprobs = new double[DataInfo.numItems][DataInfo.softmax];
	
	private static char[]   negvissoftmax = new char[DataInfo.numItems];
	private static int[] moviecount = new int[DataInfo.numItems];
	
	public static void initScore() {
		
		int[][] moviercount = new int[DataInfo.numItems][DataInfo.softmax];
		Function.zero(moviercount, DataInfo.numItems, DataInfo.softmax);
		
		for(int user = 0; user < DataInfo.numUsers; user++) {
			int num = DataInfo.TrainSet[user].length;
			
			for(int j = 0; j < num; j++) {
				int m = DataInfo.TrainSet[user][j] / 10;
				int r = DataInfo.TrainSet[user][j] % 10;
				moviercount[m][r]++;
			}	
		}
		
		Random randn = new Random();
		/** Set initial weights */
		for(int i = 0; i < DataInfo.numItems; i++) {
			for(int j = 0; j < DataInfo.totalFeatures; j++) {
				for(int k = 0; k < DataInfo.softmax; k++) {
					/** Normal Distribution */
					DataInfo.Weights[i][k][j] = 0.02 * randn.nextDouble() - 0.01;	        	
				}
			}
		}

		/** Set initial biases */
		Function.zero(DataInfo.hidbiases, DataInfo.totalFeatures);
		
		for(int i = 0; i < DataInfo.numItems; i++) {
			int mtot = 0;
			for(int k = 0; k < DataInfo.softmax; k++) {
				mtot += moviercount[i][k];
			}
			
			for(int k = 0; k < DataInfo.softmax; k++) {
				DataInfo.visbiases[i][k] = Math.log(((double)moviercount[i][k])/((double)mtot));
			}
		}
		
	}

	private static void Zero() {
		Function.zero(CDpos, DataInfo.numItems, DataInfo.softmax, DataInfo.totalFeatures);
		Function.zero(CDneg, DataInfo.numItems, DataInfo.softmax, DataInfo.totalFeatures);
		Function.zero(poshidact, DataInfo.totalFeatures);
		Function.zero(neghidact, DataInfo.totalFeatures);
		Function.zero(posvisact, DataInfo.numItems, DataInfo.softmax);
		Function.zero(negvisact, DataInfo.numItems, DataInfo.softmax);
		Function.zero(moviecount, DataInfo.numItems);
	}
	
	public static void train() {
		
		int loopcount = 0;
		int tSteps = 1;
		Random randn = new Random();
		
		Function.zero(CDinc, DataInfo.numItems, DataInfo.softmax, DataInfo.totalFeatures);
		Function.zero(visbiasinc, DataInfo.numItems, DataInfo.softmax);
		Function.zero(hidbiasinc, DataInfo.totalFeatures);
		
		while(loopcount < DataInfo.loops) {
			
			if ( loopcount >= 10 )
				tSteps = 3 + (loopcount-10) / 5;
			loopcount++;
			
			if ( loopcount > 5 )
	        	DataInfo.momentum = DataInfo.finalMomentum;
			
			Zero();
			System.out.println(loopcount);
			
			for(int user = 0; user < DataInfo.numUsers; user++) {
				
				int num = DataInfo.TrainSet[user].length;
				double[] sumW = new double[DataInfo.totalFeatures];
			    Function.zero(sumW, DataInfo.totalFeatures);
			    
			    Function.zero(negvisprobs, DataInfo.numItems, DataInfo.softmax);
			    
			    for(int i = 0; i < num; i++) {
			    	int m = DataInfo.TrainSet[user][i] / 10;
					int r = DataInfo.TrainSet[user][i] % 10;
					moviecount[m]++;
					
					posvisact[m][r] += 1.0;
					
					for(int h = 0; h < DataInfo.totalFeatures; h++) {
						sumW[h]  += DataInfo.Weights[m][r][h];
					}
			    }
			    
			    for(int h = 0; h < DataInfo.totalFeatures; h++) {
			    	double probs = 1.0 / (1.0 + Math.exp(-sumW[h] - DataInfo.hidbiases[h]));
			    	if(probs > randn.nextDouble() ) {
			    		poshidstates[h] = 1;
			            poshidact[h] += 1.0;
			    	} else {
			    		poshidstates[h] = 0;
			    	}
			    }
			    
			    
			    for(int h = 0; h < DataInfo.totalFeatures; h++)
			    	curposhidstates[h] = poshidstates[h];
			    
			    
			    /** Make T steps of Contrastive Divergence */
			    int stepT = 0;
			    do {
			    	boolean finalTStep = (stepT+1 >= tSteps);
			    	
			    	for(int i = 0; i < num; i++) {
			    		int m = DataInfo.TrainSet[user][i] / 10;
			    		
			    		for(int h = 0; h < DataInfo.totalFeatures; h++) {
			    			if(curposhidstates[h] == 1) {
			    				for(int r = 0; r < DataInfo.softmax; r++)
			    					negvisprobs[m][r]  += DataInfo.Weights[m][r][h];
			    			}
			    		}
			    		
			    		for(int r = 0; r < DataInfo.softmax; r++)
			    			negvisprobs[m][r]  = 1./(1 + Math.exp(-negvisprobs[m][r] - DataInfo.visbiases[m][r]));
			    		
			    		/** Normalize probabilities */
			    		double tsum  = 0;
			    		for(int r = 0; r < DataInfo.softmax; r++) {
			    			tsum += negvisprobs[m][r];
			    		}
		
			    		if ( tsum != 0 ) {
			    			for(int r = 0; r < DataInfo.softmax; r++) {
			    				negvisprobs[m][r]  /= tsum;
			    			}
			    		}
			    		
			    		double randval = randn.nextDouble();
			    		/*for(int r = 0; r < DataInfo.softmax; r++) {
			    			 if ((randval -= negvisprobs[m][r]) <= 0.0) {
			    				 negvissoftmax[m] = (char) r;
			    				 break;
			    			 }
			    			 negvissoftmax[m] = 4;
			    		}*/
			    		
			            /*if ((randval -= negvisprobs[m][0]) <= 0.0)
			            	negvissoftmax[m] = 0;
			            else if ((randval -= negvisprobs[m][1]) <= 0.0)
			            	negvissoftmax[m] = 1;
			            else if ((randval -= negvisprobs[m][2]) <= 0.0)
			            	negvissoftmax[m] = 2;
			            else if ((randval -= negvisprobs[m][3]) <= 0.0)
			            	negvissoftmax[m] = 3;
			            else *//** The case when ((randval -= negvisprobs[m][4]) <= 0.0) *//*			        	   
			            	negvissoftmax[m] = 4;*/
			    		
			    		if ((randval -= negvisprobs[m][0]) <= 0.0)
			            	negvissoftmax[m] = 0;
			    		else {
			    			negvissoftmax[m] = 1;
						}
			    		
			    		if(finalTStep)
			    			negvisact[m][negvissoftmax[m]] += 1.0;
			    	}
			    	
			    	
			    	Function.zero(sumW, DataInfo.totalFeatures);
			    	for(int i = 0; i < num; i++) {
				    	int m = DataInfo.TrainSet[user][i] / 10;
						
						for(int h = 0; h < DataInfo.totalFeatures; h++) {
							sumW[h]  += DataInfo.Weights[m][negvissoftmax[m]][h];
						}
				    }
				    
				    for(int h = 0; h < DataInfo.totalFeatures; h++) {
				    	double probs = 1.0/(1.0 + Math.exp(-sumW[h] - DataInfo.hidbiases[h]));
				    	
				    	if(probs > randn.nextDouble() ) {
				    		neghidstates[h] = 1;
				    		if(finalTStep)
				    			neghidact[h] += 1.0;
				    	} else {
				    		neghidstates[h] = 0;
				    	}
				    }
				    
				    if(!finalTStep) {
				    	for(int h = 0; h < DataInfo.totalFeatures; h++)
					    	curposhidstates[h] = neghidstates[h];
				    	Function.zero(negvisprobs, DataInfo.numItems, DataInfo.softmax);
				    }	    	
			    	
			    } while ( ++stepT < tSteps );
			    
			    for(int i = 0; i < num; i++) {
			    	int m = DataInfo.TrainSet[user][i] / 10;
					int r = DataInfo.TrainSet[user][i] % 10;
					
					for(int h = 0; h < DataInfo.totalFeatures; h++) {
						if ( poshidstates[h] == 1 ) {
			    			CDpos[m][r][h] += 1.0;
			    		}
			    		CDneg[m][negvissoftmax[m]][h] += (double)neghidstates[h];
					}
				}
			    
			    /** Update weights and biases */
			    update(user, num);		    
			}
			setArgument(loopcount);
			RecordErrors.rmse();
		}
		
	}
	
	private static void update(int user, int num) {
		
		/** Update weights and biases */
		 int bSize = 1000;
		 if(((user + 1) % bSize)==0 || (user + 1) == DataInfo.numUsers) {
			 int numcases = user % bSize;
			 numcases++;
			 
			 /** Update weights */
			 for(int m = 0; m < DataInfo.numItems; m++) {
				 
				 if(moviecount[m] == 0)
					 continue;
				 
				 /** For all hidden units */
				 for(int h = 0; h < DataInfo.totalFeatures; h++) {
					 
					 for(int r = 0; r < DataInfo.softmax; r++) {
						 double CDp = CDpos[m][r][h];
						 double CDn = CDneg[m][r][h];
						 if ( CDp != 0.0 || CDn != 0.0 ) {
							 CDp /= ((double)moviecount[m]);
							 CDn /= ((double)moviecount[m]);
	
		    					/** Update weights and biases W = W + alpha*ContrastiveDivergence (biases are just weights to neurons that stay always 1.0) */
		    					CDinc[m][r][h] = DataInfo.momentum * CDinc[m][r][h] + DataInfo.epsilonw * ((CDp - CDn) - DataInfo.weightCost * DataInfo.Weights[m][r][h]);
		    					DataInfo.Weights[m][r][h] += CDinc[m][r][h];
						 }
					 } 
				 }
				 
				 /** Update visible softmax biases */
				 for(int r = 0; r < DataInfo.softmax; r++) {
					 if(posvisact[m][r] != 0.0 || negvisact[m][r] != 0.0) {
						 posvisact[m][r] /= ((double)moviecount[m]);
						 negvisact[m][r] /= ((double)moviecount[m]);
						 visbiasinc[m][r] = DataInfo.momentum * visbiasinc[m][r] + DataInfo.epsilonvb * ((posvisact[m][r] - negvisact[m][r]));
						 DataInfo.visbiases[m][r]  += visbiasinc[m][r];
					 }
				 }
			 }	 
				 
			 /** Update hidden biases */
			 for(int h = 0; h < DataInfo.totalFeatures; h++) {
				 if ( poshidact[h]  != 0.0 || neghidact[h]  != 0.0 ) {
					 poshidact[h]  /= ((double)(numcases));
					 neghidact[h]  /= ((double)(numcases));
					 hidbiasinc[h] = DataInfo.momentum * hidbiasinc[h] + DataInfo.epsilonhb * ((poshidact[h] - neghidact[h]));
					 DataInfo.hidbiases[h]  += hidbiasinc[h];
				 }
			 }
			 
			 Zero();
			 
		 }
	}
	
	private static void setArgument(int loopcount) {
		if(DataInfo.totalFeatures == 200) {
			 if ( loopcount > 6 ) {
				 DataInfo.epsilonw  *= 0.90;
				 DataInfo.epsilonvb *= 0.90;
				 DataInfo.epsilonhb *= 0.90;
			 } else if ( loopcount > 5 ) {  // With 200 hidden variables, you need to slow things down a little more
				 DataInfo.epsilonw  *= 0.50;         // This could probably use some more optimization
				 DataInfo.epsilonvb *= 0.50;
				 DataInfo.epsilonhb *= 0.50;
			 } else if ( loopcount > 2 ) {
				 DataInfo.epsilonw  *= 0.70;
				 DataInfo.epsilonvb *= 0.70;
				 DataInfo.epsilonhb *= 0.70;
			 }
		} else {
			 if ( loopcount > 8 ) {
				 DataInfo.epsilonw  *= 0.92;
				 DataInfo.epsilonvb *= 0.92;
				 DataInfo.epsilonhb *= 0.92;
			 } else if ( loopcount > 6 ) { 
				 DataInfo.epsilonw  *= 0.90;        
	           	 DataInfo.epsilonvb *= 0.90;
	           	 DataInfo.epsilonhb *= 0.90;
			 } else if ( loopcount > 2 ) {
				 DataInfo.epsilonw  *= 0.78;
				 DataInfo.epsilonvb *= 0.78;
				 DataInfo.epsilonhb *= 0.78;
			 }
		}
	}
}
