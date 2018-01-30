package evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lemurproject.galago.core.eval.Eval;
import org.lemurproject.galago.core.eval.EvalDoc;
import org.lemurproject.galago.core.eval.QueryResults;
import org.lemurproject.galago.core.eval.QuerySetJudgments;
import org.lemurproject.galago.core.eval.QuerySetResults;
import org.lemurproject.galago.core.eval.SimpleEvalDoc;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import org.lemurproject.galago.core.retrieval.query.Query;
import org.lemurproject.galago.utility.Parameters;

import entityRetrieval.core.Entity;
import entityRetrieval.core.TopicRun;
import knowledgeBase.KBFilter;
import knowledgeBase.KBLinker;

public class KBLinkerEvaluator {
	private ArrayList<Entity> listOfEntities;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;
	private ArrayList<TopicRun> topicRuns;
	private String qrelFile;
	private String runFile;



	public KBLinkerEvaluator(Boolean multiple){
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/KBResults.txt";		
		if(multiple){
			int runNum=1;
			KBLinker kbLinker = new KBLinker(0);
			addQuery(kbLinker);
			while(runNum<kbLinker.getMaxTopics()){
				KBLinker kbLinkerMul = new KBLinker(runNum); //compute map of all queries
				addQuery(kbLinkerMul);
				runNum++;
			}
			
		}
		else{
			KBLinker kbLinker = new KBLinker();//generate random query
			addQuery(kbLinker);
		}
	}
	
	public void evaluate(){
		createResultsFile("KBResults.txt", topicRuns);
		runEval(runFile,qrelFile);

	}

	public void addQuery(KBLinker kblinker){
		//ArrayList<Entity> relevantEntities = mapping.get(query);	
		ArrayList<Entity> returnedEntities = kblinker.getEntitiesFromText();
		List<ScoredDocument> scoredDocs = kblinker.getScoredDocuments();
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(returnedEntities);
		this.topicRuns = new ArrayList<TopicRun>();
		topicRuns.add(new TopicRun(kblinker.getQuery(),kblinker.topicChoice,returnedEntities));
	}
	
	public static void createResultsFile(String filename, ArrayList<TopicRun> topicRuns){
		File fout = new File(filename);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for(TopicRun topicrun:topicRuns){
			for(Entity re:topicrun.getTopicEntities()){
				try {
					bw.write(topicrun.getTopicName().replaceAll(" ", "%20")+" Q"+topicrun.getTopicChoice()+" "+re.getName().replaceAll(" ", "%20")+" "+re.getRank()+" "+re.getScore()+" STANDARD");
					bw.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void runEval(String runFile, String qrelFile){
		Parameters p = Parameters.create();
		p.set("baseline", runFile); //set fileName of results file
		p.set("judgments", qrelFile);
		p.set("summary", true);

		//List<SimpleEvalDoc> rankedList = new ArrayList<SimpleEvalDoc>();
		//for(Entity re:returnedEntities){
		//	rankedList.add(new SimpleEvalDoc(re.getName(),re.getRank(),re.getScore()));
			
		//}
		//Map<String,List<SimpleEvalDoc>> results = new HashMap<String,List<SimpleEvalDoc>>();
		//QuerySetJudgments querySetJudgments = null;
		//try {
		//	querySetJudgments = new QuerySetJudgments(qrelFile); 
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		//results.put(query, rankedList);
		//List<String> limitQueryIdentifiers = new ArrayList<String>();
		//limitQueryIdentifiers.add(query);
		//Parameters finalparameters = null;
		//try {
		//	finalparameters = Eval.singleEvaluation(p, querySetJudgments,limitQueryIdentifiers);
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		Eval eval = new Eval();
		try {
			eval.run(p, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	
}
