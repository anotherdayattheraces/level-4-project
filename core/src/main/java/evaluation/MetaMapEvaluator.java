package evaluation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import entityRetrieval.core.Entity;
import entityRetrieval.core.TopicRun;
import metamap.MetaMapEntityLinker;

public class MetaMapEvaluator {
	private PrintStream outputStream;
	private ArrayList<TopicRun> topicRuns;
	private String qrelFile;
	private String runFile;

	

	public MetaMapEvaluator(Boolean multiple){ //multiple=true if you want to carry out a set comparison, false for single eval
		try {
			this.outputStream = new PrintStream(new FileOutputStream("MMextraDetails.txt",true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/MMResults.txt";
		this.topicRuns = new ArrayList<TopicRun>();
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
	public void addQuery(MetaMapEntityLinker kblinker){ //add a completed topic run to be used for evaluation
		//ArrayList<Entity> relevantEntities = mapping.get(query);	
		ArrayList<Entity> returnedEntities = kblinker.generateEntities(outputStream);
		List<ScoredDocument> scoredDocs = kblinker.getScoredDocuments();
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(returnedEntities);
		topicRuns.add(new TopicRun(kblinker.getQuery(),kblinker.topicChoice,returnedEntities));
	}
	public void evaluate(){
		KBLinkerEvaluator.createResultsFile("MMResults.txt", topicRuns);
		KBLinkerEvaluator.runEval(runFile,qrelFile);

	}
	
	
		
	

	
}
