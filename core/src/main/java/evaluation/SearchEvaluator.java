package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;

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
	private List<ScoredDocument> docScores;
	private HashMap<Long,Integer> entitiesPerDoc;
	private Map<ScoredDocument, Double> finalDocScores;
	
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
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.docScores = orchestrator.getDocuments(query, 50);
		this.finalDocScores = RelevanceModel1.logstoposteriors(docScores);;
	}
	
	public void evaluate(){
		ArrayList<Long> docids = new ArrayList<Long>();
		for(ScoredDocument sd:docScores){
			docids.add(sd.document);
		}
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
		ArrayList<Entity> orderedByFreq = orderByFrequency(returnedEntities); //order entities by frequency 
		this.entitiesPerDoc = calculateEntitiesPerDoc(returnedEntities); //fill the Long,Int hashmap for each entity for it's appearances per doc
		setMentionProbablities(returnedEntities);
		setAllRanks(orderedByFreq); // set the rank for all entities
		setScores(returnedEntities); //compute final scores for all entities
		Collections.sort(returnedEntities, score);
		System.out.println("truth entities: ");

		
		int matches=0;
		for(Entity returnedEntity:returnedEntities){
			System.out.println(returnedEntity.getName()+" "+returnedEntity.getScore());
			for(Entity truthEntity:truthEntities){
				if(truthEntity.getName().substring(0, 3).toLowerCase().equals(returnedEntity.getName().substring(0, 3).toLowerCase())){
				//System.out.println("possible match: "+truthEntity.getName()+ " "+returnedEntity.getName());
			}
				if(truthEntity.getName().toLowerCase().equals(returnedEntity.getName().toLowerCase())&&!nameMatches.contains(truthEntity.getName())){
					//System.out.println("Relevant & returned entity: "+truthEntity.getName());
					for(Long docid:returnedEntity.getHashMap().keySet()){
						//System.out.println(docid+" : "+returnedEntity.getHashMap().get(docid));
					}
					nameMatches.add(truthEntity.getName());
					matches++;
				}
			}
		}
		System.out.println("Number of returned entities: "+returnedEntities.size());
		System.out.println("Number of relevant entities: "+truthEntities.size());
		System.out.println("Number of relevant returned entities: "+matches);
		
	}
	public void sortByScore(ArrayList<Entity> unorderedList){
		
	}
	public static Comparator<Entity> score = new Comparator<Entity>() {

		public int compare(Entity e1, Entity e2) {

		   double score1 = e1.getScore();
		   double score2 = e2.getScore();

		   /*For ascending order*/
		   return Double.compare(score1,score2);

	   }};
	public void setScores(ArrayList<Entity> listOfEntities){
		for(Entity re:listOfEntities){
			re.setScore(finalDocScores);
		}
	}
	public void setMentionProbablities(ArrayList<Entity> listOfEntities){ 
		for(Entity re:listOfEntities){
			for(Pair<Long,Integer> map:re.appearancesToArray()){
					re.addMentionProbability(map.getL(), map.getR(), entitiesPerDoc.get(map.getL())); //calculate the total number of entity mentions in all docs
					}
				}
	}
	public static HashMap<Long,Integer> calculateEntitiesPerDoc(ArrayList<Entity> listofEntities){
		HashMap<Long,Integer> entitiesPerDoc = new HashMap<Long,Integer>();
		for(Entity returnedEntity:listofEntities){
			for(Pair<Long,Integer> map:returnedEntity.appearancesToArray()){ //initialize hashmap of document id's to total entity mentions
				Boolean exists = false;
				for(long docid:entitiesPerDoc.keySet()){
					if(map.getL()==docid){
						exists = true;
						int currentVal = entitiesPerDoc.get(docid);
						entitiesPerDoc.put(docid, currentVal+map.getR());
					}
				}
				if(!exists){
					entitiesPerDoc.put(map.getL(), map.getR());
				}
			}
		}
		return entitiesPerDoc;
	}
	public void setAllRanks(ArrayList<Entity> orderedList){
		for(int i=0;i<orderedList.size();i++){
			orderedList.get(i).setRank(i+1);
			//System.out.println(orderedByFreq.get(i).getName()+" "+orderedByFreq.get(i).getTotalAppearances());
		}
	}
	public static ArrayList<Long> getDocIds(ArrayList<Pair<Long,Double>> docScores){
		ArrayList<Long> docids = new ArrayList<Long>();
		for(Pair<Long,Double> pair:docScores){
			docids.add(pair.getL());
		}
		return docids;
	}
	public static ArrayList<Entity> orderByFrequency(ArrayList<Entity> unorderedList){
		ArrayList<Entity> orderedByFreq = new ArrayList<Entity>();
		for(Entity returnedEntity:unorderedList){
			if(orderedByFreq.isEmpty()){
				orderedByFreq.add(returnedEntity);
			}
			else{
				Boolean added = false;
				for(int i=0;i<orderedByFreq.size();i++){
					if(orderedByFreq.get(i).getTotalAppearances()<returnedEntity.getTotalAppearances()){ //initialize array list of ranked list of entities by total mentions
						orderedByFreq.add(i, returnedEntity);
						added=true;
						break;
					}
				}
				if(!added){
					orderedByFreq.add(returnedEntity);
				}
			}
	}
		return orderedByFreq;

}
	}
