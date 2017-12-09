package entityRetrieval.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dictionary.DictionaryHashMap;
import dictionary.DictionaryInitializer;
import dictionary.DictionarySaver;
import generation.DictionaryBuilder;


public class App 
{
    public static void main( String[] args ) throws Exception{
    	String query = "autism";

    	//System.out.println(results.size());
    	//List<String> x = new ArrayList<String>(Arrays.asList("icd9", "icd10", "meshId", "icdo"));
    	//DictionaryBuilder db = new DictionaryBuilder();
    	//DictionaryHashMap dhm = db.generateEntities(x);
    	//DictionarySaver ds = new DictionarySaver(dhm);
    	//ds.save();
    	
    	DictionaryInitializer di = new DictionaryInitializer();
    	DictionaryHashMap dictionary = di.initialize();
    	SingleQuerySearch sqs = new SingleQuerySearch(query);
    	ArrayList<String> results = sqs.search();
    	System.out.println("These are the entities found to be the most relevant to your query: "+query);
    	for(String result:results){
        	System.out.println(result);

    	}

	   
    }
	    
}

