package evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TopicRetriever {
	private String path;
	
	public TopicRetriever(){
		this.path = "C:/Work/Project/samples/Pub_Med_Data_2017/extraTopics.txt";
	}
	public TopicRetriever(String path){
		this.path = path;
	}
	
	public ArrayList<Topic> retreiveTopics() throws FileNotFoundException{
		FileInputStream inputStream = new FileInputStream(path);
		Scanner sc = new Scanner(inputStream, "UTF-8");
		ArrayList<Topic> topics = new ArrayList<Topic>();
		String disease = null;
		ArrayList<String> genes = new ArrayList<String>();
		String demographic = null;
		ArrayList<String> other = new ArrayList<String>();
		ArrayList<String> pmids= new ArrayList<String>();
		while(sc.hasNext()){
			String line = sc.nextLine();
			
			if(line.startsWith("Disease")){
				disease=line.substring(line.indexOf(" ")+1);
			}
			if(line.startsWith("Gene")){
				line=line.substring(line.indexOf(" "));
				String[] geneStrings = line.split(",");
				for(String g:geneStrings){
					genes.add(g.substring(1));
				}
			}
			if(line.startsWith("Demographic")){
				demographic=line.substring(line.indexOf(" ")+1);
			}
			if(line.startsWith("Other")){
				if(line.contains("None")){
					continue;
				}
				line=line.substring(line.indexOf(" "));
				String[] otherStrings = line.split(",");
				for(String o:otherStrings){
					other.add(o.substring(1));
				}
			}
			if(line.startsWith("PMID(s)")){
				line=line.substring(line.indexOf(" "));
				String[] pmidStrings = line.split(",");
				for(String p:pmidStrings){
					pmids.add(p.substring(1));
				}	
			}
			if(line.startsWith("NCT ID(s)")){
				if(other.size()==0){
					System.out.println(disease);
					topics.add(new Topic(disease,genes,demographic,pmids));
				}
				else{
					System.out.println(disease);
					topics.add(new Topic(disease,genes,demographic,other,pmids));
				}
				disease = null;
				genes = new ArrayList<String>();
				demographic = null;
				other = new ArrayList<String>();
				pmids= new ArrayList<String>();
			}
		}
		sc.close();
		return topics;
		
		
	}

}
