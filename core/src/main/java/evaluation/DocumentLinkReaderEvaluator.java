package evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;

import entityRetrieval.core.Entity;

public class DocumentLinkReaderEvaluator {
	private ArrayList<Entity> listOfEntities;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	
	public DocumentLinkReaderEvaluator(){
		DocumentLinkReader dlr = new DocumentLinkReader();
		this.listOfEntities=dlr.getEntitiesFromLinks();
		this.scoredDocs=dlr.getScoredDocuments();
		this.finalDocScores=RelevanceModel1.logstoposteriors(scoredDocs);
		this.mapping=dlr.getMapping();
		this.query=dlr.getQuery();
	}
	
	public void computeStatistics(){
		MedLinkEvaluator.setScores(listOfEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(listOfEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(listOfEntities);
		ArrayList<Entity> relevantEntities = mapping.get(query);
		MedLinkEvaluator.calculatePrecision(listOfEntities, relevantEntities);
		for(Entity entity:listOfEntities){
			System.out.println(entity.getName()+" Rank: "+entity.getRank()+" Score: "+entity.getScore()+ " Precision: "+entity.getPrecision());
		}
	}

}
