package knowledgeBase;

import java.io.IOException;

import org.lemurproject.galago.core.index.disk.DiskIndex;

public class KBSearcher {
	private String path;
	private DiskIndex index;
	
	public KBSearcher(){
		this.path="C:/Work/Project/samples/Unprocessed_Index";
		try {
			this.index= new DiskIndex(path);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	public DiskIndex getIndex(){
		return this.index;
	}
	
	public Boolean lookupTerm(String term){
		Long id = null;
		try {
			id = index.getIdentifier(term);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(id!=-1){

			return true;
		}
		return false;
	}
	
	

}
