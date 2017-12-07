package Entity_Retrieval_Engine.Entity_Linker;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;



/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception{
    	DbpediaReferenceParser drp = new DbpediaReferenceParser(); //generates the dictionary
    	List<String> x = new ArrayList<String>(Arrays.asList("icd9", "icd10"));
    	ArrayList<Entity> entities = drp.generateEntities(x);
    	String query = "grey";
	    DocumentIdentifier di = new DocumentIdentifier();
	    ArrayList<Long> documents = di.getRelevantDocuments(query); //finds the doc id's that contain the query word
	    TermCounter tc = new TermCounter(documents,entities);
	    ArrayList<Pair<Entity,Integer>> results = tc.matchEntities();
	    System.out.println("result size " + results.size());
	    for(Pair<Entity,Integer> result:results){
	    	System.out.println(result.getL().toString()+result.getR());
	    	}
	    		
	    	}
	    
}

