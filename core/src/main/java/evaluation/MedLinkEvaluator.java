package evaluation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import org.lemurproject.galago.core.retrieval.prf.WeightedTerm;
import org.lemurproject.galago.core.retrieval.query.AnnotatedNode;

import customEntityLinker.MedLink;
import dictionary.DictionaryHashMap;
import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import entityRetrieval.core.TopicRun;


public class MedLinkEvaluator {
	private ArrayList<TopicRun> topicRuns;
	private String qrelFile;
	private String runFile;
	private PrintStream outputStream;


	public MedLinkEvaluator(Boolean multiple){ //multiple=true if you want to carry out a set comparison, false for single eval
		this.qrelFile="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.runFile="C:/Work/Project/samples/prototype4/level-4-project/core/MLResults.txt";
		try {
			this.outputStream = new PrintStream(new FileOutputStream("MLextraDetails.txt",true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.topicRuns = new ArrayList<TopicRun>();
		if(multiple){
			int runNum=1;
			MedLink medLinker = new MedLink(0);
			addQuery(medLinker);
			DictionaryHashMap reusableDictionary = new DictionaryHashMap(medLinker.getDictionary().getDictionary());
			//reusableDictionary.getDictionary().putAll(medLinker.getMapping());
			while(runNum<medLinker.getMaxTopics()){
				MedLink medLinkerMul = new MedLink(runNum,reusableDictionary); //compute map of all queries
				addQuery(medLinkerMul);
				runNum++;
			}
		}
		else{
			MedLink medLinker = new MedLink();//generate random query
			addQuery(medLinker);
		}
	}
	public void addQuery(MedLink medLinker){ //add a completed topic run to be used for evaluation
		Boolean customScore = true;
		ArrayList<Entity> returnedEntities = medLinker.matchEntities(outputStream);
		List<ScoredDocument> scoredDocs = medLinker.getScoredDocuments();
		scoredDocs = calculateEntitiesPerDoc(returnedEntities,scoredDocs);
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		if(customScore){
			MedLinkEvaluator.setMentionProbablities(returnedEntities, scoredDocs); //calculate the mention probabilities for each entity per doc
			MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata

		}
		else{
			List<WeightedTerm> scoredTerms = null;
			try {
				scoredTerms = RelevanceModel1.scoreGrams(formatDataForApi(returnedEntities, scoredDocs),finalDocScores);
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

		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		
		MedLinkEvaluator.setAllRanks(returnedEntities);
		for(Entity entity:returnedEntities){
			System.out.println(entity.getName()+" "+entity.getScore());
		}
		topicRuns.add(new TopicRun(medLinker.getQuery(),medLinker.topicChoice,returnedEntities));
	}
	
	public void evaluate(){
		KBLinkerEvaluator.createResultsFile("MLResults.txt", topicRuns);
		KBLinkerEvaluator.runEval(runFile,qrelFile);
	}

	
	public static void sortByScore(ArrayList<Entity> unorderedList){ // method for sorting entities in a list by their score
	}
	public static Comparator<Entity> score = new Comparator<Entity>() {

		public int compare(Entity e1, Entity e2) {

		   double score1 = e1.getScore();
		   double score2 = e2.getScore();
		   return Double.compare(score2,score1);

	   }};
	public static void setScores(ArrayList<Entity> listOfEntities,  Map<ScoredDocument, Double> finalDocScores){ //static method for setting scores of all entities, details handled by entity class
		for(Entity re:listOfEntities){
			re.setScore(finalDocScores);
		}
	}
	public static void setMentionProbablities(ArrayList<Entity> listOfEntities, List<ScoredDocument> scoredDoc){ 
		for(Entity re:listOfEntities){
			for(ScoredDocument sd:re.getAppearances().keySet()){
					re.addMentionProbability(sd.document, re.getAppearances().get(sd.document), Integer.parseInt(sd.annotation.extraInfo)); //calculate the total number of entity mentions in all docs
					}
				}
	}
	public static Pair<Integer,String> generateRandomTopic(ArrayList<String> mapping){
		Random r = new Random();
		int topicChoice = r.nextInt(mapping.size());		
		return new Pair<Integer, String>(topicChoice,mapping.get(topicChoice));
	}
	public static List<ScoredDocument> calculateEntitiesPerDoc(ArrayList<Entity> listofEntities, List<ScoredDocument> scoredDocs){
		for(ScoredDocument scoredDoc:scoredDocs){
			scoredDoc.annotation=new AnnotatedNode();
			scoredDoc.annotation.extraInfo="0";
		}
		for(Entity returnedEntity:listofEntities){
			for(ScoredDocument entityScoredDoc:returnedEntity.getAppearances().keySet()){ //initialize hashmap of document id's to total entity mentions
				for(ScoredDocument scoredDoc:scoredDocs){
					if(entityScoredDoc==scoredDoc){
						if(scoredDoc.annotation.extraInfo.equals("0")){
							scoredDoc.annotation.extraInfo=Integer.toString(returnedEntity.getAppearances().get(entityScoredDoc));
						}
						else{
							int currentVal=Integer.parseInt(scoredDoc.annotation.extraInfo);
							scoredDoc.annotation.extraInfo=Integer.toString(currentVal+returnedEntity.getAppearances().get(entityScoredDoc));
						}
						
					}
				}
			}
		}
		return scoredDocs;
	}
	
	public static void setAllRanks(ArrayList<Entity> orderedList){
		for(int i=0;i<orderedList.size();i++){
			orderedList.get(i).setRank(i+1);
		}
	}
	public static ArrayList<Long> getDocIds(ArrayList<Pair<Long,Double>> docScores){
		ArrayList<Long> docids = new ArrayList<Long>();
		for(Pair<Long,Double> pair:docScores){
			docids.add(pair.getL());
		}
		return docids;
	}
	public static ArrayList<Entity> orderByFrequency(ArrayList<Entity> unorderedList){
		ArrayList<Entity> orderedByFreq = new ArrayList<Entity>();
		for(Entity returnedEntity:unorderedList){
			if(orderedByFreq.isEmpty()){
				orderedByFreq.add(returnedEntity);
			}
			else{
				Boolean added = false;
				for(int i=0;i<orderedByFreq.size();i++){
					if(orderedByFreq.get(i).getTotalAppearances()<returnedEntity.getTotalAppearances()){ //initialize array list of ranked list of entities by total mentions
						orderedByFreq.add(i, returnedEntity);
						added=true;
						break;
					}
				}
				if(!added){
					orderedByFreq.add(returnedEntity);
				}
			}
	}
		return orderedByFreq;

}
	public static Map<String, Map<ScoredDocument, Integer>> formatDataForApi(ArrayList<Entity> foundEntities, List<ScoredDocument> scoredDocuments){
		Map<String, Map<ScoredDocument, Integer>> counts = new HashMap<String, Map<ScoredDocument, Integer>>();
		for(Entity entity:foundEntities){
			counts.put(entity.getName(), new HashMap<ScoredDocument,Integer>());
			for(ScoredDocument scoredDoc:entity.getAppearances().keySet()){
				counts.get(entity.getName()).put(scoredDoc, entity.getAppearances().get(scoredDoc));
			}
		}
		return counts;
		
	}
	
	}
