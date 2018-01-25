package evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;

import entityRetrieval.core.Entity;
import knowledgeBase.KBFilter;
import knowledgeBase.KBLinker;

public class KBLinkerEvaluator {
	private ArrayList<Entity> listOfEntities;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;


	public KBLinkerEvaluator(){
		KBLinker kblinker = new KBLinker();
		this.listOfEntities=kblinker.getEntitiesFromText();
		this.scoredDocs=kblinker.getScoredDocuments();
		this.finalDocScores=RelevanceModel1.logstoposteriors(scoredDocs);
		this.mapping = kblinker.getMapping();
		this.query=kblinker.getQuery();
	}
	
	public void computeStatistics(){
		ArrayList<Entity> relevantEntities = mapping.get(query);
		System.out.println("Num unfiltered entities: "+listOfEntities.size());
		KBFilter kbfilter = new KBFilter(listOfEntities);
		listOfEntities=kbfilter.filterEntities();
		System.out.println("Num unfiltered entities: "+listOfEntities.size());
		MedLinkEvaluator.setScores(listOfEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(listOfEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(listOfEntities);
		MedLinkEvaluator.calculatePrecision(listOfEntities, relevantEntities);
		for(Entity e:relevantEntities){
			System.out.println(e.getName());
		}
		double averagePrecision=0;
		for(Entity entity:listOfEntities){
			averagePrecision+=entity.getPrecision();
			for(Entity re:relevantEntities){
				if(entity.getName().substring(0, 3).equals(re.getName().substring(0, 3))){
					System.out.println("Possible match: "+entity.getName()+" "+re.getName());
				}
			}
			System.out.println(entity.getName()+" Rank: "+entity.getRank()+" Score: "+entity.getScore()+ " Precision: "+entity.getPrecision());
		}
		
		averagePrecision=averagePrecision/(double)listOfEntities.size();
		System.out.println(averagePrecision);
	}
	
	
}
