package customEntityLinker;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;

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
	public int topicChoice;
	private static String kbPath ="C:/Work/Project/samples/Unprocessed_Index";


	
	
	public MedLink(){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
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
		this.scoredDocs = orchestrator.getDocuments(query, 25); //get top 50 documents from galago search of query
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.snomedToWikiMappings=readInMappings(mappingPath);
	}
	public MedLink(int topicChoice){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
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
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
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
								if(!addAppearance(foundEntities,term,sd)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(term,term));
										foundEntities.get(0).addAppearance(sd);
									}
									else{
										foundEntities.add(new Entity(term,term.replaceAll(" ", "%20")));
										foundEntities.get(foundEntities.size()-1).addAppearance(sd);
									}			
								}
							}
							if(two){
								if(!addAppearance(foundEntities,twoWords,sd)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(twoWords,twoWords.replaceAll(" ", "%20")));
										foundEntities.get(0).addAppearance(sd);
									}
									else{
										foundEntities.add(new Entity(twoWords,twoWords.replaceAll(" ", "%20")));
										foundEntities.get(foundEntities.size()-1).addAppearance(sd);;
										
									}			
								}
							}
							else if(three){
								if(!addAppearance(foundEntities,threeWords,sd)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(threeWords,threeWords.replaceAll(" ", "%20")));
										foundEntities.get(0).addAppearance(sd);
									}
									else{
										foundEntities.add(new Entity(threeWords,threeWords.replaceAll(" ", "%20")));
										foundEntities.get(foundEntities.size()-1).addAppearance(sd);;
										
									}			
								}
							}
							else if(four){
								if(!addAppearance(foundEntities,fourWords,sd)){
									if(foundEntities.isEmpty()){
										foundEntities.add(0, new Entity(fourWords,fourWords.replaceAll(" ", "%20")));
										foundEntities.get(0).addAppearance(sd);
									}
									else{
										foundEntities.add(new Entity(fourWords,fourWords.replaceAll(" ", "%20")));
										foundEntities.get(foundEntities.size()-1).addAppearance(sd);;
										
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
		outputStream.println("Mapping: ");
		outputStream.println(" ");
		foundEntities = mapEntities(snomedToWikiMappings, foundEntities,outputStream); //map snomed Entities to wiki entities
		
		outputStream.println("Num mapped entities: "+foundEntities.size());
		outputStream.println("Filtering: ");
		outputStream.println(" ");
		System.out.println("Num mapped-unfiltered entities: "+foundEntities.size());
		KBFilter kbfilter = new KBFilter(foundEntities);
		foundEntities=kbfilter.filterEntities(outputStream);
		System.out.println("Num filtered entities: "+foundEntities.size());
		
		//this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(foundEntities); //calculate the number of found entities per document
		getEntityDocuments(foundEntities,kbPath); //set internal identifiers for each doc
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
	
	public static ArrayList<Entity> mapEntities( HashMap<String,HashMap<String,String>> snomedToWikiMappings, ArrayList<Entity> unmappedEntities, PrintStream outputStream){
		ArrayList<Entity> mappedEntities = new ArrayList<Entity>();
		for(Entity e:unmappedEntities){
			Boolean mapped=false;
			String mappedName = null;
			outputStream.println("Mapping entity: "+e.getName());
			if(e.getName().length()<3){
				for(int i=e.getName().length();i<=3;i++){
					e.setName(e.getName()+" ");
				}
			}
			if(!snomedToWikiMappings.containsKey(e.getName().substring(0, 3).toLowerCase())){
				//System.out.println("Unable to map entity: "+e.getName());
				continue;
			}
			mappedName=snomedToWikiMappings.get(e.getName().substring(0, 3).toLowerCase()).get(e.getName().toLowerCase());
			if(mappedName==null&&e.getName().contains("(")){
				mappedName=snomedToWikiMappings.get(e.getName().substring(0, 3).toLowerCase()).get(EntityMatcher.removeBracketDescription(e.getName().toLowerCase().trim()));
				outputStream.println("Formatted: "+e.getName()+" without brackets");
			}
			if(mappedName!=null){
				outputStream.println("Mapped "+e.getName()+" to "+mappedName);
				//System.out.println("Mapped "+e.getName()+" to "+mappedName);
				Boolean merge=false;
				for(Entity entity:mappedEntities){
					if(entity.getName().equals(mappedName)){
						entity.mergeEntityApps(e);
						outputStream.println("Merged apps for entities: "+entity.getName()+" and "+e.getName());
						System.out.println("Merged apps for entities: "+entity.getName()+" and "+e.getName());
						merge=true;
						break;
					}
				}
				if(merge){
					continue;
				}
				e.setName(mappedName);
				mappedEntities.add(e);
				mapped=true;
			}
			if(!mapped){
				outputStream.println("Unable to map entity: "+e.getName());
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
	public Boolean addAppearance(ArrayList<Entity> foundEntities,String term,ScoredDocument scoredDoc){
		for(Entity entity:foundEntities){
			if(entity.getName().equals(term)){
				entity.addAppearance(scoredDoc);
				return true;
			}
		}
		return false;
	}
	public static void getEntityDocuments(ArrayList<Entity> entities, String kbPath){
		DiskIndex index=null;
		try {
			index = new DiskIndex(kbPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Entity entity:entities){
			long document = 0;
			try {
				document = index.getIdentifier(entity.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			entity.setDocument(document);
		}
	}
	
	public static ArrayList<Entity> scoreAndRankEntities(ArrayList<Entity> entities, List<ScoredDocument> scoredDocs){
		MedLinkEvaluator.calculateEntitiesPerDoc(entities, scoredDocs);
		Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
		MedLinkEvaluator.setMentionProbablities(entities, scoredDocs); //calculate the mention probabilities for each entity per doc
		MedLinkEvaluator.setScores(entities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(entities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(entities);
		return entities;
	}
	
	public static ArrayList<String> readBlackList(String path) throws IOException{
		FileReader file = null;
		try {
			file = new FileReader(path);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		ArrayList<String> blacklist = new ArrayList<String>();
		while((line=br.readLine())!=null){
			blacklist.add(line);
		}
		return blacklist;
	}
}
