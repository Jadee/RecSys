package PLSA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadData {
	
	public static void init(String userPath, String itemPath, String testPath, String split_Sign) throws IOException {
		collectUserData(userPath, split_Sign);
		System.out.println("complete collect user data");

		collectItemData(itemPath, split_Sign);
		System.out.println("complete collect Item data");

		collectTestData(testPath, split_Sign);
		System.out.println("complete collect test data");
		System.gc();
	}
	
	private static void collectUserData(String userPath, String split_Sign) throws IOException {
		
		File file = new File(userPath);
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
			DataInfo.userInfo[i] = new int[number];
			
			int p = 0;
			for(int j = first; j < last; j++) {
				DataInfo.userInfo[i][p] = user_record[j];
				p++;
			}
		}
		
	}
	
	private static void collectItemData(String itemPath, String split_Sign) throws IOException {
		
		File file = new File(itemPath);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		boolean flag = true;
		int tmp = 0 , count = 0;
		
		int[] itemIndex = new int[DataInfo.numItems];
		int[] item_record = new int[DataInfo.trainNumber];
		
		for(int i = 0; i < DataInfo.numItems; i++)
			itemIndex[i] = 0;
		
		while(buffRead.ready()) {
			String line = buffRead.readLine();
			String[] parts = line.split(split_Sign);
			
			int id1  = Integer.parseInt(parts[0]);
			int id2  = Integer.parseInt(parts[1]);
			int rate = (int)Double.parseDouble(parts[2]);
			
			rate = id2 * 10 + rate;
			
			item_record[count] = rate;
			
			if(flag)
				tmp = id1;
			else {
				if(tmp != id1) {
					itemIndex[tmp] = count;
					tmp = id1;
				}
			}
			count++;
			flag = false;
		}
		itemIndex[tmp] = count;
		buffRead.close();
		
		for(int i = 1; i < DataInfo.numItems; i++) {
			if(itemIndex[i] < itemIndex[i - 1])
				itemIndex[i] = itemIndex[i - 1];
		}
		
		for (int i = 0; i != DataInfo.numItems; i++) {
			
			int first, last;
			if(i == 0)
				first = 0;
			else {
				first = itemIndex[i - 1];
			}
			last = itemIndex[i];
			
			int number = last - first;
			DataInfo.itemInfo[i] = new int[number];
			
			int p = 0;
			for(int j = first; j < last; j++) {
				DataInfo.itemInfo[i][p] = item_record[j];
				p++;
			}
		}
	}
	
	private static void collectTestData(String testPath, String split_Sign) throws IOException {
		
		File file = new File(testPath);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		int user, item;
		double rate;
		while(buffRead.ready()) {
			String line = buffRead.readLine();
			String[] parts = line.split(split_Sign);
		
			user = Integer.parseInt(parts[1]);
			item = Integer.parseInt(parts[2]);
			rate = Double.parseDouble(parts[0]);
			
			DataInfo.userTest.add(user);
			DataInfo.itemTest.add(item);
			DataInfo.rateTest.add(rate);
		}
		buffRead.close();
	}

}
