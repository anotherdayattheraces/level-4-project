package metamap;

import java.io.IOException;
import java.io.PrintStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;

import customEntityLinker.MedLink;
import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;
import entityRetrieval.core.Pair;
import evaluation.MedLinkEvaluator;
import evaluation.TopicToEntityMapper;
import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;
import knowledgeBase.KBFilter;



public class MetaMapEntityLinker {
	private List<ScoredDocument> scoredDocs;
	private String path;
	private String query;
	private HashMap<String,ArrayList<Entity>> mapping;
	private String mappingPath;
	private HashMap<String,HashMap<String,String>> snomedToWikiMappings;
	private ArrayList<String> topics;
	private HashMap<Long,Integer> entitiesPerDoc;
	public int topicChoice;
	

	
	public MetaMapEntityLinker(){
		//this.options = setOptions("-A,-K,-R [SNOMEDCT]");
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/treccar/topics.txt");
		Pair<Integer, String> topicChoicePair=MedLinkEvaluator.generateRandomTopic(topics);
		this.query=topicChoicePair.getR();
		this.topicChoice=topicChoicePair.getL();
		System.out.println("Chosen query: "+query);
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		this.mapping = mapper.generateRelevantEntities(query);
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.snomedToWikiMappings=MedLink.readInMappings(mappingPath);
	}
	public MetaMapEntityLinker(int topicChoice){
		//this.options = setOptions("-A,-K,-R [SNOMEDCT]");
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/treccar/topics.txt");
		this.query=topics.get(topicChoice);
		this.topicChoice=topicChoice;
		System.out.println("Chosen query: "+query);
		TopicToEntityMapper mapper = new TopicToEntityMapper();
		this.mapping = mapper.generateRelevantEntities(query);
		GalagoOrchestrator orchestrator=  new GalagoOrchestrator();
		this.scoredDocs = orchestrator.getDocuments(query, 50); //get top 50 documents from galago search of query
		this.path =  "C:/Work/Project/samples/treccar/paragraphcorpus";
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.snomedToWikiMappings=MedLink.readInMappings(mappingPath);
	}
	
	public ArrayList<Entity> generateEntities(PrintStream outputStream){
		ArrayList<Entity> foundEntities = new ArrayList<Entity>();
		Retrieval index=null;
		try {
			index = RetrievalFactory.instance( path );
		} catch (Exception e1) {
			System.err.println("index not found");
			e1.printStackTrace();
			return null;
		}
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );
		MetaMapApi api = new MetaMapApiImpl();
		//api.setTimeout(5000);
		api.setOptions("-K -A -y --prune 20");
		//System.out.println("Found "+scoredDocs.size()+" documents relevant to the query");
		int docNo=1;
		for(ScoredDocument scoredDoc:scoredDocs){
			Document doc=null;
			try {
				doc = index.getDocument( index.getDocumentName( scoredDoc.document ), dc );
			} catch (IOException e) {
				System.err.println("article: "+scoredDoc.document+" not contained within index");
				e.printStackTrace();
				return null;
			}
			
			String documentText = generateString(doc.terms);
			//System.out.println(documentText);
			System.out.println("Processing document "+docNo++ +" of "+scoredDocs.size());
			List<Result> resultList = api.processCitationsFromString(documentText);
			Result result = resultList.get(0);
			try {
				for (Utterance utterance: result.getUtteranceList()) {					
					//System.out.println(" Id: " + utterance.getId());
					//System.out.println(" Utterance text: " + utterance.getString());
					//System.out.println(" Position: " + utterance.getPosition());
					//System.out.println("Candidates:");
					for (PCM pcm: utterance.getPCMList()) {
						//System.out.println(pcm.getMappingList().size());
						if(pcm.getMappingList().size()==0) continue;
				          for (Mapping map: pcm.getMappingList()) {
				            for (Ev mapEv: map.getEvList()) {
				              //if(mapEv.getPreferredName().length()<3) continue;
				              if(!mapEv.getSources().contains("SNOMEDCT_US")&& !mapEv.getSources().contains("SNOMEDCT_VET")) continue;
				              //System.out.println("   Sources: " + mapEv.getSources());
				              //System.out.println("   Score: " + mapEv.getScore());
					          //System.out.println("   Preferred Name: " + mapEv.getPreferredName());
					          //System.out.println("   Matched Words: " + mapEv.getMatchedWords());
				              addEntity(mapEv,foundEntities,scoredDoc);
				            }
				          }
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		outputStream.println(query);
		System.out.println("Num unmapped entities: "+foundEntities.size());
		outputStream.println("Num unmapped entities: "+foundEntities.size());
		foundEntities = MedLink.mapEntities(snomedToWikiMappings, foundEntities, outputStream); //map snomed Entities to wiki entities
		outputStream.println("Num mapped entities: "+foundEntities.size());
		System.out.println("Num mapped-unfiltered entities: "+foundEntities.size());
		KBFilter kbfilter = new KBFilter(foundEntities);
		foundEntities=kbfilter.filterEntities(outputStream);
		System.out.println("Num filtered entities: "+foundEntities.size());
		//this.entitiesPerDoc=MedLinkEvaluator.calculateEntitiesPerDoc(foundEntities);
		//MedLinkEvaluator.setMentionProbablities(foundEntities, entitiesPerDoc); //calculate the mention probabilities for each entity per doc
		return foundEntities;
}

	public static String generateString(List<String> terms){
		StringBuilder sb = new StringBuilder();
		for(String s:terms){
			if(s.contains("%20")){ //links are not to be matched
				continue;
			}
			s = Normalizer.normalize(s, Normalizer.Form.NFD); //normalize all non ascii letters: öäü -> oau
			String resultString = s.replaceAll("[^\\x00-\\x7F]", ""); // replace all non-ascii chars with empty space as metamap doesnt like them
			//s = s.replaceAll("–","-"); //metamap doesnt like first hyphen
			sb.append(resultString+" ");
		}
		return sb.toString();
	}
	

	public void addEntity(Ev mapEv, ArrayList<Entity> entities, ScoredDocument docid){
		for(Entity e:entities){
			try {
				if(e.getName().equals(mapEv.getPreferredName())){
					e.addAppearance(docid);
					return;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		Entity toAdd = null;
		try {
			toAdd = new Entity(mapEv.getPreferredName().toLowerCase(),mapEv.getConceptId());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		toAdd.addAppearance(docid);
		entities.add(toAdd);
		return;
	}
	public List<ScoredDocument> getScoredDocuments(){
		return this.scoredDocs;
	}
	public String getQuery(){
		return this.query;
	}
	public HashMap<String,ArrayList<Entity>> getMapping(){
		return this.mapping;
	}
	public int getMaxTopics(){
		return this.topics.size();
	}
}