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
import metamap.MetaMapEntityLinker;

public class MetaMapEvaluator {
	private ArrayList<Entity> returnedEntities;
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;
	private HashMap<Long,Integer> entitiesPerDoc;
	private ArrayList<TopicRun> topicRuns;
	private String qrelFile;
	private String runFile;

	

	public MetaMapEvaluator(Boolean multiple){
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/KBResults.txt";		
		if(multiple){
			int runNum=1;
			MetaMapEntityLinker mmlinker = new MetaMapEntityLinker(0);
			addQuery(mmlinker);
			while(runNum<mmlinker.getMaxTopics()){
				MetaMapEntityLinker kbLinkerMul = new MetaMapEntityLinker(runNum); //compute map of all queries
				addQuery(kbLinkerMul);
				runNum++;
			}
		}
		else{
			MetaMapEntityLinker kbLinker = new MetaMapEntityLinker();//generate random query
			addQuery(kbLinker);
		}
	}
	public void addQuery(MetaMapEntityLinker kblinker){
		//ArrayList<Entity> relevantEntities = mapping.get(query);	
		ArrayList<Entity> returnedEntities = kblinker.generateEntities();
		List<ScoredDocument> scoredDocs = kblinker.getScoredDocuments();
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(returnedEntities);
		this.topicRuns = new ArrayList<TopicRun>();
		topicRuns.add(new TopicRun(kblinker.getQuery(),kblinker.topicChoice,returnedEntities));
	}
	public void evaluate(){
		KBLinkerEvaluator.createResultsFile("KBResults.txt", topicRuns);
		KBLinkerEvaluator.runEval(runFile,qrelFile);

	}
	
	
		
	

	
}
