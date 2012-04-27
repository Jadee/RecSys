package PMF;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class User_Sim {
	
	@SuppressWarnings("unchecked")
	public static void collectFriendsInfo() throws IOException {
		
		FileReader file = new FileReader("data/trust.txt");//trust.txt
		BufferedReader buffRead = new BufferedReader(file);
		
		int user, friend;
		for(int i = 0; i < DataInfo.userNumber; i++)
			Pmf_SNS.friends[i] = new Stack<Integer>();
 
		while(buffRead.ready()) {
			String str = buffRead.readLine();
			String[] parts = str.split("	");
			
			user = Integer.parseInt(parts[0]) - 1;
			friend = Integer.parseInt(parts[1]) - 1;
 			
			Pmf_SNS.friends[user].push(friend);
			//friends[friend].push(user);
					
		}
		System.out.println("Complete collect friends data");
		buffRead.close();
		file.close();
	}
	
	public static double sim(int user, int friend) {
		double sim1 = 0;
		double sim2 = 0;
		double sim3 = 0;
		double sim = 0;
				
		for(int j = 0; j < DataInfo.featureNumber; j++) {
			sim1 += DataInfo.userFeature[user][j] * DataInfo.userFeature[friend][j];
			sim2 += DataInfo.userFeature[user][j] * DataInfo.userFeature[user][j];
			sim3 += DataInfo.userFeature[friend][j] * DataInfo.userFeature[friend][j];
		}
				
		if(sim1 == 0)
			sim = 0;
		else {
			sim = sim1 / (Math.sqrt(sim2) * Math.sqrt(sim3));
			sim = (sim + 1) / 2;
		}
		return sim;
	}

}
