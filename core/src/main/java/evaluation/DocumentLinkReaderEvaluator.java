package evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;

import entityRetrieval.core.Entity;
import entityRetrieval.core.TopicRun;
import knowledgeBase.KBFilter;
import knowledgeBase.KBLinker;

public class DocumentLinkReaderEvaluator {
	private ArrayList<TopicRun> topicRuns;
	private String qrelFile;
	private String runFile;
	
	public DocumentLinkReaderEvaluator(Boolean multiple){
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/DLRResults.txt";		
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
	public void addQuery(DocumentLinkReader dlr){
		//ArrayList<Entity> relevantEntities = mapping.get(query);	
		ArrayList<Entity> returnedEntities = dlr.getEntitiesFromLinks();
		List<ScoredDocument> scoredDocs = dlr.getScoredDocuments();
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(returnedEntities);
		this.topicRuns = new ArrayList<TopicRun>();
		topicRuns.add(new TopicRun(dlr.getQuery(),dlr.topicChoice,returnedEntities));
	}
	public void evaluate(){
		KBLinkerEvaluator.createResultsFile("KBResults.txt", topicRuns);
		KBLinkerEvaluator.runEval(runFile,qrelFile);

	}
	

}
