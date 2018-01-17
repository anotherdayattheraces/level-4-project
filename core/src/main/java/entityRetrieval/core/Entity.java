package entityRetrieval.core;

import java.util.ArrayList;
import java.util.HashMap;

public class Entity {
	private String name;
	private String id;
	private double score;
	private HashMap<Long,Integer> appearances;
	private double idf;
	private double tfidf;
	private ArrayList<Pair<Long,Double>> mentionProbability;
	
	public Entity(String name, String id, double score, long docID){
		this.name = name;
		this.id=id;
		this.score=score;
		this.appearances = new HashMap<Long,Integer>();
		this.appearances.put(docID, 1);
	}
	public Entity(String name, String id){
		this.name = name;
		this.id=id;
		this.appearances =  new HashMap<Long,Integer>();
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
	public void setScore(double score){
		this.score=score;
	}
	public void addMentionProbability(Long docno, double entityMentions, double totalMentions){
		double mp = entityMentions/totalMentions;
		this.mentionProbability.add(new Pair<Long, Double>(docno,mp));
	}
	public void incrementAppearance(long docID){
		int currentVal = this.appearances.get(docID);
		this.appearances.put(docID, currentVal+1);
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
		this.appearances.put(docID, 1);
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
	
}
