package knowledgeBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.ScoredDocument;

import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;
import evaluation.MedLinkEvaluator;
import evaluation.TopicToEntityMapper;

public class KBLinker {
	private String path;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;
	private List<ScoredDocument> scoredDocs;
	private HashMap<Long,Integer> entitiesPerDoc;

	
	public KBLinker(){
		this.path="C:/Work/Project/samples/treccar/paragraphcorpus";
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		this.mapping = mapper.generateRelevantEntities();
		this.query=MedLinkEvaluator.generateRandomTopic(mapping);
		System.out.println("Chosen query: "+query);
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
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
				onePreviousWord=currentWord;
				lineNum++;
				currentWord = currentWord.substring(0, 1).toUpperCase()+currentWord.substring(1);
				if(twoWords!=null){
					twoWords = SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(twoWords);
				}
				if(threeWords!=null){
					threeWords = SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(threeWords);
				}
				if(fourWords!=null){
					fourWords = SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(fourWords);
				}
				if(searcher.lookupTerm(currentWord)){
					addEntity(currentWord,entities,sd.document);

				}
				if(twoWords!=null){
					if(searcher.lookupTerm(twoWords)){
						addEntity(twoWords,entities,sd.document);

					}
				}
				if(threeWords!=null){
					if(searcher.lookupTerm(threeWords)){
						addEntity(threeWords,entities,sd.document);

					}
				}
				if(fourWords!=null){
					if(searcher.lookupTerm(fourWords)){
						addEntity(fourWords,entities,sd.document);
					}
				}

			}
		}
		this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(entities);
		MedLinkEvaluator.setMentionProbablities(entities, entitiesPerDoc); //calculate the mention probabilities for each entity per doc
		return entities;
	}
	public List<ScoredDocument> getScoredDocuments(){
		return this.scoredDocs;
	}
	public void addEntity(String name, ArrayList<Entity> entities, Long docid){
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

}
