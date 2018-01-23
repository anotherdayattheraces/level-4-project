package entityRetrieval.core;

import java.io.IOException;
import java.util.ArrayList;

import customEntityLinker.MedLink;
import dictionary.DictionaryHashMap;
import dictionary.DbpediaDictionaryInitializer;

public class SingleQuerySearch {
	private String query;
	private DictionaryHashMap dictionary;
	
	public SingleQuerySearch(String query) throws IOException{
		DbpediaDictionaryInitializer di = new DbpediaDictionaryInitializer();
		this.dictionary = di.initialize();
		this.query = query;
	}
	
	public void search() throws Exception{
		ArrayList<Pair<Entity,Integer>> entitytoMentions = new ArrayList<Pair<Entity,Integer>>();
		 DocumentIdentifier di = new DocumentIdentifier();
		 ArrayList<Long> documents = di.getRelevantDocuments(query); //finds the doc id's that contain the query word
		// MedLink tc = new MedLink(documents,dictionary);
		 //ArrayList<Entity> results = tc.matchEntities();
		 //for(Pair<Entity,Integer> result:results.getResultSet()){
		//	 entitytoMentions.add(result);
		 //   		}
		 	// return entitytoMentions;
		    	}
	
	

}
