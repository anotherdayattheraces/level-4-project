package entityRetrieval.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lemurproject.galago.core.index.corpus.SnippetGenerator;
import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.index.stats.IndexPartStatistics;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Document.DocumentComponents;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.SimpleQuery;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.core.tools.Search;
import org.lemurproject.galago.utility.Parameters;

import customEntityLinker.MedLink;
import evaluation.DocumentLinkReader;
import evaluation.MedLinkEvaluator;
import knowledgeBase.KBLinker;
import metamap.MetaMapEntityLinker;

public class MedSearch{

	private Parameters parameters;
	public SnippetGenerator generator;
    protected Retrieval retrieval;
    private Search search;
    private Node root;
	

	public MedSearch(Parameters params) throws Exception {
        //this.store = getDocumentStore(params.getAsList("corpus"));
        this.retrieval = RetrievalFactory.create(params);
        generator = new SnippetGenerator();
        this.search=new Search(params);
    }
	
	public static class SearchResult {

        public String queryAsString;
        public Node query;
        public Node transformedQuery;
        public List<SearchResultItem> items;

        public SearchResult() {
            items = new LinkedList<SearchResultItem>();
        }
    }

    public static class SearchResultItem {

        public int rank;
        public String identifier;
        public String displayTitle;
        public String url;
        public Map<String, String> metadata;
        public String summary;
        public double score;
        public Document document;
    }
    public Retrieval getRetrieval() {
        return retrieval;
    }
    public SearchResult runQuery(String query, Parameters p){
    	String linkType = p.get("linkType", "LR");
    	ArrayList<Entity> results;
    	List<ScoredDocument> scoredDocs = null;
    	if(linkType=="ML"){
    		MedLink medlink = new MedLink();
    		results = medlink.matchEntities(System.out);
    		scoredDocs=medlink.getScoredDocs();
    		this.root=medlink.root;
    	}
    	else if(linkType=="LR"){
    		DocumentLinkReader documentlinkreader = new DocumentLinkReader(query);
    		results = documentlinkreader.getEntitiesFromLinks();
    		scoredDocs=documentlinkreader.getScoredDocuments();
    		this.root=documentlinkreader.root;
    	}
    	else if(linkType=="KB"){
    		KBLinker kblinker = new KBLinker();
    		results=kblinker.getEntitiesFromText();
    		scoredDocs=kblinker.getScoredDocuments();
    		this.root=kblinker.root;
    	}
    	else if(linkType=="MM"){
    		MetaMapEntityLinker metamapentitylinker = new MetaMapEntityLinker();
    		results=metamapentitylinker.generateEntities(System.out);
    		scoredDocs=metamapentitylinker.getScoredDocuments();
    		this.root=metamapentitylinker.root;
    	}
    	else{
    		return null;
    	} 
    	calculateScores(results,scoredDocs);
    	return convertToSearchResult(results,query);
    }
    
    public Document getDocument(String identifier, DocumentComponents p) throws IOException {
        return retrieval.getDocument(identifier, p);
    }
    
    public String getSummary(Document document, Set<String> query) throws IOException {
    	System.out.println(query.size());
        if (document.metadata.containsKey("description")) {
        	System.out.println("has description");
        	String description = document.metadata.get("description");

         if (description.length() > 10) {
        	 return generator.highlight(description, query);
         	}
         }

        return generator.getSnippet(document.text, query);
    }
    public long xCount(String nodeString) throws Exception {
        return this.retrieval.getNodeStatistics(nodeString).nodeFrequency;
    }
    public long docCount(String nodeString) throws Exception {
        return this.retrieval.getNodeStatistics(nodeString).nodeDocumentCount;
    }
    public IndexPartStatistics getIndexPartStatistics(String part) throws IOException {
        return retrieval.getIndexPartStatistics(part);
    }

    public Parameters getAvailiableParts() throws IOException {
        return retrieval.getAvailableParts();
    }
    


    
    public static Node parseQuery(String query, Parameters parameters) throws IOException {
        String queryType = parameters.get("queryType", "complex");

        if (queryType.equals("simple")) {
            return SimpleQuery.parseTree(query);
        }

        return StructuredQuery.parse(query);
    }
    
    public SearchResult convertToSearchResult(ArrayList<Entity> results, String query){
    	SearchResult searchresult = new SearchResult();
    	DiskIndex index = null;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
        Set<String> queryTerms = StructuredQuery.findQueryTerms(root);
        if(queryTerms.size()==0){
        	for(String s:query.split(" ")){
        		queryTerms.add(s);
        	}
        }
		try {
			index = new DiskIndex("C:/Work/Project/samples/Unprocessed_Index");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	for(Entity entity:results){
    		SearchResultItem sri = new SearchResultItem();
    		sri.identifier=entity.getName();
    		sri.displayTitle=entity.getName();
    		sri.rank=entity.getRank();
    		sri.score=entity.getScore();
    		sri.url=entity.getId();
    		sri.summary=getSummary(entity,index,dc,queryTerms);
    		System.out.println(sri.summary);
    		searchresult.items.add(sri);
   
    	}
    	searchresult.query=new Node();
    	return searchresult;
    }
    public void calculateScores(ArrayList<Entity> results, List<ScoredDocument> scoredDocs){
    	scoredDocs=MedLinkEvaluator.calculateEntitiesPerDoc(results, scoredDocs);
    	Map<ScoredDocument, Double> finalDocScores = RelevanceModel1.logstoposteriors(scoredDocs);
    	MedLinkEvaluator.setMentionProbablities(results, scoredDocs); //calculate the mention probabilities for each entity per doc
		MedLinkEvaluator.setScores(results, finalDocScores);//set scores for all entities, using entity metadata
    }
    public String getSummary(Entity entity, DiskIndex index, DocumentComponents dc, Set<String> queryTerms){
 		Document doc = null;
		try {
			doc=index.getDocument(entity.getName(), dc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			return getSummary(doc,queryTerms);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
    }
}

