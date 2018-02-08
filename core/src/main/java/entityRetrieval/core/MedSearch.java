package entityRetrieval.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lemurproject.galago.core.index.corpus.SnippetGenerator;
import org.lemurproject.galago.core.index.stats.IndexPartStatistics;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Document.DocumentComponents;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.SimpleQuery;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.core.tools.Search;
import org.lemurproject.galago.utility.Parameters;

import customEntityLinker.MedLink;
import evaluation.DocumentLinkReader;
import knowledgeBase.KBLinker;
import metamap.MetaMapEntityLinker;

public class MedSearch{

	private Parameters parameters;
	public SnippetGenerator generator;
    protected Retrieval retrieval;
    private Search search;
	

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
    	String linkType = p.get("linkType", "ML");
    	ArrayList<Entity> results;
    	if(linkType=="ML"){
    		MedLink medlink = new MedLink();
    		results = medlink.matchEntities(System.out);
    	}
    	else if(linkType=="LR"){
    		DocumentLinkReader documentlinkreader = new DocumentLinkReader(query);
    		results = documentlinkreader.getEntitiesFromLinks();
    	}
    	else if(linkType=="KB"){
    		KBLinker kblinker = new KBLinker();
    		results=kblinker.getEntitiesFromText();
    	}
    	else if(linkType=="MM"){
    		MetaMapEntityLinker metamapentitylinker = new MetaMapEntityLinker();
    		results=metamapentitylinker.generateEntities(System.out);
    	}
    	else{
    		return null;
    	}
    	return convertToSearchResult(results,query);
    }
    
    public Document getDocument(String identifier, DocumentComponents p) throws IOException {
        return retrieval.getDocument(identifier, p);
    }
    
    public String getSummary(Document document, Set<String> query) throws IOException {
        /*if (document.metadata.containsKey("description")) {
         String description = document.metadata.get("description");

         if (description.length() > 10) {
         return generator.highlight(description, query);
         }
         }*/

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
    	for(Entity entity:results){
    		SearchResultItem sri = new SearchResultItem();
    		sri.identifier=entity.getName();
    		sri.displayTitle=entity.getName();
    		sri.rank=entity.getRank();
    		sri.score=entity.getScore();
    		sri.url=entity.getId();
    		searchresult.items.add(sri);
   
    	}
    	searchresult.query=new Node();
    	return searchresult;
    }

}
