package customEntityLinker;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;

import dictionary.DictionaryHashMap;
import dictionary.SnomedDictionaryInitializer;
import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;
import entityRetrieval.core.Pair;
import evaluation.EntityMatcher;
import evaluation.MedLinkEvaluator;
import evaluation.TopicToEntityMapper;
import knowledgeBase.KBFilter;


public class MedLink {
	private DictionaryHashMap dictionary;
	private List<ScoredDocument> scoredDocs;
	private String path;
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	private String mappingPath;
	private HashMap<String,HashMap<String,String>> snomedToWikiMappings;
	private ArrayList<String> topics;
	private HashMap<Long,Integer> entitiesPerDoc;
	public int topicChoice;

	
	
	public MedLink(){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/treccar/topics.txt");
		Pair<Integer, String> topicChoicePair=MedLinkEvaluator.generateRandomTopic(topics);
		this.query=topicChoicePair.getR();
		this.topicChoice=topicChoicePair.getL();
		System.out.println("Chosen query: "+query);
		SnomedDictionaryInitializer sdi = new SnomedDictionaryInitializer();
		try {
			this.dictionary = sdi.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.snomedToWikiMappings=readInMappings(mappingPath);
	}
	public MedLink(int topicChoice){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/treccar/topics.txt");
		this.query=topics.get(topicChoice);
		this.topicChoice=topicChoice;
		System.out.println("Chosen query: "+query);
		SnomedDictionaryInitializer sdi = new SnomedDictionaryInitializer();
		try {
			this.dictionary = sdi.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.snomedToWikiMappings=readInMappings(mappingPath);	
	}
	public MedLink(int topicChoice, DictionaryHashMap dictionary){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/treccar/topics.txt");
		this.query=topics.get(topicChoice);
		this.topicChoice=topicChoice;
		System.out.println("Chosen query: "+query);
		this.dictionary=dictionary;
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.snomedToWikiMappings=readInMappings(mappingPath);	
	}
	
	public ArrayList<Entity> matchEntities(PrintStream outputStream){
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
		String threepreviousTerm=null;
		Boolean doubleTerm=false;
		Boolean tripleTerm=false;
		Boolean quadTerm=false;
		String twoWords = null;
		String threeWords = null;
		String fourWords=null;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );
		for(ScoredDocument sd:scoredDocs){
			//System.out.println("Processing doc "+docNo++ +" of "+scoredDocs.size());
			Document doc=null;
			try {
				doc = index.getDocument( index.getDocumentName( sd.document ), dc );
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
				if(line>3){
					quadTerm=true;
				}
				Boolean one=false;
				Boolean two=false;
				Boolean three=false;
				Boolean four=false;
				
				if(doubleTerm){
					twoWords=previousTerm+" "+term;
					//System.out.println(twoWords);
				}
				if(tripleTerm){
					threeWords=twopreviousTerm+" "+previousTerm+" "+term;
					//System.out.println(threeWords);
				}
				if(quadTerm){
					fourWords=threepreviousTerm+" "+twopreviousTerm+" "+previousTerm+" "+term;
				}
				if(line>2){
					threepreviousTerm=twopreviousTerm;
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
				if(doubleTerm&&dictionary.lookupString(twoWords)){
						//System.out.println("found match for term: "+twoWords);
						two = true;
					}
				if(tripleTerm&&dictionary.lookupString(threeWords)){
						three = true;
					}
				if(quadTerm&&dictionary.lookupString(fourWords)){
						four = true;
					}
					if(!one&&!two&&!three&&!four) continue; //no entity matches
							if(one){
								if(!addAppearance(foundEntities,term,sd.document)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(term,term));
										foundEntities.get(0).addAppearance(sd.document);
									}
									else{
										foundEntities.add(new Entity(term,term.replaceAll(" ", "%20")));
										System.out.println("Adding new entity: "+term);
										foundEntities.get(foundEntities.size()-1).addAppearance(sd.document);
										System.out.println("Adding app for entity: "+foundEntities.get(foundEntities.size()-1).getName());

										
									}			
								}
							}
							if(two){
								if(!addAppearance(foundEntities,twoWords,sd.document)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(twoWords,twoWords.replaceAll(" ", "%20")));
										foundEntities.get(0).addAppearance(sd.document);
									}
									else{
										foundEntities.add(new Entity(twoWords,twoWords.replaceAll(" ", "%20")));
										foundEntities.get(foundEntities.size()-1).addAppearance(sd.document);;
										
									}			
								}
							}
							else if(three){
								if(!addAppearance(foundEntities,threeWords,sd.document)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(threeWords,threeWords.replaceAll(" ", "%20")));
										foundEntities.get(0).addAppearance(sd.document);
									}
									else{
										foundEntities.add(new Entity(threeWords,threeWords.replaceAll(" ", "%20")));
										foundEntities.get(foundEntities.size()-1).addAppearance(sd.document);;
										
									}			
								}
							}
							else if(four){
								if(!addAppearance(foundEntities,fourWords,sd.document)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(fourWords,fourWords.replaceAll(" ", "%20")));
										foundEntities.get(0).addAppearance(sd.document);
									}
									else{
										foundEntities.add(new Entity(fourWords,fourWords.replaceAll(" ", "%20")));
										foundEntities.get(foundEntities.size()-1).addAppearance(sd.document);;
										
									}			
								}
							}
					}
							
						}
		try {
			index.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		outputStream.println(query);
		System.out.println("Num unmapped entities: "+foundEntities.size());
		outputStream.println("Num unmapped entities: "+foundEntities.size());
		foundEntities = mapEntities(snomedToWikiMappings, foundEntities); //map snomed Entities to wiki entities
		outputStream.println("Num mapped entities: "+foundEntities.size());
		System.out.println("Num mapped-unfiltered entities: "+foundEntities.size());
		KBFilter kbfilter = new KBFilter(foundEntities);
		foundEntities=kbfilter.filterEntities();
		System.out.println("Num filtered entities: "+foundEntities.size());
		this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(foundEntities); //calculate the number of found entities per document
		MedLinkEvaluator.setMentionProbablities(foundEntities, entitiesPerDoc); //calculate the mention probabilities for each entity per doc
		return foundEntities; 
		
	}
	public List<ScoredDocument> getScoredDocs(){
		return this.scoredDocs;
	}
	public HashMap<String,ArrayList<Entity>> getMapping(){
		return this.mapping;
	}
	public String getQuery(){
		return this.query;
	}
	
	public static ArrayList<Entity> mapEntities( HashMap<String,HashMap<String,String>> snomedToWikiMappings, ArrayList<Entity> unmappedEntities){
		ArrayList<Entity> mappedEntities = new ArrayList<Entity>();
		for(Entity e:unmappedEntities){
			Boolean mapped=false;
			String mappedName = null;
			//System.out.println("Mapping entity: "+e.getName());
			if(!snomedToWikiMappings.containsKey(e.getName().substring(0, 3).toLowerCase())){
				//System.out.println("Unable to map entity: "+e.getName());
				continue;
			}
			mappedName=snomedToWikiMappings.get(e.getName().substring(0, 3).toLowerCase()).get(e.getName().toLowerCase());
			if(mappedName==null&&e.getName().contains("(")){
				mappedName=snomedToWikiMappings.get(e.getName().substring(0, 3).toLowerCase()).get(EntityMatcher.removeBracketDescription(e.getName().toLowerCase().trim()));
				//System.out.println("Formatted: "+e.getName()+" without brackets");
			}
			if(mappedName!=null){
				//System.out.println("Mapped "+e.getName()+" to "+mappedName);
				e.setName(mappedName);
				mappedEntities.add(e);
				mapped=true;
			}
			if(!mapped){
				System.out.println("Unable to map entity: "+e.getName());
			}
		}
		return mappedEntities;
	}
	public static HashMap<String,HashMap<String,String>> readInMappings(String mappingPath){
		FileReader file = null;
		try {
			file = new FileReader(mappingPath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		HashMap<String,HashMap<String,String>> mappings = new HashMap<String,HashMap<String,String>>();
		try {
			while((line=br.readLine())!=null){
				String[] split = line.split("///");
				if(split[0].length()<3) continue;
				if(!mappings.containsKey(split[1].substring(0, 3))){
					String finalString=split[1].substring(0, 3);
					if(finalString.length()<3){
						for(int i=finalString.length();i<3;i++){
							finalString=finalString+" ";
						}
						if(mappings.containsKey(finalString)){
							mappings.get(split[1].substring(0, 3)).put(split[1], split[0]);
							continue;
						}
					}
					mappings.put(split[1].substring(0, 3), new HashMap<String,String>());
				}
				mappings.get(split[1].substring(0, 3)).put(split[1], split[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mappings;
	}
	public int getMaxTopics(){
		return this.topics.size();
	}
	public List<ScoredDocument> getScoredDocuments(){
		return this.scoredDocs;
	}
	public DictionaryHashMap getDictionary(){
		return this.dictionary;
	}
	public Boolean addAppearance(ArrayList<Entity> foundEntities,String term,Long docID){
		for(Entity entity:foundEntities){
			if(entity.getName().equals(term)){
				entity.addAppearance(docID);
				return true;
			}
		}
		return false;
	}
	


}
