package evaluation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import org.lemurproject.galago.core.retrieval.prf.WeightedTerm;

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
			this.outputStream = new PrintStream(new FileOutputStream("MM50extraDetails.txt",true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/MM50Results.txt";
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
		ArrayList<Entity> returnedEntities = kblinker.generateEntities(outputStream);
		List<ScoredDocument> scoredDocs = kblinker.getScoredDocuments();
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		scoredDocs = MedLinkEvaluator.calculateEntitiesPerDoc(returnedEntities,scoredDocs);
		Boolean customScore = true;
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
		topicRuns.add(new TopicRun(kblinker.getQuery(),kblinker.topicChoice,returnedEntities));
	}
	public void evaluate(){
		KBLinkerEvaluator.createResultsFile("MM50Results.txt", topicRuns);
		KBLinkerEvaluator.runEval(runFile,qrelFile);

	}
	
	
		
	

	
}
