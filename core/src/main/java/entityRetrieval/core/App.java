package entityRetrieval.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dictionary.generation.DbpediaReferenceParser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception{
    	String query = "grey";
    	DbpediaReferenceParser drp = new DbpediaReferenceParser();
    	List<String> x = new ArrayList<String>(Arrays.asList("icd9", "icd10", "meshId"));
    	drp.generateEntities(x);
	   
    }
	    
}

