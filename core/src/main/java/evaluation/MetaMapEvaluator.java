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
import metamap.MetaMapEntityLinker;

public class MetaMapEvaluator {
	private ArrayList<Entity> returnedEntities;
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;
	private HashMap<Long,Integer> entitiesPerDoc;


	
	public MetaMapEvaluator(){
		MetaMapEntityLinker linker = new MetaMapEntityLinker();
		this.returnedEntities=linker.generateEntities();
		this.mapping = linker.getMapping();
		this.query=linker.getQuery();
		System.out.println("Chosen query: "+query);
		this.scoredDocs=linker.getScoredDocuments();
		this.finalDocScores=RelevanceModel1.logstoposteriors(scoredDocs);
	} 
	
	public void computeStatistics(){
		ArrayList<Entity> relevantEntities = mapping.get(query); // create data structure for mapping
		System.out.println("Num unfiltered entities: "+returnedEntities.size());
		KBFilter kbfilter = new KBFilter(returnedEntities);
		returnedEntities=kbfilter.filterEntities();
		System.out.println("Num filtered entities: "+returnedEntities.size());
		this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(returnedEntities);
		MedLinkEvaluator.setMentionProbablities(returnedEntities, entitiesPerDoc); //calculate the mention probabilities for each entity per doc
		MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(returnedEntities);
		MedLinkEvaluator.calculatePrecision(returnedEntities, relevantEntities);
		for(Entity e:relevantEntities){
			System.out.println(e.getName());
		}
		double averagePrecision=0;
		for(Entity entity:returnedEntities){
			averagePrecision+=entity.getPrecision();
			System.out.println(entity.getName()+" Rank: "+entity.getRank()+" Score: "+entity.getScore()+ " Precision: "+entity.getPrecision());
		}
		averagePrecision=averagePrecision/(double)returnedEntities.size();
		System.out.println(averagePrecision);
	}
	
		
	

	
}
