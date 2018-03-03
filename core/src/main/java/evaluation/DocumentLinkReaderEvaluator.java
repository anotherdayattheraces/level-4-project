package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import org.lemurproject.galago.core.retrieval.prf.WeightedTerm;

import entityRetrieval.core.Entity;
import entityRetrieval.core.TopicRun;

public class DocumentLinkReaderEvaluator {
	private ArrayList<TopicRun> topicRuns;
	private String qrelFile;
	private String runFile;
	
	public DocumentLinkReaderEvaluator(Boolean multiple){ //multiple=true if you want to carry out a set comparison, false for single eval
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/DLRResults.txt";
		this.topicRuns = new ArrayList<TopicRun>();
		if(multiple){
			int runNum=1;
			DocumentLinkReader dlr = new DocumentLinkReader(0);
			addQuery(dlr);
			while(runNum<dlr.getMaxTopics()){ 
				DocumentLinkReader kbLinkerMul = new DocumentLinkReader(runNum); //compute map of all queries
				addQuery(kbLinkerMul);
				runNum++;
			}
		}
		else{
			DocumentLinkReader dlr = new DocumentLinkReader();//generate random query
			addQuery(dlr);
		}
	}
	public void addQuery(DocumentLinkReader dlr){ //add a completed topic run to be used for evaluation
		Boolean customScore=true;
		ArrayList<Entity> returnedEntities = dlr.getEntitiesFromLinks();
		List<ScoredDocument> scoredDocs = dlr.getScoredDocuments();
		scoredDocs = MedLinkEvaluator.calculateEntitiesPerDoc(returnedEntities,scoredDocs);
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		if(customScore){
			MedLinkEvaluator.setMentionProbablities(returnedEntities, scoredDocs); //calculate the mention probabilities for each entity per doc
			MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata

		}
		else{
			List<WeightedTerm> scoredTerms = null;
			try {
				scoredTerms = RelevanceModel1.scoreGrams(MedLinkEvaluator.formatDataForApi(returnedEntities, scoredDocs),finalDocScores);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(WeightedTerm wt:scoredTerms){
				for(Entity entity:returnedEntities){
					if(entity.getName()==wt.getTerm()){
						entity.setScore(wt.score);
					}
				}
			}
			
		}
		
		MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(returnedEntities);
		topicRuns.add(new TopicRun(dlr.getQuery(),dlr.topicChoice,returnedEntities));
	}
	public void evaluate(){
		KBLinkerEvaluator.createResultsFile("DLRResults.txt", topicRuns);
		KBLinkerEvaluator.runEval(runFile,qrelFile,"DLR");

	}
	

}
