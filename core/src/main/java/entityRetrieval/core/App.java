package entityRetrieval.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import dictionary.DictionaryHashMap;
import dictionary.DictionaryInitializer;
import dictionary.SnomedDictionarySaver;
import evaluation.SearchEvaluator;
import evaluation.TopicToEntityMapper;
import dictionary.DbpediaDictionarySaver;
import generation.DbpediaDictionaryGenerator;
import generation.SnomedDictionaryGenerator;


public class App 
{
    public static void main( String[] args ) throws Exception{
    	
    	String query = "autism";
    	//SnomedDictionaryGenerator sdg = new SnomedDictionaryGenerator();
    	//DictionaryHashMap dhm = sdg.generateEntities();
    	//SnomedDictionarySaver ds = new SnomedDictionarySaver(dhm);
    	//ds.save();
    	//DictionaryInitializer di = new DictionaryInitializer();
    	//DictionaryHashMap dictionary = di.initialize();
    	//TopicRetriever tr = new TopicRetriever();
    	//ArrayList<Topic> topics = tr.retreiveTopics();
    	SearchEvaluator se = new SearchEvaluator();
    	se.evaluate();
    	//SingleQuerySearch sqs = new SingleQuerySearch(query);
    	//sqs.search();


	        

	   
    }
    
    public void run(String[] args){
    	
    }
	    
}

