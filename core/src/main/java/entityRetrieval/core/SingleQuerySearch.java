package entityRetrieval.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dictionary.DictionaryHashMap;
import generation.DbpediaDictionaryGenerator;

public class SingleQuerySearch {
	private String query;
	private DictionaryHashMap dictionary;
	
	public SingleQuerySearch(String query) throws FileNotFoundException{
		List<String> x = new ArrayList<String>(Arrays.asList("icd9", "icd10", "meshId", "icdo"));
		DbpediaDictionaryGenerator drp = new DbpediaDictionaryGenerator(x);
		this.dictionary = drp.generateEntities();
		this.query = query;
	}
	
	public ArrayList<Pair<Entity,Integer>> search() throws Exception{
		ArrayList<Pair<Entity,Integer>> entitytoMentions = new ArrayList<Pair<Entity,Integer>>();
		 DocumentIdentifier di = new DocumentIdentifier();
		 ArrayList<Long> documents = di.getRelevantDocuments(query); //finds the doc id's that contain the query word
		 TermCounter tc = new TermCounter(documents,dictionary);
		 ResultSet results = tc.matchEntities();
		 results = results.sort();
		 for(Pair<Entity,Integer> result:results.getResultSet()){
			 entitytoMentions.add(result);
		    		}
		 	 return entitytoMentions;
		    	}
	
	

}
