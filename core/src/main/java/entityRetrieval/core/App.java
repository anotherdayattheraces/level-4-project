package entityRetrieval.core;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lemurproject.galago.core.tools.Search;
import org.lemurproject.galago.core.tools.SearchWebHandler;

import customEntityLinker.MedLink;
import dictionary.DbpediaDictionarySaver;
import dictionary.DictionaryHashMap;
import dictionary.SnomedDictionaryEnhancer;
import dictionary.SnomedDictionaryInitializer;
import dictionary.SnomedDictionarySaver;
import evaluation.DocumentLinkReader;
import evaluation.DocumentLinkReaderEvaluator;
import evaluation.KBLinkerEvaluator;
import evaluation.LinkerTester;
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
import misc.Del;
import misc.DocumentExtractor;
import misc.QrelFilter;
import misc.ResultsAnalyzer;
import webInterface.MedStreamContextHandler;
import webInterface.MedWebServer;





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
    		SearchFn searchfn = new SearchFn();
    		searchfn.run(System.out);
    		
    	}
    	else if(fn.equals("evaluate")){
    		//DocumentLinkReaderEvaluator documentLinkReaderEvaluator = new DocumentLinkReaderEvaluator(true);
    		//documentLinkReaderEvaluator.evaluate();
    		//MetaMapEvaluator mmeval = new MetaMapEvaluator(true);
    		//mmeval.evaluate();
    		MedLinkEvaluator seval = new MedLinkEvaluator(true);
    		seval.evaluate();
    		KBLinkerEvaluator kble = new KBLinkerEvaluator(true);
    		kble.evaluate();
    	}
    	else if(fn.equals("test")){
    		//SnomedToWikiMapper stwm = new SnomedToWikiMapper();
    		//stwm.saveMappings(stwm.generateMappings());
    		//CategoryGenerator cg = new CategoryGenerator();
    		//HashMap<String,Integer> categories = cg.findCategories();
    		//cg.saveCategories(categories);
    		//TopicToEntityMapper ttem = new TopicToEntityMapper();
    		//ttem.saveFilteredQrels(ttem.filterQrels());
    		//QueryEnhancer.enhanceQuery();
    		//SnomedDictionaryInitializer sdi = new SnomedDictionaryInitializer();
    		//sdi.initialize();
    		//QrelFilter qf = new QrelFilter();
    		//qf.filterByMapping();
    		ResultsAnalyzer ra = new ResultsAnalyzer("KB");
    		//ra.getTopics();
    		ra.getPrecision();
    		//ra.getINFO(true);
    		//ra.findUnmappedEntities();
    		//ra.getTopicNums();
    		//PrintStream outputStream = new PrintStream(new FileOutputStream("details2.txt",true));
    		//MedLink.schedule(outputStream);
    		//ra.reformDoc();
    		//LinkerTester lt = new LinkerTester();
    		//lt.test("KB");
    		//lt.generateQrels2();
    		//lt.getTopicNums();
    		//PrintStream outputStream = new PrintStream(new FileOutputStream("Details2.txt",true));
    		//MedLink.schedule(outputStream);
    		//ra.findCommonEntities();
    		//Del.getDiff();
    		//DocumentExtractor de = new DocumentExtractor();
    		//de.saveDocs();
    		//SnomedDictionaryEnhancer sde = new SnomedDictionaryEnhancer();
    		//sde.enhanceDictionary();
    		//sde.mapSythesizedEntities();
    		//sde.filterSythesizedEntitiesByCategory();
    		//SnomedDictionaryInitializer sdi = new SnomedDictionaryInitializer();
    		//DictionaryHashMap dhm = new DictionaryHashMap();
    		//try {
    		//	dhm = sdi.initialize();
    		//} catch (IOException e) {
    		//	e.printStackTrace();
    		//}
    		
    		//SnomedDictionarySaver sds = new SnomedDictionarySaver(dhm,sde.readInCompleteEntities());
    		//sds.save();
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

