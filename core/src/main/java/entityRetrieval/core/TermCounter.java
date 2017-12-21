package entityRetrieval.core;


import java.io.IOException;
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
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
		
	}
	
	public ResultSet matchEntities(){
		Retrieval index=null;
		try {
			index = RetrievalFactory.instance( path );
		} catch (Exception e1) {
			System.err.println("index not found");
			e1.printStackTrace();
			return null;
		}
		String previousTerm=null; // the previous term
		String twopreviousTerm=null; // the term before the previous term
		Boolean doubleTerm=false;
		Boolean tripleTerm=false;
		String twoWords = null;
		String threeWords = null;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );
		for(Long d:documents){
			Document doc=null;
			try {
				doc = index.getDocument( index.getDocumentName( d ), dc );
			} catch (IOException e) {
				System.err.println("article: "+d+" not contained within index");
				e.printStackTrace();
				return null;
			}
			int line=1;
			for(String term : doc.terms ) {
				if(line>1){
					doubleTerm=true;
					
				}
				if(line>2){
					tripleTerm=true;
				}
				Boolean one=false;
				Boolean two=false;
				Boolean three=false;
				Boolean exists =  false;
				
				if(doubleTerm){
					twoWords=previousTerm+" "+term;
					//System.out.println(twoWords);
				}
				if(tripleTerm){
					threeWords=twopreviousTerm+" "+previousTerm+" "+term;
					//System.out.println(threeWords);
				}
				if(line>1){
					twopreviousTerm = previousTerm;
				}
				previousTerm = term;
				line++;
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
							matchedEntities.add(new Pair<Entity, Integer>(new Entity(term),1));}
						else if(two){
							matchedEntities.add(new Pair<Entity, Integer>(new Entity(twoWords),1));
							}
						else if(three){
							matchedEntities.add(new Pair<Entity, Integer>(new Entity(threeWords),1));
							}
						}
					
					
					}
							
						}
		return new ResultSet(matchedEntities);
		
	}

}
