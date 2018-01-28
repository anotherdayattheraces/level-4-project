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
import customEntityLinker.MedLink;
import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import knowledgeBase.KBFilter;


public class MedLinkEvaluator {
	private HashMap<String,ArrayList<Entity>> mapping;
	private ArrayList<Entity> returnedEntities;
	private String query;
	private List<ScoredDocument> scoredDocs;
	private HashMap<Long,Integer> entitiesPerDoc;
	private Map<ScoredDocument, Double> finalDocScores;
	
	public MedLinkEvaluator() throws IOException{
		MedLink medlink = new MedLink();
		this.returnedEntities = medlink.matchEntities();
		this.scoredDocs = medlink.getScoredDocs();
		this.finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		this.mapping=medlink.getMapping();
		this.query=medlink.getQuery();
		
	}
	
	public void computeStatistics(){
		ArrayList<Entity> relevantEntities = mapping.get(query);
		System.out.println("Num unfiltered entities: "+returnedEntities.size());
		KBFilter kbfilter = new KBFilter(returnedEntities);
		returnedEntities=kbfilter.filterEntities();
		System.out.println("Num filtered entities: "+returnedEntities.size());
		this.entitiesPerDoc = calculateEntitiesPerDoc(returnedEntities); //fill the Long,Int hashmap for each entity for it's appearances per doc
		setMentionProbablities(returnedEntities,entitiesPerDoc);
		setScores(returnedEntities,finalDocScores); //compute final scores for all entities
		Collections.sort(returnedEntities, score);
		MedLinkEvaluator.setAllRanks(returnedEntities);
		for(Entity e:relevantEntities){
			System.out.println(e.getName());
		}
		System.out.println("Returned: ");
		for(Entity entity:returnedEntities){
			System.out.println(entity.getName()+" Rank: "+entity.getRank()+" Score: "+entity.getScore());
		}
		
		}
		

	public static void sortByScore(ArrayList<Entity> unorderedList){
	}
	public static Comparator<Entity> score = new Comparator<Entity>() {

		public int compare(Entity e1, Entity e2) {

		   double score1 = e1.getScore();
		   double score2 = e2.getScore();
		   return Double.compare(score2,score1);

	   }};
	public static void setScores(ArrayList<Entity> listOfEntities,  Map<ScoredDocument, Double> finalDocScores){
		for(Entity re:listOfEntities){
			re.setScore(finalDocScores);
		}
	}
	public static void setMentionProbablities(ArrayList<Entity> listOfEntities, HashMap<Long,Integer> entitiesPerDoc){ 
		for(Entity re:listOfEntities){
			for(Pair<Long,Integer> map:re.appearancesToArray()){
					re.addMentionProbability(map.getL(), map.getR(), entitiesPerDoc.get(map.getL())); //calculate the total number of entity mentions in all docs
					}
				}
	}
	public static String generateRandomTopic(ArrayList<String> mapping){
		Random r = new Random();
		int topicChoice = r.nextInt(mapping.size());		
		return mapping.get(topicChoice);
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
	public static void setAllRanks(ArrayList<Entity> orderedList){
		for(int i=0;i<orderedList.size();i++){
			orderedList.get(i).setRank(i+1);
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
	public static void calculatePrecision(ArrayList<Entity> orderedListOfEntities, ArrayList<Entity> relevantEntities){
		double currentSum=0;
		for(Entity returnedEntity:orderedListOfEntities){
			int related=0;
			for(Entity relevantEntity:relevantEntities){
				if(relevantEntity.getName().equals(returnedEntity.getName())){
					related=1;
				}
				if(EntityMatcher.removeBracketDescription(relevantEntity.getName()).equals(EntityMatcher.removeBracketDescription(returnedEntity.getName()))){
					related=1;
				}
				if(returnedEntity.getName().split(" ").length>=3){
					if(EntityMatcher.lastTwoWords(returnedEntity.getName()).equals(relevantEntity.getName())){
						related=1;
						System.out.println("Dodgy match of: "+returnedEntity.getName()+" to: "+relevantEntity.getName());
					}
				}
			}
			currentSum=returnedEntity.setPrecision(currentSum, returnedEntity.getRank(),related);
		}
	}
	}
