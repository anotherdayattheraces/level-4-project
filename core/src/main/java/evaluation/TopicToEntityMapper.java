package evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Document.DocumentComponents;

import dictionary.DictionaryHashMap;
import dictionary.SnomedDictionaryInitializer;
import entityRetrieval.core.Entity;
import knowledgeBase.KBFilter;
import misc.CategoryGenerator;

public class TopicToEntityMapper {
	private String qrelPath;
	private ArrayList<String> topics;
	private DictionaryHashMap dictionary;
	private String categoryPath;
	private ArrayList<String> topCategories;
	private String kbPath;
	private String filteredQrelPath;
	
	
	public TopicToEntityMapper(String qrelPath, String topicPath){
		this.qrelPath=qrelPath;
		this.topics = readTopics(topicPath);
	}
	public TopicToEntityMapper(){
		this.filteredQrelPath="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.kbPath="C:/Work/Project/samples/Unprocessed_Index";
		this.categoryPath="C:/Work/Project/samples/prototype4/level-4-project/core/categories.txt";
		this.qrelPath="C:/Work/Project/samples/treccar/benchmarkY1train/train.benchmarkY1train.cbor.hierarchical.entity.qrels";
		this.topics =readTopics("C:/Work/Project/samples/treccar/topics.txt");
		this.topCategories=KBFilter.readInCategories(categoryPath);
		SnomedDictionaryInitializer init = new SnomedDictionaryInitializer();
		try {
			this.dictionary = init.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public DictionaryHashMap getDictionary(){
		return this.dictionary;
	}
	
	public HashMap<String,ArrayList<Entity>> filterQrels(){
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
		Boolean seen = false;
		while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        String[] elements = line.split(" ");
	        ArrayList<String> newElements= new ArrayList<String>();
	        for(String element:elements){
	        	element = element.replaceAll("%E2%80%93", "-"); //- encoded as %E2%80%93 - you wont believe how much frustration/time - vs – caused me
	        	newElements.add(element.replaceAll("%20", " ")); // spaces are encoded as %20 in the qrels file
	        }
        	String entity = newElements.get(2);
        	//if(entity.contains("(")){
        	//	int pos = entity.lastIndexOf("(");
        	//	entity = entity.substring(0, pos-1);
        	//}
	        String articlePath = newElements.get(0);
	        String[] subjects = articlePath.split("/");
	        String primaryTopic = subjects[0];
	        if(topics.contains(primaryTopic)){
	        	//System.out.println(line);
	        	if(filterConceptByCategory(entity)){
	        		Boolean exists = false;
	        		for(Entity e:mappings.get(primaryTopic)){
	        			if(e.getName().equals(entity)){
	        				exists = true;
	        			}
	        		}
	        		if(!exists){
	        			//System.out.println("Adding entity: "+entity+" to topic: "+primaryTopic);
			        	mappings.get(primaryTopic).add(new Entity(entity));
	        		}
	        		
	        	}
	        	
	        	
		}
	        else{
	        	continue;
	        }
		}
		sc.close();
		return mappings;

}
	public HashMap<String,ArrayList<Entity>> generateRelevantEntities(String entityToMap){
		HashMap<String,ArrayList<Entity>> mappings = new HashMap<String,ArrayList<Entity>>();
		for(String topic:topics){
			mappings.put(topic, new ArrayList<Entity>());
		}
		FileInputStream inputStream=null;
		try {
			inputStream = new FileInputStream(filteredQrelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(inputStream, "UTF-8");
		Boolean seen = false;
		while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        String[] elements = line.split(" ");
	        ArrayList<String> newElements= new ArrayList<String>();
	        for(String element:elements){
	        	element = element.replaceAll("%E2%80%93", "-"); //- encoded as %E2%80%93 - you wont believe how much frustration/time - vs – caused me
	        	newElements.add(element.replaceAll("%20", " ")); // spaces are encoded as %20 in the qrels file
	        }
        	String entity = newElements.get(2);
        	//if(entity.contains("(")){
        	//	int pos = entity.lastIndexOf("(");
        	//	entity = entity.substring(0, pos-1);
        	//}
	        String articlePath = newElements.get(0);
	        String[] subjects = articlePath.split("/");
	        String primaryTopic = subjects[0];
	        if(primaryTopic.equals(entityToMap)){
	        	seen = true;
	        }
	        if(!primaryTopic.equals(entityToMap)&&seen){
	        	break;
	        }
	        if(!primaryTopic.equals(entityToMap)){
	        	continue;
	        }
	        if(topics.contains(primaryTopic)){
	        	//System.out.println(line);
	        	if(filterConceptByCategory(entity)){
	        		Boolean exists = false;
	        		for(Entity e:mappings.get(primaryTopic)){
	        			if(e.getName().equals(entity)){
	        				exists = true;
	        			}
	        		}
	        		if(!exists){
	        			//System.out.println("Adding entity: "+entity+" to topic: "+primaryTopic);
			        	mappings.get(primaryTopic).add(new Entity(entity));
	        		}
	        		
	        	}
		}
	        else{
	        	continue;
	        }
		}
		sc.close();
		return mappings;

}
	
	public static ArrayList<String> readTopics(String path){
		ArrayList<String> topics = new ArrayList<String>();
		FileInputStream inputStream=null;
		try {
			inputStream = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(inputStream, "UTF-8");
		Boolean firstLine = true;
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			if(firstLine){ //poor code to deal with the fact scanner would read in mystery character on first line that appeared as whitespace char but wasnt
				line = line.substring(1);
				firstLine=false;
			}
			if(line.length()==0) continue;
			int numEnd=line.indexOf(" ");
			
			topics.add(line.substring(numEnd+1));
		}
		sc.close();
		return topics;
	}
	
	public ArrayList<String> getTopics(){
		return this.topics;
	}
	public Boolean filterConcept(String conceptName){
		if(dictionary.lookupString(conceptName)){
			return true;
		}
		return false;
	}
	public Boolean filterConceptByCategory(String conceptName){
		DiskIndex index=null;
		try {
			index = new DiskIndex(kbPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
		ArrayList<String> entityCategories = CategoryGenerator.findEntityCategories(index, conceptName, dc);

		if(entityCategories==null){
			return false;
		}
		for(String category:entityCategories){
			if(topCategories.contains(category)){
				return true;
			}
		}
		return false;
	}
	
	public void saveFilteredQrels(HashMap<String,ArrayList<Entity>> mappings){
		File fout = new File("filteredQrels"+".txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		for(String key:mappings.keySet()){
			for(Entity entity:mappings.get(key)){
				try {
					bw.write(key.replaceAll(" ", "%20")+" 0 "+entity.getName().replaceAll(" ", "%20")+" 1");
					bw.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
