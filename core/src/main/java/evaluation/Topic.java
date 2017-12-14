package evaluation;

import java.util.ArrayList;

public class Topic {
	private String disease;
	private ArrayList<String> genes;
	private String demographic;
	private ArrayList<String> other;
	private ArrayList<String> pmids;

	public Topic(String disease, ArrayList<String> genes, String demographic, ArrayList<String> other, ArrayList<String> pmids){
		this.disease=disease;
		this.genes=genes;
		this.demographic=demographic;
		this.other=other;
		this.pmids=pmids;
		}
	
	public Topic(String disease, ArrayList<String> genes, String demographic, ArrayList<String> pmids){
		this.disease=disease;
		this.genes=genes;
		this.demographic=demographic;
		this.pmids=pmids;
		}
	
	public String getDisease(){
		return this.disease;
}
	public ArrayList<String> getPMIDS(){
		return this.pmids;
	}

}