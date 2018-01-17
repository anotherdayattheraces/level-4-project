package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


import entityRetrieval.core.DocumentIdentifier;
import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;
import entityRetrieval.core.Pair;
import metamap.MetaMapEntityLinker;

public class MetaMapEvaluator {
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	
	public MetaMapEvaluator(){
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		this.mapping = mapper.generateRelevantEntities();
		Random r = new Random();
		int topicChoice = r.nextInt(mapping.keySet().size());
		//topicChoice = 3;
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
		HashMap<String,ArrayList<Entity>> mapping = mapper.generateRelevantEntities(); //generate topics->entities mapping
		ArrayList<Entity> relevantEntities = mapping.get(query); // create data structure for mapping
		GalagoOrchestrator orchestrator =  new GalagoOrchestrator();
		ArrayList<Long> documents = SearchEvaluator.getDocIds(orchestrator.getDocuments(query, 10)); //get top 50 documents from galago search of query
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

		Set<String> idMatches = new HashSet<String>();
		System.out.println("Truth entities");
		System.out.println("");
		for(Entity te:truthEntities){
			System.out.println(te.getName());
		}
		for(Entity te:truthEntities){
			for(Entity re:returnedEntities){
				
				if(te.getName().substring(0, 3).toLowerCase().equals(re.getName().substring(0, 3).toLowerCase())){
					System.out.println("possible match: "+te.getName()+" "+re.getName());
				}
				if(te.getName().toLowerCase().equals(re.getName().toLowerCase())){
					idMatches.add((re.getName()));
					System.out.println("Entity: "+re.getName()+ " "+re.getTotalAppearances());
					for(Long docno:re.getHashMap().keySet()){
						System.out.println("Docno: "+docno+" appearances: "+re.getHashMap().get(docno));
					}
					
				}
			}
		}
		
		Iterator<String> i = idMatches.iterator();
		System.out.println("Matched entities");
		System.out.println("");
		while(i.hasNext()){
			String p = i.next();
			System.out.println(p);
		}
		System.out.println("Number of returned entities: "+returnedEntities.size());
		System.out.println("Number of relevant entities: "+truthEntities.size());
		System.out.println("Number of relevant returned entities: "+idMatches.size());
		double precision  = ((double) idMatches.size()/(double)returnedEntities.size());
		double recall  =  ((double)idMatches.size()/(double)truthEntities.size());
		double f1 = 2*((precision*recall)/(precision+recall));
		System.out.println("precision: "+precision+" recall: "+recall+" f1: "+f1);
	}
}
