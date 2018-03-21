package misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Del {
	
	public static void main(String[] args) throws IOException{
		Boolean map = true;
		getStats(map);
	}
	
	public static void getStats(Boolean map) throws IOException{
		String resultsFile = "C:/Work/IR/terrier-core-4.2/previous results/both/output.txt";
		
		FileReader file = new FileReader(resultsFile);
		BufferedReader br = new BufferedReader(file);
		String line;
		while((line=br.readLine())!=null){
			if(line.startsWith("map")){
				if(map){
					int dot = line.indexOf(".");
					System.out.println(line.substring(dot-1));
					
				}
				else{
					int dot = line.indexOf(".");
					System.out.println(line.substring(4,dot-2));
				}	
			}
		}
		System.out.println("------------");
		System.out.println("------------");

		System.out.println("------------");

		br.close();
		getDiff();
	}
	public static void getDiff() throws IOException{
		String baseline = "C:/Work/IR/terrier-core-4.2/previous results/Stage1/output.txt";
		String results = "C:/Work/IR/terrier-core-4.2/previous results/both/output.txt";
		FileReader file = new FileReader(baseline);
		BufferedReader br = new BufferedReader(file);
		String line;
		HashMap<Integer,Double> baselineMAP = new HashMap<Integer,Double>();
		int ups=0;
		int downs=0;
		int same=0;
		while((line=br.readLine())!=null){
			if(line.startsWith("map")){
				if(line.contains("all")) continue;
				int dot = line.indexOf(".");
				//System.out.println(line.substring(dot-3,dot-1).trim());
				double map= Double.parseDouble(line.substring(dot-1).trim());
				int q=Integer.parseInt(line.substring(dot-5,dot-1).trim());
				baselineMAP.put(q, map);
				//System.out.println(map);
			}
		}
		file = new FileReader(results);
		br = new BufferedReader(file);
		int biggestdown = 0;
		int biggestup = 0;
		double biggestdownval=0;
		double biggestupval=0;
		while((line=br.readLine())!=null){
			if(line.startsWith("map")){
				System.out.println(line);
				if(line.contains("all")) continue;
				int dot = line.indexOf(".");
				//System.out.println(line.substring(dot-3,dot-1).trim());
				double map= Double.parseDouble(line.substring(dot-1).trim());
				int q=Integer.parseInt(line.substring(dot-5,dot-1).trim());
				double basemap = baselineMAP.get(q);
				if(basemap<map){
					if(map-basemap>biggestupval){
						biggestup=q;
						biggestupval=map-basemap;
					}
					ups++;
				}
				else if(basemap>map){
					if(basemap-map>biggestdownval){
						biggestdown=q;
						biggestdownval=basemap-map;

					}
					downs++;
				}
				else{
					same++;
				}
			}
		}
			System.out.println("Number of increases: "+ups);
			System.out.println("Number of decreases: "+downs);
			System.out.println("Number of no change: "+same);
			System.out.println("Biggest increase query: "+biggestup+" with change: "+biggestupval);
			System.out.println("Biggest decrease query: "+biggestdown+" with change: "+biggestdownval);



		
	}


}
