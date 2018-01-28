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
import org.lemurproject.galago.core.eval.SimpleEvalDoc;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import org.lemurproject.galago.utility.Parameters;

import entityRetrieval.core.Entity;
import knowledgeBase.KBFilter;
import knowledgeBase.KBLinker;

public class KBLinkerEvaluator {
	private ArrayList<Entity> listOfEntities;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;



	public KBLinkerEvaluator(){
		KBLinker kblinker = new KBLinker();
		this.listOfEntities=kblinker.getEntitiesFromText();
		this.scoredDocs=kblinker.getScoredDocuments();
		this.finalDocScores=RelevanceModel1.logstoposteriors(scoredDocs);
		this.mapping = kblinker.getMapping();
		this.query=kblinker.getQuery();
		
	}
	
	public void computeStatistics(){
		ArrayList<Entity> relevantEntities = mapping.get(query);
		System.out.println("Num unfiltered entities: "+listOfEntities.size());
		KBFilter kbfilter = new KBFilter(listOfEntities);
		listOfEntities=kbfilter.filterEntities();
		System.out.println("Num unfiltered entities: "+listOfEntities.size());
		MedLinkEvaluator.setScores(listOfEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(listOfEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(listOfEntities);
		String runFile="C:/Work/Project/samples/prototype4/level-4-project/core/results.txt";
		String qrelFile="C:/Work/Project/samples/treccar/benchmarkY1train/train.benchmarkY1train.cbor.article.entity.qrels";
		createResultsFile("results.txt", query, listOfEntities);
		runEval(runFile,qrelFile,query,listOfEntities);
		for(Entity e:relevantEntities){
			System.out.println(e.getName());
		}

	}
	
	public static void createResultsFile(String filename,String query, ArrayList<Entity> returnedEntities){
		File fout = new File(filename);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for(Entity re:returnedEntities){
			try {
				bw.write(query+"\\s+N\\s+"+re.getName()+"\\s+"+re.getScore());
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void runEval(String runFile, String qrelFile,String query, ArrayList<Entity> returnedEntities){
		Parameters p = Parameters.create();
		p.set("baseline", qrelFile);
		p.set("summary", true);
		List<SimpleEvalDoc> rankedList = new ArrayList<SimpleEvalDoc>();
		for(Entity re:returnedEntities){
			rankedList.add(new SimpleEvalDoc(re.getName(),re.getRank(),re.getScore()));
			
		}
		Map<String,List<SimpleEvalDoc>> results = new HashMap<String,List<SimpleEvalDoc>>();
		QuerySetJudgments querySetJudgments = null;
		try {
			querySetJudgments = new QuerySetJudgments(runFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		results.put(query, rankedList);
		List<String> limitQueryIdentifiers = new ArrayList<String>();
		limitQueryIdentifiers.add(query);
		Parameters finalparameters = null;
		try {
			finalparameters = Eval.singleEvaluation(p, querySetJudgments,limitQueryIdentifiers);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Eval eval = new Eval();
		try {
			eval.run(finalparameters, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
