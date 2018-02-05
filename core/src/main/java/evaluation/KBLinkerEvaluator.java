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
import java.util.List;
import java.util.Map;
import org.lemurproject.galago.core.eval.Eval;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import org.lemurproject.galago.core.retrieval.prf.WeightedTerm;
import org.lemurproject.galago.utility.Parameters;
import entityRetrieval.core.Entity;
import entityRetrieval.core.TopicRun;
import knowledgeBase.KBLinker;

public class KBLinkerEvaluator {
	private ArrayList<TopicRun> topicRuns;
	private String qrelFile;
	private String runFile;

	public KBLinkerEvaluator(Boolean multiple){ //multiple=true if you want to carry out a set comparison, false for single eval
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/KBResults.txt";
		this.topicRuns = new ArrayList<TopicRun>();
		if(multiple){
			int runNum=1;
			KBLinker kbLinker = new KBLinker(0); //need to initialize first linker to access non static method getMaxTopics()
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

	public void addQuery(KBLinker kblinker){ //add a completed topic run to be used for evaluation
		//ArrayList<Entity> relevantEntities = mapping.get(query);	
		ArrayList<Entity> returnedEntities = kblinker.getEntitiesFromText();
		List<ScoredDocument> scoredDocs = kblinker.getScoredDocuments();
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		scoredDocs = MedLinkEvaluator.calculateEntitiesPerDoc(returnedEntities,scoredDocs);
		Boolean customScore=false;
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
		topicRuns.add(new TopicRun(kblinker.getQuery().trim(),kblinker.topicChoice,returnedEntities));
	}
	
	public static void createResultsFile(String filename, ArrayList<TopicRun> topicRuns){ //static method to be used by other eval classes that creates the run file
		File fout = new File(filename);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for(TopicRun topicrun:topicRuns){
			String topicName=topicrun.getTopicName().replaceAll(" ", "%20");
			for(Entity re:topicrun.getTopicEntities()){
				System.out.println(re.getName());
				try {
					
					bw.write(topicName+" Q"+topicrun.getTopicChoice()+" "+re.getName().replaceAll(" ", "%20")+" "+re.getRank()+" "+re.getScore()+" STANDARD");
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
	public static void runEval(String runFile, String qrelFile){ //static method to be used by other eval classes takes both run/qrel files and uses galago api to compute statistics
		Parameters p = Parameters.create();
		p.set("baseline", runFile); //set fileName of results file
		p.set("judgments", qrelFile);
		p.set("summary", true);
		p.set("details", true);

		Eval eval = new Eval();
		PrintStream printstream =null;
		try {
			printstream = new PrintStream(new FileOutputStream("fullDetails.txt",true));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			eval.run(p, printstream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		printstream.close();
	}


	
	
}
