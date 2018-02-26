package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Tag;
import org.lemurproject.galago.core.retrieval.ScoredDocument;

import customEntityLinker.MedLink;
import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;
import entityRetrieval.core.Pair;
import knowledgeBase.KBFilter;

public class DocumentLinkReader {
	private List<ScoredDocument> documents;
	private String path;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;
	private ArrayList<String> topics;
	public int topicChoice;

	
	public DocumentLinkReader(){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
		Pair<Integer, String> topicChoicePair=MedLinkEvaluator.generateRandomTopic(topics);
		this.query=topicChoicePair.getR();
		this.topicChoice=topicChoicePair.getL();
		System.out.println("Chosen query: "+query);
		GalagoOrchestrator orchestrator = new GalagoOrchestrator();
		this.documents=orchestrator.getDocuments(query, 50);
		this.path="C:/Work/Project/samples/treccar/paragraphcorpus";
	}
	public DocumentLinkReader(String query){
		this.query=query;
		System.out.println("Chosen query: "+query);
		GalagoOrchestrator orchestrator = new GalagoOrchestrator();
		this.documents=orchestrator.getDocuments(query, 50);
		System.out.println("got docs");
		this.path="C:/Work/Project/samples/treccar/paragraphcorpus";
	}
	
	public DocumentLinkReader(int topicChoice){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
		this.query=topics.get(topicChoice);
		this.topicChoice=topicChoice;
		System.out.println("Chosen query: "+query);
		GalagoOrchestrator orchestrator = new GalagoOrchestrator();
		this.documents=orchestrator.getDocuments(query, 50);
		this.path="C:/Work/Project/samples/treccar/paragraphcorpus";
	}
	
	public ArrayList<Entity> getEntitiesFromLinks(){
		DiskIndex index = null;
		String linkIndicator = "<link tokenizeTagContent";
		try {
			index = new DiskIndex(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
		ArrayList<Entity> foundEntities = new ArrayList<Entity>();
		for(ScoredDocument sd:documents){
			Document currentDoc=null;
			try {
				currentDoc=index.getDocument(sd.documentName, dc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = -1; (i = currentDoc.text.indexOf(linkIndicator, i + 1)) != -1; i++) {
			   int start = i+33; //33 is the string len of <link tokenizeTagContent
			   int end = currentDoc.text.substring(start).indexOf("</link>")+start;
			   String entityID = currentDoc.text.substring(start, end);
			   String entityName=entityID.replaceAll("%20", " ");			   
			   if(foundEntities.isEmpty()){
				   Entity e = new Entity(entityName,entityID);
				   e.addAppearance(sd);
				   foundEntities.add(e);
				   continue;
			   }
			   Boolean exists = false;
			   for(Entity fe:foundEntities){
				   if(fe.getId().equals(entityID)){ //entity has already been found
					   exists=true;
					   fe.addAppearance(sd);
				   		}
				   }
			   if(!exists){
				   Entity e = new Entity(entityName,entityID);
				   e.addAppearance(sd);
				   foundEntities.add(e);
			   }
			   
			} 
		}
		try {
			index.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Num unfiltered entities: "+foundEntities.size());
		KBFilter kbfilter = new KBFilter(foundEntities);
		foundEntities=kbfilter.filterEntities(null);
		System.out.println("Num filtered entities: "+foundEntities.size());
		
		foundEntities=MedLink.scoreAndRankEntities(foundEntities, getScoredDocuments());
		//this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(foundEntities);
		//MedLinkEvaluator.setMentionProbablities(foundEntities, entitiesPerDoc); //calculate the mention probabilities for each entity per doc
		for(Entity e:foundEntities){
			System.out.println(e.getName());
		}
		return foundEntities;
	}
	public List<ScoredDocument> getScoredDocuments(){
		return this.documents;
	}
	public HashMap<String,ArrayList<Entity>> getMapping(){
		return this.mapping;
	}
	public String getQuery(){
		return this.query;
	}
	public int getMaxTopics(){
		return this.topics.size();
	}


}
