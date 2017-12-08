package entityRetrieval.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dictionary.DictionaryHashMap;
import dictionary.generation.DbpediaReferenceParser;

public class SingleQuerySearch {
	String query;
	DictionaryHashMap dictionary;
	
	public SingleQuerySearch(String query) throws FileNotFoundException{
		DbpediaReferenceParser drp = new DbpediaReferenceParser();
		List<String> x = new ArrayList<String>(Arrays.asList("icd9", "icd10", "meshId", "icdo"));
		this.dictionary = drp.generateEntities(x);
		this.query = query;
	}
	public ArrayList<String> search() throws Exception{
		ArrayList<String> resultStrings = new ArrayList<String>();
		 DocumentIdentifier di = new DocumentIdentifier();
		 ArrayList<Long> documents = di.getRelevantDocuments(query); //finds the doc id's that contain the query word
		 TermCounter tc = new TermCounter(documents,dictionary);
		 ArrayList<Pair<Entity,Integer>> results = tc.matchEntities();
		 System.out.println("result size " + results.size());
		 for(Pair<Entity,Integer> result:results){
			 System.out.println(result.getL().toString()+result.getR());
			 resultStrings.add(result.getL().toString()+": "+result.getR());
		    	}
		 	 return resultStrings;
		    		
		    	}
	
	

}
