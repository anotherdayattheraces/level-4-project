package pseudoRelavanceFeedback;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lemurproject.galago.core.retrieval.prf.WeightedTerm;

import entityRetrieval.core.Entity;
import evaluation.DocumentLinkReader;

public class QueryEnhancer {
	private ArrayList<Entity> terms;
	private PrintStream outputStream;
	private int numTerms;

	
	public QueryEnhancer(ArrayList<Entity> terms, PrintStream outputStream){
		this.terms=terms;
		this.outputStream=outputStream;
	}
	

	public static ArrayList<String> enhanceQuery(){
		return null;
	}
	public List<WeightedTerm> convertToGalago(){
		List<WeightedTerm> wTerms = new ArrayList<WeightedTerm>();
		return wTerms;
		
	}
	
	public static void sortBytfidf(ArrayList<Entity> unorderedList){ // method for sorting entities in a list by their score
	}
	public static Comparator<Entity> tfidf = new Comparator<Entity>() {

		public int compare(Entity e1, Entity e2) {

			double score1 = e1.getTFIDF();
			double score2 = e2.getTFIDF();
		   return Double.compare(score2,score1);

	   }};

	
}
