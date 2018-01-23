package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.RelevanceModel1;

import entityRetrieval.core.DocumentIdentifier;
import entityRetrieval.core.Entity;
import entityRetrieval.core.GalagoOrchestrator;
import entityRetrieval.core.Pair;
import metamap.MetaMapEntityLinker;

public class MetaMapEvaluator {
	private ArrayList<Entity> returnedEntities;
	private HashMap<String,ArrayList<Entity>> mapping;
	private String query;
	private List<ScoredDocument> scoredDocs;
	private Map<ScoredDocument, Double> finalDocScores;

	
	public MetaMapEvaluator(){
		MetaMapEntityLinker linker = new MetaMapEntityLinker();
		this.returnedEntities=linker.linkArticles();
		this.mapping = linker.getMapping();
		this.query=linker.getQuery();
		this.scoredDocs=linker.getScoredDocuments();
		this.finalDocScores=RelevanceModel1.logstoposteriors(scoredDocs);
		System.out.println("Chosen query: "+query);
	} 
	
	public void computeStatistics(){
		ArrayList<Entity> relevantEntities = mapping.get(query); // create data structure for mapping
		MedLinkEvaluator.setScores(returnedEntities, finalDocScores);//set scores for all entities, using entity metadata
		Collections.sort(returnedEntities, MedLinkEvaluator.score);//sort by score
		MedLinkEvaluator.setAllRanks(returnedEntities);
		MedLinkEvaluator.calculatePrecision(returnedEntities, relevantEntities);
		for(Entity e:relevantEntities){
			System.out.println(e.getName());
		}
		double averagePrecision=0;
		for(Entity entity:returnedEntities){
			averagePrecision+=entity.getPrecision();
			System.out.println(entity.getName()+" Rank: "+entity.getRank()+" Score: "+entity.getScore()+ " Precision: "+entity.getPrecision());
		}
		averagePrecision=averagePrecision/(double)returnedEntities.size();
		System.out.println(averagePrecision);
	}
		
	

	
}
