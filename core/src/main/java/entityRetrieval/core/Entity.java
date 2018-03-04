package entityRetrieval.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lemurproject.galago.core.retrieval.ScoredDocument;

public class Entity {
	private String name;
	private String id;
	private double score;
	private HashMap<ScoredDocument,Integer> appearances;
	private HashMap<Long,Double> mentionProbability;
	private int rank;
	private long document;
	private String summary;
	
	public Entity(String name, String id, double score, ScoredDocument scoredDoc){
		this.name = name;
		this.id=id;
		this.score=score;
		this.appearances = new HashMap<ScoredDocument,Integer>();
		this.appearances.put(scoredDoc, 1);
		this.mentionProbability = new HashMap<Long,Double>();
	}
	public Entity(String name, String id){
		this.name = name;
		this.id=id;
		this.appearances =  new HashMap<ScoredDocument,Integer>();
		this.mentionProbability = new HashMap<Long,Double>();
	}
	public Entity(String name, String id, long document){
		this.name = name;
		this.id=id;
		this.appearances =  new HashMap<ScoredDocument,Integer>();
		this.mentionProbability = new HashMap<Long,Double>();
		this.document=document;
	}
	public Entity(String name){
		this.name = name;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
	public void setSummary(String summary){
		 this.summary=summary;
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
		for(ScoredDocument key:this.appearances.keySet()){
			array.add(new Pair<Long, Integer>(key.document,appearances.get(key)));
		}
		return array;
	}
	public HashMap<ScoredDocument, Integer> getHashMap(){
		return this.appearances;
	}
	public void addAppearance(ScoredDocument scoredDoc){
		if(this.appearances.containsKey(scoredDoc)){
			int currentVal = this.appearances.get(scoredDoc);
			this.appearances.put(scoredDoc, currentVal+1);
		}
		else{
			this.appearances.put(scoredDoc, 1);
		}
	}
	public int getTotalAppearances(){
		int appearances = 0;
		for(Pair<Long,Integer> pair:this.appearancesToArray()){
			appearances += pair.getR();
		}
		return appearances;
	}
	
	
	public HashMap<ScoredDocument,Integer> getAppearances(){
		return this.appearances;
	}
	public void setScore(double score){
		this.score=score;
	}
	public void mergeEntityApps(Entity otherEntity){
		for(ScoredDocument sd:otherEntity.getAppearances().keySet()){
			if(!this.appearances.containsKey(sd)){
				this.appearances.put(sd, otherEntity.getAppearances().get(sd));
			}
			else{
				this.appearances.put(sd, this.appearances.get(sd)+otherEntity.getAppearances().get(sd));
			}
		}
	}
	public void setDocument(long document){
		this.document=document;
	}
	public long getDocument(){
		return this.document;
	}
	public int getRank() {
		return this.rank;
	}
	
				
}
