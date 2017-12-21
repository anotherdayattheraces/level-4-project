package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import dictionary.DictionaryHashMap;
import dictionary.DictionaryInitializer;
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
		System.out.println(topicChoice);
		Set<String> keySet = mapping.keySet();
		Iterator<String> i = keySet.iterator();
		int count = 0;
		while(count!=topicChoice){
			i.next();
			count++;
		}
		this.query=i.next().toString();
		System.out.println("Chosen query: "+query);
		DictionaryInitializer di = new DictionaryInitializer();
		this.dictionary=di.initialize();
	}
	
	public void evaluate(){
		DocumentIdentifier di = new DocumentIdentifier();
		try {
			ArrayList<Long> docids = di.getRelevantDocuments(query);
			System.out.println(docids.size());
			TermCounter tc = new TermCounter(docids,dictionary);
			ResultSet results = tc.matchEntities();
			TopicToEntityMapper mapper = new TopicToEntityMapper();
			HashMap<String,ArrayList<Entity>> mapping = mapper.generateRelevantEntities();
			ArrayList<Entity> relevantEntities = mapping.get(query);
			evaluator(relevantEntities,results);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	private void evaluator(ArrayList<Entity> truthEntities, ResultSet returnedEntities){
		System.out.println("Number of returned entities: "+returnedEntities.size);
		System.out.println("Number of relevant entities: "+truthEntities.size());
		int matches=0;
		for(Pair<Entity,Integer> returnedEntity:returnedEntities.getResultSet()){
			for(Entity truthEntity:truthEntities){
				if(truthEntity.getName().toLowerCase().equals(returnedEntity.getL().getName())){
					matches++;
				}
			}
		}
		System.out.println("Number of relevant returned entities: "+matches);


		
		
	}

}
