package customEntityLinker;


import java.io.IOException;
import java.util.ArrayList;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;

import dictionary.DictionaryHashMap;
import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;


public class MedLink {
	private DictionaryHashMap dictionary;
	private ArrayList<Long> documents;
	private ArrayList<Pair<Entity,Integer>> matchedEntities;
	private String path;
	
	
	public MedLink(ArrayList<Long> docs, DictionaryHashMap dictionary){
		this.dictionary = dictionary;
		this.documents = docs;
		this.matchedEntities = new ArrayList<Pair<Entity,Integer>>();
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
		
		
	}
	
	public ArrayList<Entity> matchEntities(){
		ArrayList<Entity> foundEntities = new ArrayList<Entity>();
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
		int docNo = 1;
		for(Long d:documents){
			System.out.println("Processing doc "+docNo++ +" of "+documents.size());
			Document doc=null;
			try {
				doc = index.getDocument( index.getDocumentName( d ), dc );
			} catch (IOException e) {
				return null;
			}
			int line=1;
			for(String term : doc.terms ) {
				//System.out.println(term);
				term = term.toLowerCase();
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
					for(Entity entity:foundEntities){
						if(one&&entity.getName().equals(term)){
							entity.addAppearance(d);
							exists = true;
						}
						else if(two&&entity.getName().equals(twoWords)){
							entity.addAppearance(d);
							exists = true;
						}
						else if(three&&entity.getName().equals(threeWords)){
							entity.addAppearance(d);
							exists=true;
						}
					}
					if(!exists){
						if(one){
							dictionary.getEntity(term).addAppearance(d);;
							foundEntities.add(dictionary.getEntity(term));
							//matchedEntities.add(new Pair<Entity, Integer>(new Entity(term),1));
							}
						else if(two){
							dictionary.getEntity(twoWords).addAppearance(d);;
							foundEntities.add(dictionary.getEntity(twoWords));
							//matchedEntities.add(new Pair<Entity, Integer>(new Entity(twoWords),1));
							}
						else if(three){
							dictionary.getEntity(threeWords).addAppearance(d);;
							foundEntities.add(dictionary.getEntity(threeWords));
							//matchedEntities.add(new Pair<Entity, Integer>(new Entity(threeWords),1));
							}
						}
					
					
					}
							
						}
		return foundEntities;
		
	}


}
