package entityRetrieval.core;


import java.util.ArrayList;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Tag;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;

import dictionary.DictionaryHashMap;


public class TermCounter {
	private DictionaryHashMap dictionary;
	private ArrayList<Long> documents;
	private ArrayList<Pair<Entity,Integer>> matchedEntities;
	private String path;
	
	public TermCounter(ArrayList<Long> docs, DictionaryHashMap dictionary){
		this.dictionary = dictionary;
		this.documents = docs;
		this.matchedEntities = new ArrayList<Pair<Entity,Integer>>();
		this.path =  "C:/Work/Project/samples/Pub_Med_Index";
		
	}
	
	public ResultSet matchEntities() throws Exception{
		Retrieval index = RetrievalFactory.instance( path );
		String twoTerms = null;
		String threeTerms=null;
		int line=1;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );
		for(Long d:documents){
			Document doc = index.getDocument( index.getDocumentName( d ), dc );
			for(String term : doc.terms ) {
				if(line>1){
					twoTerms = term;
				}
				else if(line>2){
					threeTerms = twoTerms;
				}
				line++;
				if(term.length()<3) continue;
					System.out.println(term);
					if(dictionary.lookupString(term)){ //dictionary entity equals term
						System.out.println("entity match");
						Boolean exists =  false;
						for(Pair<Entity,Integer> pair: matchedEntities){
							if(term.toLowerCase().equals(pair.getL().getName())){
								System.out.println("entity exists already");
							    pair.setR(pair.getR()+1);
							    exists = true;}
							else if(pair.getL().getName().equals(twoTerms+" "+term)){
								System.out.println("entity exists already");
							    pair.setR(pair.getR()+1);
							    exists = true;}
								
							
							else if(pair.getL().getName().equals(threeTerms+twoTerms+" "+term)){
								System.out.println("entity exists already");
							    pair.setR(pair.getR()+1);
							    exists = true;
							    	}
							}    		
						        if(!exists){
						        	System.out.println("adding new entity");
						        	matchedEntities.add(new Pair<Entity, Integer>(new Entity(term),0));
						            		}
					            	}
				            		
			            	}
			            
			        
		}
			    
			
		
		return new ResultSet(matchedEntities);
		
	}

}
