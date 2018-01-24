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

import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;

public class DocumentLinkReader {
	private List<ScoredDocument> documents;
	private String path;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;
	private HashMap<Long,Integer> entitiesPerDoc;

	
	public DocumentLinkReader(){
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		this.mapping = mapper.generateRelevantEntities();
		Random r = new Random();
		int topicChoice = r.nextInt(mapping.keySet().size());
		topicChoice=6;
		Set<String> keySet = mapping.keySet();
		Iterator<String> i = keySet.iterator();
		int count = 0;
		while(count!=topicChoice){
			i.next();
			count++;
		}
		this.query=i.next().toString();
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
				   e.addAppearance(sd.document);
				   foundEntities.add(e);
				   continue;
			   }
			   Boolean exists = false;
			   for(Entity fe:foundEntities){
				   if(fe.getId().equals(entityID)){ //entity has already been found
					   exists=true;
					   fe.addAppearance(sd.document);
				   		}
				   }
			   if(!exists){
				   Entity e = new Entity(entityName,entityID);
				   e.addAppearance(sd.document);
				   foundEntities.add(e);
			   }
			   
			} 
			
			
		}
		try {
			index.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(foundEntities);
		MedLinkEvaluator.setMentionProbablities(foundEntities, entitiesPerDoc);
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


}
