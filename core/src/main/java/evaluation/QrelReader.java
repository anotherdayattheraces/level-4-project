package evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;

public class QrelReader {
	private String qrelPath;
	private ArrayList<String> topics;
	
	public QrelReader(String qrelPath, String topicPath){
		this.qrelPath=qrelPath;
		this.topics = readTopics(topicPath);
	}
	public QrelReader(){
		this.qrelPath="C:/Work/Project/samples/treccar/benchmarkY1train/train.benchmarkY1train.cbor.hierarchical.entity.qrels";
		this.topics =readTopics("C:/Work/Project/samples/treccar/topics.txt");
	}
	
	public HashMap<String,ArrayList<Entity>> generateRelevantEntities(){
		HashMap<String,ArrayList<Entity>> mappings = new HashMap<String,ArrayList<Entity>>();
		for(String topic:topics){
			mappings.put(topic, new ArrayList<Entity>());
		}
		FileInputStream inputStream=null;
		try {
			inputStream = new FileInputStream(qrelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(inputStream, "UTF-8");
		while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        String[] elements = line.split(" ");
	        ArrayList<String> newElements= new ArrayList<String>();
	        for(String element:elements){
	        	newElements.add(element.replaceAll("%20", " ")); // spaces are encoded as %20 in the qrels file
	        }
        	String entity = newElements.get(2);
	        String articlePath = newElements.get(0);
	        String[] subjects = articlePath.split("/");
	        String primaryTopic = subjects[0];
	        if(topics.contains(primaryTopic)){
	        	mappings.get(primaryTopic).add(new Entity(entity));
	        	System.out.println("adding mapping from: "+primaryTopic+" to: "+entity);
		}
	        else{
	        	continue;
	        }
		}
		sc.close();
		return mappings;

}
	private ArrayList<String> readTopics(String path){
		ArrayList<String> topics = new ArrayList<String>();
		FileInputStream inputStream=null;
		try {
			inputStream = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(inputStream, "UTF-8");
		while(sc.hasNext()){
			topics.add(sc.next());
		}
		sc.close();
		return topics;
	}
}
