package entityRetrieval.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dictionary.DbpediaDictionarySaver;
import dictionary.DictionaryHashMap;
import dictionary.SnomedDictionarySaver;
import evaluation.DictionaryComparator;
import evaluation.DocumentLinkReader;
import evaluation.DocumentLinkReaderEvaluator;
import evaluation.KBLinkerEvaluator;
import evaluation.MetaMapEvaluator;
import evaluation.MedLinkEvaluator;
import evaluation.TopicToEntityMapper;
import generation.DbpediaDictionaryGenerator;
import generation.SnomedDictionaryGenerator;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Result;
import knowledgeBase.KBLinker;
import knowledgeBase.SnomedToWikiMapper;
import metamap.MetaMapEntityLinker;
import misc.CategoryGenerator;
import pseudoRelavanceFeedback.QueryEnhancer;





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
    		//DocumentIdentifier di = new DocumentIdentifier();
    		//ArrayList<Long> documents = di.getRelevantDocuments("Autism");
    		//QueryEnhancer enhancer = new QueryEnhancer(documents);
    		//enhancer.enhanceQuery();
    		//SingleQuerySearch sqs = new SingleQuerySearch(args[1]);
    		//sqs.search();
    	}
    	else if(fn.equals("evaluate")){
    		//DocumentLinkReaderEvaluator documentLinkReaderEvaluator = new DocumentLinkReaderEvaluator();
    		//documentLinkReaderEvaluator.computeStatistics();
    		//MetaMapEvaluator mmeval = new MetaMapEvaluator();
    		//mmeval.computeStatistics();
    		MedLinkEvaluator seval = new MedLinkEvaluator();
    		seval.computeStatistics();
    		//KBLinkerEvaluator kble = new KBLinkerEvaluator();
    		//kble.computeStatistics();
    	}
    	else if(fn.equals("test")){
    		SnomedToWikiMapper stwm = new SnomedToWikiMapper();
    		stwm.saveMappings(stwm.generateMappings());
    		CategoryGenerator cg = new CategoryGenerator();
    		HashMap<String,Integer> categories = cg.findCategories();
    		cg.saveCategories(categories);

    	}
    	else if(fn.equals("initialize")){
    		//DbpediaDictionaryGenerator ddg = new DbpediaDictionaryGenerator();
    		//DictionaryHashMap dbpediaDictionary = ddg.generateEntities();
    		//DbpediaDictionarySaver dds = new DbpediaDictionarySaver(dbpediaDictionary);
    		//dds.save();
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

