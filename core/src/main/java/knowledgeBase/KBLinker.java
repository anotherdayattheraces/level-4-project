package knowledgeBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;

import customEntityLinker.MedLink;
import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;
import entityRetrieval.core.Pair;
import evaluation.MedLinkEvaluator;
import evaluation.TopicToEntityMapper;

public class KBLinker {
	private String path;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;
	private List<ScoredDocument> scoredDocs;
	private ArrayList<String> topics;
	public int topicChoice;
	private ArrayList<String> blacklist;
	public Node root;

	public KBLinker(){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
		this.path="C:/Work/Project/samples/treccar/paragraphcorpus";
		Pair<Integer, String> topicChoicePair=MedLinkEvaluator.generateRandomTopic(topics);
		this.query=topicChoicePair.getR();
		this.topicChoice=topicChoicePair.getL();
		System.out.println("Chosen query: "+query);
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
		this.root=orchestrator.getRoot();
		try {
			this.blacklist=MedLink.readBlackList("C:/Work/Project/samples/prototype4/level-4-project/core/KblinkerBlacklist.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public KBLinker(int topicChoice){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
		this.path="C:/Work/Project/samples/treccar/paragraphcorpus";
		this.query=topics.get(topicChoice);
		this.topicChoice=topicChoice;
		System.out.println("Chosen query: "+query);
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
		this.root=orchestrator.getRoot();
		try {
			this.blacklist=MedLink.readBlackList("C:/Work/Project/samples/prototype4/level-4-project/core/KblinkerBlacklist.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public KBLinker(int i, String topicPath) {
		this.topics=TopicToEntityMapper.readTopics(topicPath);
		this.path="C:/Work/Project/samples/treccar/paragraphcorpus";
		this.query=topics.get(i);
		System.out.println("Chosen query: "+query);
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 1);
		this.root=orchestrator.getRoot();
		this.blacklist=new ArrayList<String>();

	}
	public ArrayList<Entity> getEntitiesFromText(){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		KBSearcher searcher = new KBSearcher();
		DiskIndex index = null;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
		try {
			index = new DiskIndex(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(ScoredDocument sd:scoredDocs){
			Document currentDoc=null;
			try {
				currentDoc=index.getDocument(sd.documentName, dc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			int lineNum=1;
			String onePreviousWord = null;
			String twoPreviousWord = null;
			String threePreviousWord = null;
			String twoWords = null;
			String threeWords = null;
			String fourWords = null;
			for(String currentWord:currentDoc.terms){
				if(currentWord==null||currentWord.contains(" ")) continue;
				if(lineNum>1){
					twoWords = onePreviousWord+" "+currentWord;
				}
				if(lineNum>2){
					threeWords = twoPreviousWord+" "+onePreviousWord+" "+currentWord;
				}
				if(lineNum>3){
					fourWords = threePreviousWord+" "+twoPreviousWord+" "+onePreviousWord+" "+currentWord;
					threePreviousWord=twoPreviousWord;
				}
				if(lineNum>2){
					threePreviousWord=twoPreviousWord;
				}
				if(lineNum>1){
					twoPreviousWord=onePreviousWord;
				}
				//System.out.println(currentWord);
				onePreviousWord=currentWord;
				lineNum++;
				currentWord = currentWord.substring(0, 1).toUpperCase()+currentWord.substring(1); //format current word: brass -> Brass
				if(twoWords!=null&&!blacklist.contains(twoWords)){
					twoWords = SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(twoWords);
					if(searcher.lookupTerm(twoWords)){
						addEntity(twoWords,entities,sd);
						//System.out.println("Found entity: "+twoWords);
					}
				}
				if(threeWords!=null&&!blacklist.contains(fourWords)){
					threeWords = SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(threeWords);
					if(searcher.lookupTerm(threeWords)){
						addEntity(threeWords,entities,sd);
						//System.out.println("Found entity: "+threeWords);

					}
				}
				if(fourWords!=null&&!blacklist.contains(fourWords)){
					fourWords = SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(fourWords);
					if(searcher.lookupTerm(fourWords)){
						addEntity(fourWords,entities,sd);
						//System.out.println("Found entity: "+fourWords);

					}
				}
				if(searcher.lookupTerm(currentWord)&&!blacklist.contains(currentWord)){
					addEntity(currentWord,entities,sd);
					//System.out.println("Found entity: "+currentWord);
				}
			}
		}
		boolean t=true;
		if(t==true){
			return entities;
		}
		System.out.println("Num unfiltered entities: "+entities.size());
		KBFilter kbfilter = new KBFilter(entities,blacklist);
		entities=kbfilter.filterEntities(null);
		System.out.println("Num filtered entities: "+entities.size());
		//this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(entities);
		//MedLinkEvaluator.setMentionProbablities(entities, entitiesPerDoc); //calculate the mention probabilities for each entity per doc
		return entities;
	}
	public List<ScoredDocument> getScoredDocuments(){
		return this.scoredDocs;
	}
	public void addEntity(String name, ArrayList<Entity> entities, ScoredDocument docid){
		for(Entity e:entities){
			if(e.getName().equals(name)){
				e.addAppearance(docid);
				return;
			}
		}
		Entity toAdd = new Entity(name,createID(name));
		toAdd.addAppearance(docid);
		entities.add(toAdd);
		return;
		
	}
	public String getQuery(){
		return this.query;
	}
	public static String createID(String name){
		return name.replaceAll(" ", "%20");
	}
	public HashMap<String,ArrayList<Entity>> getMapping(){
		return this.mapping;
	}
	public int getMaxTopics(){
		return this.topics.size();
	}

}
