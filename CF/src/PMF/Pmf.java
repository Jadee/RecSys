package PMF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Random;

public class Pmf {	

	//输入文件的userID，itemID都是从1开始
	//trainPath的格式是userID，itemID，rate
	//testPath的格式是userID，itemID，rate
	//split_Sign表示的是输入文件中三元组的分隔符号
	
	static String trainPath  = new String("data/train_vec.txt");  
	static String testPath   = new String("data/probe_vec.txt");  
	static String split_Sign = new String(" ");
	
	public static void main(String args[]) throws NumberFormatException, IOException{
		
		initFeature();
		readTrainData(trainPath, split_Sign);
		readTestData(testPath, split_Sign);
		
		
		System.out.println("Begin Training ! ! !");
	
		for(int i = 0; i < DataInfo.round; i++)
		{
			System.out.println("round:  " + (i + 1));
			update_one();
			System.out.println("loss: " + eval());							
		}
		//genResult();
	}
	
	static void initFeature() {
		
		Random rand = new Random();
		
		for(int i = 0; i < DataInfo.userNumber; i++)
			for(int j = 0; j < DataInfo.featureNumber; j++)
				DataInfo.userFeature[i][j] = 0.01 * rand.nextDouble();
		
		for(int i = 0; i < DataInfo.itemNumber; i++)
			for(int j = 0; j < DataInfo.featureNumber; j++)
				DataInfo.itemFeature[i][j] = 0.01 * rand.nextDouble();
	}
	
	
	static void readTrainData(String trainPath, String split_Sign) throws NumberFormatException, IOException
	{
		File file = new File(trainPath);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		double sum = 0;
		int index = 0;
		while(buffRead.ready()) {
			String str = buffRead.readLine();
			String[] parts = str.split(split_Sign);
			
			int user = Integer.parseInt(parts[0]) - 1;
			int item = Integer.parseInt(parts[1]) - 1;
			double rating = Double.parseDouble(parts[2]);
			
			DataInfo.user_record[index] = user;
			DataInfo.item_record[index] = item;
			DataInfo.rate_record[index] = rating;
			
			index++;
			sum += rating;
		}
		
		DataInfo.mean_rating = sum / DataInfo.trainNumber;
		for(int i = 0; i < DataInfo.trainNumber; i++) {
			double tmp = (Double)DataInfo.rate_record[i] - DataInfo.mean_rating;
			DataInfo.rate_record[i] = tmp;
		}
		buffRead.close();
	}
	
	static void readTestData(String testPath, String split_Sign) throws IOException {
		
		File file = new File(testPath);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		int user, item;
		double rate;
		while(buffRead.ready()) {
			String str = buffRead.readLine();
			String[] parts = str.split(split_Sign);	
			
			user = Integer.parseInt(parts[0]) - 1;
			item = Integer.parseInt(parts[1]) - 1;
			rate = Double.parseDouble(parts[2]);			
			
			DataInfo.userTest.add(user);
			DataInfo.itemTest.add(item);
			DataInfo.rateTest.add(rate);
		}
		
		buffRead.close();
	}


	static double predict(int userId, int itemId) {
		
		double pre = 0;
		for(int i = 0; i < DataInfo.featureNumber; i++) 
			pre += DataInfo.userFeature[userId][i] * DataInfo.itemFeature[itemId][i];
		return pre;
	}
	
	public static void update_one() {
		for (int i = 0; i < DataInfo.trainNumber; i++) {
			
			int user = (Integer) DataInfo.user_record[i];
			int item = (Integer) DataInfo.item_record[i];
			double rate = (Double) DataInfo.rate_record[i];

			double vary = predict(user, item) - rate;
			
			for(int j = 0; j < DataInfo.featureNumber; j++)
			{
				double tmp = vary * DataInfo.itemFeature[item][j] + DataInfo.lambda * DataInfo.userFeature[user][j];
				DataInfo.userFeature[user][j] = DataInfo.userFeature[user][j] - DataInfo.alpha * tmp;
			}
			
			for(int j = 0; j < DataInfo.featureNumber; j++)
			{
				double tmp = vary * DataInfo.userFeature[user][j] + DataInfo.lambda * DataInfo.itemFeature[item][j];
				DataInfo.itemFeature[item][j] = DataInfo.itemFeature[item][j] - DataInfo.alpha * tmp;
			}
			
		}
	}

	static double eval() {
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
			
			double rate = predict(a, b) + DataInfo.mean_rating;
			
			if(rate < 0)
				rate = 0;
			if(rate > 5)
				rate = 5;
			
			rate = rate - c;
			loss += rate * rate;
			
			/*if(c == 0) {
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
			}*/
		}
		
		return Math.sqrt(loss / DataInfo.userTest.size());
	}
	
	/*static void genResult() throws IOException
	{
		BufferedReader reader =new BufferedReader(new FileReader("/home/starry/DataSet/competions/track/sat_write/test_cf.csv"));
		BufferedWriter writer = new BufferedWriter(new FileWriter("/home/starry/DataSet/competions/track/sat_write/result.csv"));
		
		ArrayList<Integer> user = new ArrayList<Integer>();
		ArrayList<Integer> item = new ArrayList<Integer>();
		
		while(reader.ready()) {
			String str = reader.readLine();
			String[] parts = str.split(split_sign);
			int a = Integer.parseInt(parts[0]);
			int b = Integer.parseInt(parts[1]);
			user.add(a);
			item.add(b);
		}
		reader.close();
		
		@SuppressWarnings("rawtypes")
		Iterator userIter = user.iterator();
		@SuppressWarnings("rawtypes")
		Iterator ItemIter = item.iterator();
		
		while(userIter.hasNext() && ItemIter.hasNext()) {
			int a = (Integer) userIter.next();
			int b = (Integer) ItemIter.next();
			double p = predict(a, b);
			writer.write(a + "," + b + "," + p + "\n");
		}
		writer.flush();
		writer.close();
	}*/
}
