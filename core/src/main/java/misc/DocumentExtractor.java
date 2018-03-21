package misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.ScoredDocument;

import entityRetrieval.core.GalagoOrchestrator;
import evaluation.TopicToEntityMapper;

public class DocumentExtractor {
	private ArrayList<String> topics;
	HashMap<String,List<ScoredDocument>> documents;
	
	public DocumentExtractor(){
		this.topics=TopicToEntityMapper.readTopics("C:/Work/Project/samples/prototype4/level-4-project/core/topics.txt");
		documents = new HashMap<String,List<ScoredDocument>>();
		initializeTopics();
		initializeDocuments();
	}
	
	public void initializeTopics(){
		for(int i=0;i<50;i++){
			documents.put(topics.get(i), new ArrayList<ScoredDocument>());
		}
	}
	public void initializeDocuments(){
		GalagoOrchestrator go = new GalagoOrchestrator();
		for(String topic:topics){
			List<ScoredDocument> docs = go.getDocuments(topic, 4);
			documents.put(topic, docs);
		}
	}
	public void saveDocs() throws IOException{
		DiskIndex index= new DiskIndex("C:/Work/Project/samples/Unprocessed_Index");
		File fout = new File("docToSearch.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		Set<String> keys = documents.keySet();
		Iterator<String> i = keys.iterator();
		while(i.hasNext()){
			String topic = i.next();
			String d1 = index.getName(documents.get(topic).get(0).document);
			String d2 = index.getName(documents.get(topic).get(1).document);
			String d3 = index.getName(documents.get(topic).get(2).document);
			String d4 = index.getName(documents.get(topic).get(3).document);
			bw.write(topic+": "+documents.get(topic).get(0).documentName);
			bw.newLine();
			bw.write(topic+": "+documents.get(topic).get(1).documentName);
			bw.newLine();
			bw.write(topic+": "+documents.get(topic).get(2).documentName);
			bw.newLine();
			bw.write(topic+": "+documents.get(topic).get(3).documentName);
			bw.newLine();
		}
		bw.close();
		index.close();
	}

}
