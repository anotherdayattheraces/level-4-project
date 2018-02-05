package misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dictionary.DictionaryHashMap;
import entityRetrieval.core.Pair;

public class ResultsAnalyzer {
	private String resultFile;
	
	
	public ResultsAnalyzer(String type){
		if(type=="ML"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/MLResults.txt";
		}
		else if(type=="KB"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/KBResults.txt";
		}
		else if(type=="DL"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/DLRResults.txt";

		}
		else if(type=="MM"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/MMResults.txt";
		}
		else{
			this.resultFile=null;
		}
	}
		
		public void findCommonEntities(){
			FileReader file = null;
			try {
				file = new FileReader(resultFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedReader br = new BufferedReader(file);
			String line;
			HashMap<String,Integer> entityToApps = new HashMap<String,Integer>();
			try {
				while((line=br.readLine())!=null){
					String[] lineSplit = line.split(" ");
					String entity = lineSplit[2];
					if(!entityToApps.containsKey(entity)){
						entityToApps.put(entity, 1);
					}
					else{
						entityToApps.put(entity, 1+entityToApps.get(entity));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Pair<String,Integer>> sortedEntities = new ArrayList<Pair<String,Integer>>();
			while(!entityToApps.isEmpty()){
				int max=0;
				String maxKey=null;
				for(String key:entityToApps.keySet()){
					if(entityToApps.get(key)>max){
						max=entityToApps.get(key);
						maxKey=key;
					}
			}
				sortedEntities.add(new Pair<String, Integer>(maxKey,max));
				entityToApps.remove(maxKey);
				
			}
			for(Pair<String,Integer> pair:sortedEntities){
				System.out.println(pair.getL()+" apps: "+pair.getR());
			}
	}
	
	
}
