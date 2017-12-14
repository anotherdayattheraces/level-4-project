package entityRetrieval.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dictionary.DictionaryHashMap;
import dictionary.DictionaryInitializer;
import dictionary.DictionarySaver;
import evaluation.SearchEvaluator;
import evaluation.Topic;
import evaluation.TopicRetriever;
import generation.DbpediaDictionaryGenerator;
import generation.SnomedDictionaryGenerator;


public class App 
{
    public static void main( String[] args ) throws Exception{
    	String query = "autism";
    	//List<String> x = new ArrayList<String>(Arrays.asList("icd9", "icd10", "meshId", "icdo"));
    	//DbpediaDictionaryGenerator db = new DbpediaDictionaryGenerator(x);
    	//DictionaryHashMap dhm = db.generateEntities();
    	//DictionarySaver ds = new DictionarySaver(dhm);
    	//ds.save();
    	//SnomedDictionaryGenerator sdg = new SnomedDictionaryGenerator();
    	//sdg.generateEntities();
    	//DictionaryInitializer di = new DictionaryInitializer();
    	//DictionaryHashMap dictionary = di.initialize();
    	TopicRetriever tr = new TopicRetriever();
    	ArrayList<Topic> topics = tr.retreiveTopics();
    	for(Topic t:topics){
    		System.out.println(t.getDisease());
    	}
    	SearchEvaluator se = new SearchEvaluator(topics);
    	//SingleQuerySearch sqs = new SingleQuerySearch(query);
    	//ArrayList<Pair<Entity,Integer>> results = sqs.search();
    	//System.out.println("These are the entities found to be the most relevant to your query: "+query);
    	//for(Pair<Entity,Integer> result:results){
        	//System.out.println(result);

    	//}

	   
    }
	    
}

