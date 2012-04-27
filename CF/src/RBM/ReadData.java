package RBM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStreamReader;

public class ReadData {
	
	public static void readTrainData(String trainPath, String split_Sign) throws IOException {		
		File file = new File(trainPath);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		boolean flag = true;
		int tmp = 0, count = 0;
		
		int[] userIndex = new int[DataInfo.numUsers];
		int[] user_record = new int[DataInfo.trainNumber];
		
		for(int i = 0; i < DataInfo.numUsers; i++)
			userIndex[i] = 0;

		while(buffRead.ready()) {
			String line = buffRead.readLine();
			String[] parts = line.split(split_Sign);
			
			int id1  = Integer.parseInt(parts[0]);
			int id2  = Integer.parseInt(parts[1]);
			int rate = (int)Double.parseDouble(parts[2]);
			
			rate = id2 * 10 + rate;

			user_record[count] = rate;
			
			if(flag)
				tmp = id1;
			else {
				if(tmp != id1) {
					userIndex[tmp] = count;
					tmp = id1;
				}
			}
			count++;
			flag = false;
		}
		userIndex[tmp] = count;		
		buffRead.close();
		
		for(int i = 1; i < DataInfo.numUsers; i++) {
			if(userIndex[i] < userIndex[i - 1])
				userIndex[i] = userIndex[i - 1];
		}
		
		for (int i = 0; i != DataInfo.numUsers; i++) {
			
			int first, last;
			if(i == 0)
				first = 0;
			else {
				first = userIndex[i - 1];
			}
			last = userIndex[i];
			
			int number = last - first;
			DataInfo.TrainSet[i] = new int[number];
			
			int p = 0;
			for(int j = first; j < last; j++) {
				DataInfo.TrainSet[i][p] = user_record[j];
				p++;
			}
		}
		buffRead.close();
	}
	
	public static void ReadTestData(String testPath, String split_Sign) throws IOException {
		
		File file = new File(testPath);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		int user, item, rate;
		while(buffRead.ready()) {
			String line = buffRead.readLine();
			String[] parts = line.split(split_Sign);
		
			user = Integer.parseInt(parts[1]);
			item = Integer.parseInt(parts[2]);
			rate = Integer.parseInt(parts[0]);
			
			int record = item * 10 + rate; 
			
			DataInfo.TestSet[user].add(record);
		}
		
		buffRead.close();
	}

}
