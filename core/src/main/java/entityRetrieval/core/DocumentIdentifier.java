package entityRetrieval.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.lemurproject.galago.core.index.IndexPartReader;
import org.lemurproject.galago.core.index.KeyIterator;
import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.retrieval.iterator.CountIterator;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.utility.ByteUtil;

public class DocumentIdentifier {
	private String path;
	
	public DocumentIdentifier(String path){
		this.path = path;
	}
	public DocumentIdentifier(){
		this.path = "C:/Work/Project/samples/Pub_Med_Index";
	}
	
	
	public ArrayList<Long> getRelevantDocuments(String term) throws IOException{
	
		ArrayList<Long> releventDocuments = new ArrayList<Long>();
		// Let's just retrieve the posting list for the term "reformulation" in the "text" field
		String field = "krovetz";
		
		
		// by default, the posting list for a field (without using stemming) is stored as a file with the name field.{fieldname}
		File pathPosting = new File( new File( path ), "postings." + field );
	
		DiskIndex index = new DiskIndex( path );
		IndexPartReader posting = DiskIndex.openIndexPart( pathPosting.getAbsolutePath() );
	
		//System.out.printf( "%-10s%-15s%-10s\n", "DOCID", "DOCNO", "FREQ" );
	
		KeyIterator vocabulary = posting.getIterator();
		// try to locate the term in the vocabulary
		if ( vocabulary.skipToKey( ByteUtil.fromString( term ) ) && term.equals( vocabulary.getKeyString() ) ) {
		    // get an iterator for the term's posting list
		    CountIterator iterator = (CountIterator) vocabulary.getValueIterator();
		    ScoringContext sc = new ScoringContext();
		    
		    // Get the current entry's document id.
		    // Note that you need to assign the value of sc.document,
		    // otherwise count(sc) and others will not work correctly.
		    while ( !iterator.isDone() ) {
			    sc.document = iterator.currentCandidate();
			    String docno = index.getName( sc.document ); // get the docno (external ID) of the current document
			    
			    if(!releventDocuments.contains(docno)){
			    	releventDocuments.add(sc.document);}
			    //System.out.printf( "%-10s%-15s%-10s\n", sc.document, docno, freq );
			    iterator.movePast( iterator.currentCandidate() ); // jump to the entry right after the current one

		    }
		}
		
		posting.close();
		index.close();
		return releventDocuments;
		}

}
