package entityRetrieval.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    private HashMap<String,ArrayList<Entity>> categoryToEntities;
    public ArrayList<Pair<String,Double>> topCategories;
    public HashMap<String,ArrayList<Entity>> finalEntityCategories;
	

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
    	HashMap<Entity,ArrayList<String>> categories = null;
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
    		categories = new HashMap<Entity,ArrayList<String>>();
    		categories.putAll(documentlinkreader.getFinalEntityCategories());
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
    	for(Entity entity:results){
    		System.out.println(entity.getName()+" "+entity.getScore());
    	}
    	topCategories = getTopCategories(categories);
    	rescoreCategories(topCategories,results);
    	topCategories.removeAll(enforceCategoryBlacklist(topCategories));
    	finalEntityCategories = getFinalEntityCategories(topCategories,results);
    	topCategories.add(0, new Pair<String,Double>("Overview",Double.MAX_VALUE));
    	return convertToSearchResult(results,query);
    }
    
    public Document getDocument(String identifier, DocumentComponents p) throws IOException {
        return retrieval.getDocument(identifier, p);
    }
    
    public String getSummary(Document document, Set<String> query) throws IOException {
        ///if (document.metadata.containsKey("description")) {
        //	String description = document.metadata.get("description");
    //}

    //     if (description.length() > 10) {
      //  	 return generator.highlight(description, query);
        // 	}
         //}
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
    	int rankModifier=0;
    	for(Entity entity:results){
    		SearchResultItem sri = new SearchResultItem();
    		sri.identifier=entity.getName();
    		sri.displayTitle=entity.getName();
    		sri.rank=entity.getRank()-rankModifier;
    		sri.score=entity.getScore();
    		sri.url=entity.getId();
    		sri.summary=getSummary(entity,index,dc,queryTerms);
    		if(sri.summary.equals("NO DOC")){
    			rankModifier++;
    			continue;
    		}
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
    	System.out.println(entity.getName());
 		Document doc = null;
		try {
			doc=index.getDocument(entity.getName(), dc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(doc==null){
			return "NO DOC";
		}
		
		try {
			return getSummary(doc,queryTerms);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
    }
    public ArrayList<Pair<String,Double>> getTopCategories(HashMap<Entity,ArrayList<String>> categories){
    	ArrayList<Pair<String,Double>> categoryAggregate = new ArrayList<Pair<String,Double>>();
    	categoryToEntities=new HashMap<String,ArrayList<Entity>>();
    	for(Entity key:categories.keySet()){
    		for(String category:categories.get(key)){
    			addCategory(categoryAggregate,category,key);
    		}
    	}
    	Collections.sort(categoryAggregate, score );
		return categoryAggregate;
    }
    public void addCategory(ArrayList<Pair<String,Double>> cats,String cat,Entity entity){
    	for(Pair<String,Double> c:cats){
    		if(c.getL().equals(cat)){
    			c.setR(c.getR()+1);
    			if(!categoryToEntities.containsKey(c.getL())){
    				categoryToEntities.put(c.getL(), new ArrayList<Entity>());
    			}
    			if(!categoryToEntities.get(cat).contains(entity)){
    	    		categoryToEntities.get(cat).add(entity);
    	    	}
    			return;
    		}
    	}
    	if(!categoryToEntities.containsKey(cat)){
			categoryToEntities.put(cat, new ArrayList<Entity>());
		}
    	if(!categoryToEntities.get(cat).contains(entity)){
    		categoryToEntities.get(cat).add(entity);
    	}
    	cats.add(new Pair<String,Double>(cat,1d));
    	
    }
    public static void sortByScore(ArrayList<Pair<String,Double>> unorderedList){ // method for sorting entities in a list by their score
	}
	public static Comparator<Pair<String,Double>> score = new Comparator<Pair<String,Double>>() {


		public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
			return Double.compare(o2.getR(), o1.getR());
		}};
		
	public void rescoreCategories(ArrayList<Pair<String,Double>> topCategories, ArrayList<Entity> results){
		int maxScore = results.size();
		for(Pair<String,Double> pair:topCategories){ //for each top appearing category
			for(Entity entity:categoryToEntities.get(pair.getL())){ //for each entity that is mapped to by each category
				pair.setR(pair.getR()+(maxScore-entity.getRank()));
			}
		}
		Collections.sort(topCategories,score);
	}
	public Entity getEntity(ArrayList<Entity> results,Entity entity){
		for(Entity e:results){
			if(e.equals(entity)){
				return e;
			}
		}
		return null;
	}
	public HashMap<String,ArrayList<Entity>> getFinalEntityCategories(ArrayList<Pair<String,Double>> topCategories, ArrayList<Entity> results){
		HashMap<String,ArrayList<Entity>> finalCategories = initCategories(results);
		for(Pair<String,Double> pair:topCategories){
			ArrayList<Entity> toAdd = sortCategoryMapping(pair.getL(),finalCategories);
			finalCategories.put(pair.getL(), toAdd);
		}
		finalCategories.put("Additional information", new ArrayList<Entity>());
		for(Entity entity:results){
			if(!hasEntity(finalCategories,entity)){
				finalCategories.get("Additional information").add(entity);
			}
		}
		return finalCategories;

	}
	public HashMap<String,ArrayList<Entity>> initCategories(ArrayList<Entity> results){
		HashMap<String,ArrayList<Entity>> finalCategories = new HashMap<String,ArrayList<Entity>>();
		finalCategories.put("Overview", new ArrayList<Entity>());
		for(int i=0;i<3&&i<results.size();i++){
			finalCategories.get("Overview").add(results.get(i));
		}
		return finalCategories;
		
	}
	public boolean hasEntity(HashMap<String,ArrayList<Entity>> categories,Entity entity){
		for(String key:categories.keySet()){
			for(Entity e:categories.get(key)){
				if(e.equals(entity)){
					return true;
				}
			}
		}
		return false;
	}
	public ArrayList<Entity> sortCategoryMapping(String category, HashMap<String,ArrayList<Entity>> finalCategories){
		ArrayList<Entity> mapping = categoryToEntities.get(category);
		Collections.sort(mapping, MedLinkEvaluator.score);
		ArrayList<Entity> toAdd = new ArrayList<Entity>();		
		for(int i=0;i<mapping.size();i++){
			if(!hasEntity(finalCategories,mapping.get(i))){
				toAdd.add(mapping.get(i));
			}
			if(toAdd.size()>=3){
				break;
			}
		}
		return toAdd;
	}
	
	public ArrayList<Pair<String,Double>> enforceCategoryBlacklist(ArrayList<Pair<String,Double>> categories){
		ArrayList<String> blacklist = new ArrayList<String>();
		ArrayList<Pair<String,Double>> toRemove = new ArrayList<Pair<String,Double>>();
		blacklist.add("Articles containing video clips");
		blacklist.add("Animal anatomy");
		blacklist.add("RTT");
		blacklist.add("RTT(full)");

		for(Pair<String,Double> category:categories){
			for(String black:blacklist){
				if(black.equals(category.getL().replaceAll("%20", " "))){
					toRemove.add(category);
				}
			}
		}
		return toRemove;
	}
	
}

