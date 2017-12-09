package entityRetrieval.core;


import java.util.ArrayList;
import org.lemurproject.galago.core.parse.Document;
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
		Boolean doubleTerm=false;
		Boolean tripleTerm=false;
		String twoWords = null;
		String threeWords = null;
		int line=1;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );
		for(Long d:documents){
			Document doc = index.getDocument( index.getDocumentName( d ), dc );
			for(String term : doc.terms ) {
				if(line>1){
					twoTerms = term;
					doubleTerm=true;
					
				}
				else if(line>2){
					threeTerms = twoTerms;
					tripleTerm=true;
				}
				Boolean one=false;
				Boolean two=false;
				Boolean three=false;
				Boolean exists =  false;
				line++;
				if(doubleTerm) twoWords=twoTerms+" "+term;
				if(tripleTerm) threeWords=threeTerms+" "+twoTerms+" "+term;
				if(term.length()<3) continue; //term must have length >=3
				if(dictionary.lookupString(term)){ //dictionary contains term
						one = true;
					}
					else if(two&&dictionary.lookupString(twoWords)){
						two = true;
					}
					else if(three&&dictionary.lookupString(threeWords)){
						three = true;
					}
					if(!one&&!two&&!three) continue; //no entity matches
					for(Pair<Entity,Integer> pair:matchedEntities){
						if(one&&pair.getL().getName().equals(term)){
							pair.setR(pair.getR()+1);
							exists = true;
						}
						else if(two&&pair.getL().getName().equals(twoWords)){
							pair.setR(pair.getR()+1);
							exists = true;
						}
						else if(three&&pair.getL().getName().equals(threeWords)){
							pair.setR(pair.getR()+1);
							exists=true;
						}
					}
					if(!exists){
						if(one){
						    System.out.println("adding term: "+term);
							matchedEntities.add(new Pair<Entity, Integer>(new Entity(term),1));}
						else if(two){
						    System.out.println("adding term: "+twoWords);
							matchedEntities.add(new Pair<Entity, Integer>(new Entity(twoWords),1));
							}
						else if(three){
						    System.out.println("adding term: "+threeWords);
							matchedEntities.add(new Pair<Entity, Integer>(new Entity(threeWords),1));
							}
						}
					}
							
						}
		return new ResultSet(matchedEntities);
		
	}

}
