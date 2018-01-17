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
import entityRetrieval.core.GalagoOrchestrator;
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
		int topicChoice = r.nextInt(mapping.keySet().size());
		//topicChoice = 19;
		Set<String> keySet = mapping.keySet();
		Iterator<String> i = keySet.iterator();
		int count = 0;
		while(count!=topicChoice){
			i.next();
			count++;
		}
		this.query=i.next().toString();
		System.out.println("Chosen query: "+query);
		this.dictionary=mapper.getDictionary();
	}
	
	public void evaluate(){
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		ArrayList<Long> docids = getDocIds(orchestrator.getDocuments(query, 50));
		System.out.println("Found "+docids.size()+" documents relevant to the query");
		TermCounter tc = new TermCounter(docids,dictionary);
		ArrayList<Entity> results = tc.matchEntities();			
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		HashMap<String,ArrayList<Entity>> mapping = mapper.generateRelevantEntities();
		ArrayList<Entity> relevantEntities = mapping.get(query);
		printStatistics(relevantEntities,results);

		}
		
		
	
	private void printStatistics(ArrayList<Entity> truthEntities, ArrayList<Entity> returnedEntities){
		ArrayList<String> nameMatches = new ArrayList<String>();
		ArrayList<Pair<Long,Integer>> entitiesPerDoc = new ArrayList<Pair<Long,Integer>>();
		System.out.println("truth entities: ");
		System.out.println("");

		for(Entity e:truthEntities){
			System.out.println(e.getName());
		}
		
		int matches=0;
		
		for(Entity returnedEntity:returnedEntities){
			for(Pair<Long,Integer> map:returnedEntity.appearancesToArray()){
				Boolean exists = false;
				for(Pair<Long,Integer> pair:entitiesPerDoc){
					if(map.getL()==pair.getL()){
						pair.setR(pair.getR()+map.getR());
					}
				}
				if(!exists){
					entitiesPerDoc.add(map);
				}
			}
			for(Pair<Long,Integer> pair:entitiesPerDoc){
				System.out.println(pair.getL()+" "+pair.getR());
			}
			for(Entity truthEntity:truthEntities){
				if(truthEntity.getName().substring(0, 3).toLowerCase().equals(returnedEntity.getName().substring(0, 3).toLowerCase())){
				//System.out.println("possible match: "+truthEntity.getName()+ " "+returnedEntity.getName());
			}
				if(truthEntity.getName().toLowerCase().equals(returnedEntity.getName().toLowerCase())&&!nameMatches.contains(truthEntity.getName())){
					System.out.println("Relevant & returned entity: "+truthEntity.getName());
					for(Long docid:returnedEntity.getHashMap().keySet()){
						System.out.println(docid+" : "+returnedEntity.getHashMap().get(docid));
					}
					nameMatches.add(truthEntity.getName());
					matches++;
				}
			}
		}
		System.out.println("Number of returned entities: "+returnedEntities.size());
		System.out.println("Number of relevant entities: "+truthEntities.size());
		System.out.println("Number of relevant returned entities: "+matches);
		//double precision  = ((double) nameMatches.size()/(double)returnedEntities.size());
		//double recall  =  ((double)nameMatches.size()/(double)truthEntities.size());
		//double f1 = 2*((precision*recall)/(precision+recall));
		//System.out.println("precision: "+precision+" recall: "+recall+" f1: "+f1);


		
		
	}
	public static ArrayList<Long> getDocIds(ArrayList<Pair<Long,Double>> docScores){
		ArrayList<Long> docids = new ArrayList<Long>();
		for(Pair<Long,Double> pair:docScores){
			docids.add(pair.getL());
		}
		return docids;
	}

}
