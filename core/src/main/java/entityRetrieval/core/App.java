package entityRetrieval.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dictionary.DbpediaDictionarySaver;
import dictionary.DictionaryHashMap;
import dictionary.SnomedDictionarySaver;
import evaluation.MetaMapEvaluator;
import evaluation.SearchEvaluator;
import generation.DbpediaDictionaryGenerator;
import generation.SnomedDictionaryGenerator;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Result;
import metamap.MetaMapEntityLinker;





public class App {
	
	
	
    public static void main( String[] args ) throws Exception{
    	HashMap<String, ArrayList<String>> appFunctions = App.initializeFunctions();
    	App.run(args, appFunctions);
    }
    
    public static void run(String[] args, HashMap<String, ArrayList<String>> appFunctions) throws Exception{
    	String fn = "help";
    	if(args.length>0 && appFunctions.containsKey(args[0])){
    		fn = args[0];
    	}
    	if(fn.equals("search")){
    		SingleQuerySearch sqs = new SingleQuerySearch(args[1]);
    		sqs.search();
    	}
    	else if(fn.equals("evaluate")){
    		SearchEvaluator se = new SearchEvaluator();
    		se.evaluate();
    	}
    	else if(fn.equals("test")){
    		MetaMapEvaluator evaulator = new MetaMapEvaluator();
    		evaulator.evauluate();
    	}
    	else if(fn.equals("initialize")){
    		DbpediaDictionaryGenerator ddg = new DbpediaDictionaryGenerator();
    		DictionaryHashMap dbpediaDictionary = ddg.generateEntities();
    		DbpediaDictionarySaver dds = new DbpediaDictionarySaver(dbpediaDictionary);
    		dds.save();
    		SnomedDictionaryGenerator sdg = new SnomedDictionaryGenerator();
    		DictionaryHashMap snomedDictionary = sdg.generateEntities();
    		SnomedDictionarySaver sds = new SnomedDictionarySaver(snomedDictionary);
    		sds.save();
    	}
    	
    }
    private static HashMap<String, ArrayList<String>> initializeFunctions(){
    	HashMap<String, ArrayList<String>> appFunctions = new HashMap<String, ArrayList<String>>();
    	appFunctions.put("test", new ArrayList<String>());
    	appFunctions.put("search", new ArrayList<String>());
    	appFunctions.put("evaluate", new ArrayList<String>());
    	appFunctions.put("initialize", new ArrayList<String>()); // initialize dictionarys
		return appFunctions;

    }
	    
}

