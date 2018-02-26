package misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dictionary.DictionaryHashMap;
import entityRetrieval.core.Pair;
import evaluation.TopicToEntityMapper;

public class ResultsAnalyzer {
	private String resultFile;
	
	
	public ResultsAnalyzer(String type){
		if(type=="ML"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/Eval/50-STANDARD-ALL-TOPICS/CAT30/MLResults.txt";
		}
		else if(type=="KB"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/Eval/50-STANDARD-ALL-TOPICS/CAT30/KBResults.txt";
		}
		else if(type=="DL"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/Eval/50-STANDARD-ALL-TOPICS/CAT30/DLRResults.txt";

		}
		else if(type=="MM"){
			this.resultFile="C:/Work/Project/samples/prototype4/level-4-project/core/Eval/50-STANDARD-ALL-TOPICS/CAT30/MMResults.txt";
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
				if(pair.getR()>=25){
					System.out.println(pair.getL().replaceAll("%20", " "));
				}
			}
	}
		
		public void findRemovedEntities() throws IOException{
			String extraDetailsFile = "C:/Work/Project/samples/prototype4/level-4-project/core/Eval/50-STANDARD-ALLTOPICS/MLextraDetails.txt";
			String qrelFile = "C:/Work/Project/samples/prototype4/level-4-project/core/OLDfilteredQrels.txt";
			HashMap<String,ArrayList<String>> removedEntities = new HashMap<String,ArrayList<String>>();
			ArrayList<String> topics = TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/OLDtopics.txt");
			FileReader file = new FileReader(extraDetailsFile);
			BufferedReader br = new BufferedReader(file);
			String line;
			String currentTopic = null;
			while((line=br.readLine())!=null){
				if(topics.contains(line.trim())){
					currentTopic=line.trim();
					removedEntities.put(currentTopic, new ArrayList<String>());
				}
				else{
					if(line.startsWith("Removed entity: ")){
						System.out.println(line.substring("Removed entity: ".length()));
						removedEntities.get(currentTopic).add(line.substring("Removed entity: ".length()));
					}
					
				}
			}
			HashMap<String,ArrayList<String>> finalRemovedEntities = new HashMap<String,ArrayList<String>>();
			br.close();
			FileReader file2 = new FileReader(qrelFile);
			BufferedReader br2 = new BufferedReader(file2);
			currentTopic = null;
			Boolean first=false;
			while((line=br2.readLine())!=null){
				String[] split = line.split(" ");
				if(!split[0].replaceAll("%20"," ").equals(currentTopic)){
					currentTopic=split[0].replaceAll("%20", " ");
					finalRemovedEntities.put(currentTopic, new ArrayList<String>());
					System.out.println("new topic: "+currentTopic);
					first=true;
				}
				System.out.println("Current te: "+split[2].replaceAll("%20", " ")+" for topic: "+currentTopic);
			
				for(int i=0;i<removedEntities.get(currentTopic).size();i++){
					if(first){
						System.out.println("removed entity: "+removedEntities.get(currentTopic).get(i));
					}
					if(removedEntities.get(currentTopic).get(i).equals(split[2].replaceAll("%20", " "))){ 
						finalRemovedEntities.get(currentTopic).add(split[2].replaceAll("%20", " "));
						
						break;
					}
				}
				first=false;
			}
			br2.close();
			for(String key:finalRemovedEntities.keySet()){
				for(String entity:finalRemovedEntities.get(key)){
					System.out.println("Removed entity: "+entity+" for topic: "+key);
				}
			}
		}
		public void getINFO(Boolean stats) throws IOException{
			String resultsFile = "C:/Work/Project/samples/prototype4/level-4-project/core/Eval/50-STANDARD-ALL-TOPICS/CAT30/MMfullDetails.txt";
			FileReader file = new FileReader(resultsFile);
			BufferedReader br = new BufferedReader(file);
			String line;
			while((line=br.readLine())!=null){
				if(!line.startsWith("map")) continue;
				//System.out.println(line);
				String[] split = line.split("\\s+");
				if(stats){
					System.out.println(split[2].replaceAll("%20", " "));
				}
				else{
					System.out.println(split[1].replaceAll("%20", " "));
				}
		}
			br.close();
		}
		public void findUnmappedEntities() throws IOException{
			String extraDetailsFile = "C:/Work/Project/samples/prototype4/level-4-project/core/Eval/50-STANDARD-ALL-TOPICS/CAT30/MLextraDetails.txt";
			String qrelFile = "C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
			HashMap<String,ArrayList<String>> removedEntities = new HashMap<String,ArrayList<String>>();
			ArrayList<String> topics = TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
			FileReader file = new FileReader(extraDetailsFile);
			BufferedReader br = new BufferedReader(file);
			String pattern = "Unable to map entity: ";
			String line;
			String currentTopic = null;
			while((line=br.readLine())!=null){
				if(topics.contains(line.trim())){
					currentTopic=line.trim();
					System.out.println(currentTopic);
					removedEntities.put(currentTopic, new ArrayList<String>());
				}
				else{
					if(line.startsWith(pattern)){
						System.out.println(line.substring(pattern.length()));
						removedEntities.get(currentTopic).add(line.substring(pattern.length()));
					}
				}
			}
			HashMap<String,ArrayList<String>> finalRemovedEntities = new HashMap<String,ArrayList<String>>();
			br.close();
			FileReader file2 = new FileReader(qrelFile);
			BufferedReader br2 = new BufferedReader(file2);
			currentTopic = null;
			Boolean first=false;
			while((line=br2.readLine())!=null){
				String[] split = line.split(" ");
				if(!split[0].replaceAll("%20"," ").equals(currentTopic)){
					currentTopic=split[0].replaceAll("%20", " ");
					finalRemovedEntities.put(currentTopic, new ArrayList<String>());
					//System.out.println("new topic: "+currentTopic);
					first=true;
				}
				//System.out.println("Current te: "+split[2].replaceAll("%20", " ")+" for topic: "+currentTopic);
			
				for(int i=0;i<removedEntities.get(currentTopic).size();i++){
					if(first){
						//System.out.println("removed entity: "+removedEntities.get(currentTopic).get(i));
					}
					if(removedEntities.get(currentTopic).get(i).equals(split[2].replaceAll("%20", " ").toLowerCase())){ 
						finalRemovedEntities.get(currentTopic).add(split[2].replaceAll("%20", " "));
						break;
					}
				}
				first=false;
			}
			br2.close();
			for(String key:finalRemovedEntities.keySet()){
				for(String entity:finalRemovedEntities.get(key)){
					System.out.println("Removed entity: "+entity+" for topic: "+key);
				}
			}
		}
	
	
}
