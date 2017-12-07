package Entity_Retrieval_Engine.Entity_Linker;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Tag;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;


public class TermCounter {
	private ArrayList<Entity> dictionary;
	private ArrayList<Long> documents;
	private ArrayList<Pair<Entity,Integer>> matchedEntities;
	private String path;
	
	public TermCounter(ArrayList<Long> docs, ArrayList<Entity> entities){
		this.dictionary = entities;
		this.documents = docs;
		this.matchedEntities = new ArrayList<Pair<Entity,Integer>>();
		this.path =  "C:/Work/Project/samples/Pub_Med_Index";
		
	}
	
	public ArrayList<Pair<Entity,Integer>> matchEntities() throws Exception{
		Retrieval index = RetrievalFactory.instance( path );
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );


		for(Long d:documents){
			Document doc = index.getDocument( index.getDocumentName( d ), dc );
			for ( Tag tag : doc.tags ) { // doc.tags return a list of document fields
			    if ( tag.name.equals( "Text" ) )
					for(Entity e:dictionary){
						for ( int position = tag.begin; position < tag.end; position++ ) {
				            String term = doc.terms.get( position );
							if(term.toLowerCase()==e.getName().toLowerCase()){ //dictionary entity exists in text
								System.out.println("entity match");
				
							    Boolean exists =  false;
							    for(Pair<Entity,Integer> pair: matchedEntities){
							    	if(e.getName()==pair.getL().getName()){
							        System.out.println("entity exists already");
							        pair.setR(pair.getR()+1);
							        exists = true;
						            			}
						            		}
						        if(!exists){
						        	System.out.println("adding new entity");
						        	matchedEntities.add(new Pair<Entity, Integer>(new Entity(e.getName()),0));
						            		}
					            	}
				            		
			            	}
			            }
			        }
		}
			    
			
		
		return matchedEntities;
		
	}

}
