package entityRetrieval.core;


import java.util.List;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.utility.Parameters;


public class GalagoOrchestrator {
	private String path;
	
	public GalagoOrchestrator(){
		this.path = "C:/Work/Project/samples/treccar/paragraphcorpus";
	}
	public List<ScoredDocument> getDocuments(String query,int docsNeeded){
		Retrieval retrieval = null;
		try {
			retrieval = RetrievalFactory.instance(path);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		Parameters p = Parameters.create();
		p.set("startAt", 0);
	    p.set("resultCount", docsNeeded);
	    p.set("requested", docsNeeded);
	    p.set("index", path);
		Node root = StructuredQuery.parse(query);
		Node transformed = null;
		try {
			transformed = retrieval.transformQuery(root, p);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<ScoredDocument> results = null;
		try {
			results = retrieval.executeQuery(transformed, p).scoredDocuments;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//System.out.println(transformed.toSimplePrettyString());
		return results;

		
		
		
	    
		
		
	}
}
