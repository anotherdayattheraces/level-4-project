package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


import entityRetrieval.core.DocumentIdentifier;
import entityRetrieval.core.Entity;
import metamap.MetaMapEntityLinker;

public class MetaMapEvaluator {
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	
	public MetaMapEvaluator(){
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		this.mapping = mapper.generateRelevantEntities();
		Random r = new Random();
		int topicChoice = r.nextInt(mapping.keySet().size()-1);
		Set<String> keySet = mapping.keySet();
		Iterator<String> i = keySet.iterator();
		int count = 0;
		while(count!=topicChoice){
			i.next();
			count++;
		}
		this.query=i.next().toString();
		System.out.println("Chosen query: "+query);
	}
	
	public void evauluate() throws IOException{
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		HashMap<String,ArrayList<Entity>> mapping = mapper.generateRelevantEntities();
		ArrayList<Entity> relevantEntities = mapping.get(query);
		DocumentIdentifier di =  new DocumentIdentifier();
		ArrayList<Long> documents = di.getRelevantDocuments(query);
		MetaMapEntityLinker linker = new MetaMapEntityLinker(documents);
		ArrayList<Entity> returnedEntities = null;
		try {
			returnedEntities = linker.linkArticles().toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		printStatistics(relevantEntities,returnedEntities);
		
	}
	private void printStatistics(ArrayList<Entity> truthEntities, ArrayList<Entity> returnedEntities){
		System.out.println("Number of returned entities: "+returnedEntities.size());
		System.out.println("Number of relevant entities: "+truthEntities.size());
		int matches=0;
		for(Entity te:truthEntities){
			for(Entity re:returnedEntities){
				if(te.getName().equals(re.getName())){
					matches++;
					System.out.println("Relevant & returned entity: "+te.getName());
				}
			}
		}
		System.out.println("Number of relevant returned entities: "+matches);
	}
}
