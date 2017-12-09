package entityRetrieval.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dictionary.DictionaryHashMap;
import generation.DictionaryBuilder;

public class SingleQuerySearch {
	private String query;
	private DictionaryHashMap dictionary;
	
	public SingleQuerySearch(String query) throws FileNotFoundException{
		DictionaryBuilder drp = new DictionaryBuilder();
		List<String> x = new ArrayList<String>(Arrays.asList("icd9", "icd10", "meshId", "icdo"));
		this.dictionary = drp.generateEntities(x);
		this.query = query;
	}
	
	public ArrayList<String> search() throws Exception{
		ArrayList<String> resultStrings = new ArrayList<String>();
		 DocumentIdentifier di = new DocumentIdentifier();
		 ArrayList<Long> documents = di.getRelevantDocuments(query); //finds the doc id's that contain the query word
		 TermCounter tc = new TermCounter(documents,dictionary);
		 ResultSet results = tc.matchEntities();
		 results = results.sort();
		 System.out.println("result size " + results.getResultSet().size());
		 for(Pair<Entity,Integer> result:results.getResultSet()){
			 resultStrings.add(result.getL().getName()+": "+result.getR());
		    		}
		 	 return resultStrings;
		    	}
	
	

}
