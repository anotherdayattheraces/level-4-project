package pseudoRelavanceFeedback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dictionary.DictionaryHashMap;
import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import metamap.MetaMapEntityLinker;

public class QueryEnhancer {
	private ArrayList<Long> documents;
	
	public QueryEnhancer(ArrayList<Long> documents){
		this.documents=documents;
	}

	public ArrayList<Entity> enhanceQuery(){
		MetaMapEntityLinker linker = new MetaMapEntityLinker();
		ArrayList<Entity> mmentities = linker.generateEntities();
		ArrayList<Entity> entityList = new ArrayList<Entity>();
		for(Entity e:mmentities){
			e.calculateIDF(50);
			e.calculateTFIDF();
			System.out.println("Entity name: "+e.getName()+" entity ID: "+e.getId()+" tfidf: "+e.getTFIDF());
			}
		Collections.sort(entityList, new Comparator<Entity>() {
			public int compare(Entity e1, Entity e2) {
				return Double.compare(e1.getTFIDF(),e2.getTFIDF());
			}
		});
		for(Entity e:entityList){
			System.out.println("Entity name: "+e.getName()+" entity ID: "+e.getId()+" tfidf: "+e.getTFIDF());
			}
		return null;
	}
	
}
