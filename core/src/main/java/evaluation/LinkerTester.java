package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;

import customEntityLinker.MedLink;
import dictionary.DictionaryHashMap;
import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import knowledgeBase.KBFilter;
import knowledgeBase.KBLinker;
import metamap.MetaMapEntityLinker;

public class LinkerTester {
	String qrelpath;
	
	public LinkerTester(){
		this.qrelpath="FinalParagraphJudgementsANSI.txt";
	}
	
	public void generateQrels() throws IOException{
		FileReader file = new FileReader(qrelpath);
		BufferedReader br = new BufferedReader(file);
		String line;
		String currentTopic=null;
		HashMap<String,ArrayList<String>> qrels = new HashMap<String,ArrayList<String>>();
		
		DiskIndex index=null;
		try {
			index = new DiskIndex("C:/Work/Project/samples/Unprocessed_Index");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
		Document doc=null;
		while((line=br.readLine())!=null){
			line=line.trim();
			if(line.startsWith("Topic: ")){
				currentTopic=line.substring("Topic: ".length());
				qrels.put(currentTopic, new ArrayList<String>());
			}
			else{
				doc=index.getDocument(line, dc);
				if(doc!=null){
					if(doc.text.contains("#REDIRECT")){
						line = KBFilter.searchRedirect(index, dc, doc.text);
					}
					if(line!=null){
						if(!qrels.get(currentTopic).contains(line)&&!line.equals(currentTopic)){
							qrels.get(currentTopic).add(line);
						}
					}
				}
			}
		}
		for(String key:qrels.keySet()){
			if(qrels.get(key).size()==0){
				System.out.println("No qrels for topic: "+key);
			}
			if(qrels.get(key).size()==1){
				if(qrels.get(key).get(0).equals(key)){
					System.out.println("SINGLE TOPIC-QREL " +key);
				}
			}
		}
		br.close();
		File fout = new File("ParagraphsQrels"+".txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		for(String key:qrels.keySet()){
			for(String entity:qrels.get(key)){
				bw.write(key.replaceAll(" ", "%20")+" 0 "+entity.replaceAll(" ", "%20")+" 1");
				bw.newLine();
			}
		}
		bw.close();
		
	}
	public void generateQrels2() throws IOException{
		FileReader file = new FileReader(qrelpath);
		BufferedReader br = new BufferedReader(file);
		String line;
		String currentTopic=null;
		HashMap<String,ArrayList<String>> qrels = new HashMap<String,ArrayList<String>>();
		
		DiskIndex index=null;
		try {
			index = new DiskIndex("C:/Work/Project/samples/Unprocessed_Index");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
		Document doc=null;
		while((line=br.readLine())!=null){
			line=line.trim();
			if(line.startsWith("Topic: ")){
				currentTopic=line.substring("Topic: ".length());
				qrels.put(currentTopic, new ArrayList<String>());
			}
			else{
				if(!qrels.get(currentTopic).contains(line)&&!line.equals(currentTopic)){
					qrels.get(currentTopic).add(line);
				}
			}
		}
		for(String key:qrels.keySet()){
			if(qrels.get(key).size()==0){
				System.out.println("No qrels for topic: "+key);
			}
			if(qrels.get(key).size()==1){
				if(qrels.get(key).get(0).equals(key)){
					System.out.println("SINGLE TOPIC-QREL " +key);
				}
			}
		}
		br.close();
		File fout = new File("FinalParagraphsQrels"+".txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		for(String key:qrels.keySet()){
			for(String entity:qrels.get(key)){
				bw.write(key.replaceAll(" ", "%20")+" 0 "+entity.replaceAll(" ", "%20")+" 1");
				bw.newLine();
			}
		}
		bw.close();
		
	}
	public void reformDoc() throws IOException{
		File fout = new File("FinalParagraphsPart2"+".txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		
		
		
		FileReader file = new FileReader("Details2.txt");
		BufferedReader br = new BufferedReader(file);
		String line;
		boolean inText=false;
		boolean inLink = false;
		boolean inPage=false;
		boolean topicNext =false;
		while((line=br.readLine())!=null){
			if(line.equals("---------------------------------------------------------------------------------")){
				bw.write("----------------------------------");
				inText=true;
			}
			if(line.equals("--------------")){
				inText=false;
				topicNext=true;
			}
			if(inText){
				String[] split = line.split(" ");
				for(String s:split){
					if(s.equals("<link")){
						inLink=true;
					}
					
					if(s.startsWith("<page>")){
						inPage=true;
					}
					if(!inLink&&!inPage){
						bw.write(s+" ");
					}
					
					if(s.endsWith("</link>" )){
						inLink=false;
					}
					if(s.endsWith("</page>")){
						inPage=false;
					}
				}
				bw.newLine();
			}
		}
	}
	public void getTopicNums() throws IOException{
		FileReader file = new FileReader("FinalParagraphJudgementsANSI.txt");
		BufferedReader br = new BufferedReader(file);
		String line;
		int topics=0;
		while((line=br.readLine())!=null){
			if(line.startsWith("Topic:")){
				topics++;
			}
		}
		System.out.println(topics);
	}
	public void test(String linker) throws IOException{
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<Pair<Integer,Integer>> pairs = new ArrayList<Pair<Integer,Integer>>();
		if(linker.equals("DLR")){
			for(int i=0;i<100;i++){
				DocumentLinkReader dlr = new DocumentLinkReader(i,"TopicsExpanded.txt");
				Pair<Integer,Integer> result = testLinker(dlr.getEntitiesFromLinks(),dlr.getQuery());
				results.add(dlr.getQuery()+" "+result.getL()+" of "+result.getR());
				pairs.add(result);
			}
	}
		if(linker.equals("MM")){
			for(int i=0;i<100;i++){
				MetaMapEntityLinker mm = new MetaMapEntityLinker(i,"TopicsExpanded.txt");
				Pair<Integer,Integer> result = testLinker(mm.generateEntities(null),mm.getQuery());
				results.add(mm.getQuery()+" "+result.getL()+" of "+result.getR());
				pairs.add(result);

			}
	}
		if(linker.equals("ML")){
			DictionaryHashMap dictionary=null;
			for(int i=1;i<100;i++){
				if(i==0){
					MedLink ml = new MedLink(i,"TopicsExpanded.txt",dictionary);
					dictionary = new DictionaryHashMap(ml.getDictionary().getDictionary());
					Pair<Integer,Integer> result = testLinker(ml.matchEntities(null),ml.getQuery());
					results.add(ml.getQuery()+" "+result.getL()+" of "+result.getR());
					pairs.add(result);

				}
				else{
					MedLink ml = new MedLink(i,"TopicsExpanded.txt",dictionary);
					Pair<Integer,Integer> result = testLinker(ml.matchEntities(null),ml.getQuery());
					results.add(ml.getQuery()+" "+result.getL()+" of "+result.getR());
					pairs.add(result);

				}
			}
	}
		if(linker.equals("KB")){
			for(int i=0;i<100;i++){
				KBLinker kb = new KBLinker(i,"TopicsExpanded.txt");
				Pair<Integer,Integer> result = testLinker(kb.getEntitiesFromText(),kb.getQuery());
				results.add(kb.getQuery());
				pairs.add(result);

			}
	}
		
		
		
		
		for(String s:results){
			System.out.println(s);
		}
		double correct=0d;
		double qrels=0d;
		for(Pair<Integer,Integer> pair:pairs){
			System.out.println((double)pair.getL()/(double)(pair.getR()));
			correct+=pair.getL();
			qrels+=pair.getR();
		}
		System.out.println(correct);
		System.out.println(qrels);

	}
	
	public Pair<Integer,Integer> testLinker(ArrayList<Entity> entities, String topic) throws IOException{
		FileReader file = new FileReader("FinalParagraphsQrels.txt");
		BufferedReader br = new BufferedReader(file);
		String line;
		boolean foundTopic =false;
		Pair<Integer,Integer> results = new Pair<Integer,Integer>(0,0);
		while((line=br.readLine())!=null){
			String[] split = line.split(" ");
			if(split[0].equals(topic.replaceAll(" ", "%20"))){
				foundTopic=true;
				results.setR(results.getR()+1);
				for(Entity entity:entities){
					if(entity.getName().replaceAll(" ", "%20").equals(split[2])){
						results.setL(results.getL()+1);
						break;
					}
				}
			}
			else{
				if(foundTopic){
					return results;
				}
			}
		}
		br.close();
		return results;
	}
	

}
