package entityRetrieval.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lemurproject.galago.core.retrieval.ScoredDocument;

public class Entity {
	private String name;
	private String id;
	private double score;
	private HashMap<Long,Integer> appearances;
	private double idf;
	private double tfidf;
	private HashMap<Long,Double> mentionProbability;
	private int rank;
	private double precision;
	
	public Entity(String name, String id, double score, long docID){
		this.name = name;
		this.id=id;
		this.score=score;
		this.appearances = new HashMap<Long,Integer>();
		this.appearances.put(docID, 1);
		this.mentionProbability = new HashMap<Long,Double>();
	}
	public Entity(String name, String id){
		this.name = name;
		this.id=id;
		this.appearances =  new HashMap<Long,Integer>();
		this.mentionProbability = new HashMap<Long,Double>();
	}
	public Entity(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public String getId(){
		return this.id;
	}
	public double getScore(){
		return this.score;
	}
	public void setScore(Map<ScoredDocument,Double> docScores){
		double currentScore=0;
		for(ScoredDocument scoredDoc:docScores.keySet()){
			if(this.mentionProbability.containsKey(scoredDoc.document)){
				currentScore+=this.mentionProbability.get(scoredDoc.document)*(docScores.get(scoredDoc));
			}
		}
		this.score=currentScore;
	}
	public void setRank(int rank){
		this.rank=rank;
	}
	public HashMap<Long,Double> getMentionProbability(){
		return this.mentionProbability;
	}
	public void addMentionProbability(Long docno, double entityMentions, double totalMentions){
		double mp = entityMentions/totalMentions;
		this.mentionProbability.put(docno,mp);
	}

	public ArrayList<Pair<Long,Integer>> appearancesToArray(){
		ArrayList<Pair<Long,Integer>> array = new ArrayList<Pair<Long,Integer>>();
		for(long key:this.appearances.keySet()){
			array.add(new Pair<Long, Integer>(key,appearances.get(key)));
		}
		return array;
	}
	public HashMap<Long, Integer> getHashMap(){
		return this.appearances;
	}
	public void addAppearance(Long docID){
		if(this.appearances.containsKey(docID)){
			int currentVal = this.appearances.get(docID);
			this.appearances.put(docID, currentVal+1);
		}
		else{
			this.appearances.put(docID, 1);
		}
	}
	public int getTotalAppearances(){
		int appearances = 0;
		for(Pair<Long,Integer> pair:this.appearancesToArray()){
			appearances += pair.getR();
		}
		return appearances;
	}
	public double getTFIDF(){
		return this.tfidf;
	}
	public void calculateIDF(double numDocs){
		double docAppearances = this.appearances.size();
		this.idf=Math.log10(numDocs/docAppearances);
	}
	public void calculateTFIDF(){
		double tfidf = 0;
		for(Pair<Long,Integer> pair:this.appearancesToArray()){
			tfidf+=(this.idf*pair.getR());
		}
		this.tfidf = tfidf;
	}
	public int getRank(){
		return this.rank;
	}
	public double setPrecision(double currentSum,int rank,int related){
		 this.precision = (currentSum*related)/rank;
		 return this.precision;
		
	}
	public double getPrecision(){
		return this.precision;
	}
	
}
