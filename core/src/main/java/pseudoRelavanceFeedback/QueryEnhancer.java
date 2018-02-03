package pseudoRelavanceFeedback;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import entityRetrieval.core.Entity;
import evaluation.DocumentLinkReader;

public class QueryEnhancer {
	private ArrayList<Long> documents;
	private PrintStream outputStream;

	
	public QueryEnhancer(ArrayList<Long> documents){
		this.documents=documents;
	}

	public static ArrayList<Entity> enhanceQuery(){
		DocumentLinkReader linker = new DocumentLinkReader();
		ArrayList<Entity> entities = linker.getEntitiesFromLinks();
		for(Entity e:entities){
			e.calculateIDF(50);
			e.calculateTFIDF();
			}
		Collections.sort(entities,QueryEnhancer.tfidf);
		for(Entity e:entities){
			System.out.println("Entity name: "+e.getName()+" entity ID: "+e.getId()+" tfidf: "+e.getTFIDF());
			}
		return null;
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
