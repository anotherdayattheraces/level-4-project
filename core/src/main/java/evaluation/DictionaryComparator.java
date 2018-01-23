package evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.iterator.DataIterator;

import customEntityLinker.MedLink;
import dictionary.DictionaryHashMap;
import dictionary.SnomedDictionaryInitializer;
import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import entityRetrieval.core.ResultSet;
import metamap.MetaMapEntityLinker;

public class DictionaryComparator {
	private ArrayList<Long> documents;
	private ArrayList<Entity> customDictionaryResults;
	private DictionaryHashMap metamapResults;
	private String path;
	
	public DictionaryComparator() throws IOException{
		this.path = "C:/Work/Project/samples/treccar/paragraphcorpus";
		ArrayList<Long> documents =  new ArrayList<Long>();
		DiskIndex index = null;
		try {
			index = new DiskIndex( path );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		DataIterator i = index.getNamesIterator();
		int numDocs = (int) i.totalEntries(); //get the total number of documents
		Random r = new Random();
		long docid = r.nextInt(numDocs);
		docid = 41540;
		documents.add(docid);
    	System.out.println("Using document number: "+docid);
		this.documents = documents;
		SnomedDictionaryInitializer init = new SnomedDictionaryInitializer();
		MedLink snomedCounter = null;
		//try {
		//	snomedCounter = new MedLink(documents, init.initialize());
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		this.customDictionaryResults = snomedCounter.matchEntities();
		MetaMapEntityLinker mmlinker = new MetaMapEntityLinker(documents);
		this.metamapResults = mmlinker.linkArticles();
		
	}
	
	public void compareResults(){
		Retrieval index=null;
		try {
			index = RetrievalFactory.instance( path );
		} catch (Exception e1) {
			System.err.println("index not found");
			e1.printStackTrace();
		}
		Document doc=null;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, false, true );
		try {
			doc = index.getDocument( index.getDocumentName( documents.get(0) ), dc );
		} catch (IOException e) {
			e.printStackTrace();
		}
		String docText = MetaMapEntityLinker.generateString(doc.terms);
		System.out.println("Document text:");
		System.out.println(docText);
		System.out.println("Metamap found "+metamapResults.getSize()+" entities:");
		for(Entity entity:metamapResults.toArray()){
			System.out.println(entity.getName());
		}
		System.out.println("Alternative dictionary found "+customDictionaryResults.size()+" entities:");
		for(Entity e:customDictionaryResults){
			System.out.println(e.getName());
		
	}
	
	}

}
