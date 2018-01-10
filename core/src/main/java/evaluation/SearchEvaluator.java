package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import dictionary.DictionaryHashMap;
import dictionary.DictionaryInitializer;
import dictionary.SnomedDictionaryInitializer;
import dictionary.DbpediaDictionaryInitializer;
import entityRetrieval.core.DocumentIdentifier;
import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import entityRetrieval.core.ResultSet;
import entityRetrieval.core.TermCounter;

public class SearchEvaluator {
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	private DictionaryHashMap dictionary;

	public SearchEvaluator() throws IOException{
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		this.mapping = mapper.generateRelevantEntities();
		Random r = new Random();
		int topicChoice = r.nextInt(mapping.keySet().size()-1);
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
		DictionaryInitializer di = new SnomedDictionaryInitializer();
		this.dictionary=di.initialize();
	}
	
	public void evaluate(){
		DocumentIdentifier di = new DocumentIdentifier();
		try {
			ArrayList<Long> docids = di.getRelevantDocuments(query);
			System.out.println("Found "+docids.size()+" documents relevant to the query");
			TermCounter tc = new TermCounter(docids,dictionary);
			ResultSet results = tc.matchEntities();
			TopicToEntityMapper mapper = new TopicToEntityMapper();
			HashMap<String,ArrayList<Entity>> mapping = mapper.generateRelevantEntities();
			ArrayList<Entity> relevantEntities = mapping.get(query);
			printStatistics(relevantEntities,results);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	private void printStatistics(ArrayList<Entity> truthEntities, ResultSet returnedEntities){
		ArrayList<String> nameMatches = new ArrayList<String>();
		for(Pair<Entity,Integer> pair:returnedEntities.resultSet){
			System.out.println(pair.getL().getName());
		}
		System.out.println("truth: ");
		System.out.println("");

		for(Entity e:truthEntities){
			System.out.println(e.getName());
		}
		System.out.println("Number of returned entities: "+returnedEntities.size);
		System.out.println("Number of relevant entities: "+truthEntities.size());
		int matches=0;
		
		for(Pair<Entity,Integer> returnedEntity:returnedEntities.getResultSet()){
			for(Entity truthEntity:truthEntities){
				if(truthEntity.getName().substring(0, 3).toLowerCase().equals(returnedEntity.getL().getName().substring(0, 3).toLowerCase())){
				System.out.println("possible match: "+truthEntity.getName()+ " "+returnedEntity.getL().getName());
			}
				if(truthEntity.getName().toLowerCase().equals(returnedEntity.getL().getName().toLowerCase())&&!nameMatches.contains(truthEntity.getName())){
					System.out.println("Relevant&returned entity: "+truthEntity.getName());
					nameMatches.add(truthEntity.getName());
					matches++;
				}
			}
		}
		System.out.println("Number of relevant returned entities: "+matches);


		
		
	}

}
