package evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class TopicToArticleMapper {
	private String qrelPath;
	private ArrayList<String> topics;
	
	public TopicToArticleMapper(String qrelPath, String topicPath){
		this.qrelPath=qrelPath;
		this.topics = TopicToEntityMapper.readTopics(topicPath);
	}
	public TopicToArticleMapper(){
		this.qrelPath="C:/Work/Project/samples/treccar/benchmarkY1train/train.benchmarkY1train.cbor.hierarchical.qrels";
		this.topics =TopicToEntityMapper.readTopics("C:/Work/Project/samples/treccar/topics.txt");
	}
	
	public HashMap<String,ArrayList<String>> generateTopicEntityMappings(){
		HashMap<String,ArrayList<String>> mappings = new HashMap<String,ArrayList<String>>();
		for(String topic:topics){
			mappings.put(topic, new ArrayList<String>());
		}
		FileInputStream inputStream=null;
		try {
			inputStream = new FileInputStream(qrelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(inputStream, "UTF-8");
		while(sc.hasNext()){
			 String line = sc.nextLine();
		     String[] elements = line.split(" ");
		     ArrayList<String> newElements= new ArrayList<String>();
		     for(String element:elements){
		    	 newElements.add(element.replaceAll("%20", " ")); // spaces are encoded as %20 in the qrels file
		        }
		     
		}
		sc.close();
		return mappings;
	}
	

}
