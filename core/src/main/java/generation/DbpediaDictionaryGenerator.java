package generation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import dictionary.DictionaryHashMap;
import entityRetrieval.core.Entity;

public class DbpediaDictionaryGenerator extends DictionaryGenerator {
	private String path;
	private Boolean isFirstLine;
	private List<String> references;
	
	public DbpediaDictionaryGenerator(String pathName, List<String> references){
		this.path = pathName;
		this.isFirstLine = true;
		this.references = references;
	}
	public DbpediaDictionaryGenerator(List<String> references){
		this.path = "C:/Work/Project/samples/dbpediaDataDump/mappingbased_literals_en.ttl/mappingbased_literals_en.ttl";
		this.isFirstLine = true;
		this.references = references;
	}
	
	public DictionaryHashMap generateEntities() throws FileNotFoundException{
		FileInputStream inputStream = new FileInputStream(path);
		Scanner sc = new Scanner(inputStream, "UTF-8");
		DictionaryHashMap dhm = new DictionaryHashMap();
		while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        //example line: <http://dbpedia.org/resource/Tell_Hawsh> <http://dbpedia.org/ontology/utcOffset> "+2"
	        if(!this.isFirstLine && !line.startsWith("#")){ //final line is not valid data and begins with #
	        	//lines arrive in the format <subject)> <reference> "literal"
	        	String[] items = line.split(">");
	        	String url = items[0].substring(1);
	        	System.out.println(url);
	        	String subject = format(items[0]);
	        	String reference = format(items[1]);
	        	subject = subject.toLowerCase();
	        	subject = subject.replace('_', ' ');
	        	if (references.contains(reference)){
	        		if (!dhm.lookupString(subject)){
	        			Entity toBeAdded = new Entity(subject,url);
	        			dhm.addEntity(toBeAdded);;}
	        				}
	        			}
	        else{
	        	isFirstLine=false;
	        	}
	        }
		sc.close();
		return dhm;
	}
	
	public String format(String s){
		int start = s.lastIndexOf('/')+1;
		return s.substring(start).trim();

	}

}
