package evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;

import entityRetrieval.core.Entity;
import knowledgeBase.KBLinker;

public class KBLinkerEvaluator {
	private ArrayList<Entity> listOfEntities;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;

	public KBLinkerEvaluator(){
		KBLinker kblinker = new KBLinker();
		this.listOfEntities=kblinker.getEntitiesFromText();
		this.scoredDocs=kblinker.getScoredDocuments();
		this.finalDocScores=RelevanceModel1.logstoposteriors(scoredDocs);
	}
	
	public void computeStatistics(){
		MedLinkEvaluator.setScores(listOfEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(listOfEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(listOfEntities);
		for(Entity entity:listOfEntities){
			System.out.println(entity.getName()+" Rank: "+entity.getRank()+" Score: "+entity.getScore());
		}
	}


}
