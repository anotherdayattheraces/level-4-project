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
		int topicChoice = r.nextInt(mapping.keySet().size());
		topicChoice = 4;
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
	
		
		int matches=0;
		ArrayList<String> idMatches = new ArrayList<String>();
		for(Entity te:truthEntities){
			for(Entity re:returnedEntities){
				if(te.getName().substring(0, 3).toLowerCase().equals(re.getName().substring(0, 3).toLowerCase())){
					System.out.println("possible match: "+te.getName()+" "+re.getName());
				}
				if(te.getName().toLowerCase().equals(re.getName().toLowerCase())){
					matches++;
					idMatches.add(te.getId());
					System.out.println("Relevant & returned entity: "+re.getName()+ " "+re.getId());
				}
			}
		}
		System.out.println("Number of returned entities: "+returnedEntities.size());
		System.out.println("Number of relevant entities: "+truthEntities.size());
		System.out.println("Number of relevant returned entities: "+matches);
	}
}
