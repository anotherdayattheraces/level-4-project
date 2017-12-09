package generation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import dictionary.DictionaryHashMap;
import entityRetrieval.core.Entity;

public class DictionaryBuilder {
	String path;
	Boolean isFirstLine;
	
	public DictionaryBuilder(String pathName){
		this.path = pathName;
		this.isFirstLine = true;
	}
	public DictionaryBuilder(){
		this.path = "C:/Work/Project/samples/dbpediaDataDump/mappingbased_literals_en.ttl/mappingbased_literals_en.ttl";
		this.isFirstLine = true;
	}
	
	public DictionaryHashMap generateEntities(List<String> references) throws FileNotFoundException{
		FileInputStream inputStream = new FileInputStream(path);
		Scanner sc = new Scanner(inputStream, "UTF-8");
		DictionaryHashMap dhm = new DictionaryHashMap();
		while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        //example line: <http://dbpedia.org/resource/Tell_Hawsh> <http://dbpedia.org/ontology/utcOffset> "+2"
	        if(!this.isFirstLine && !line.startsWith("#")){ //final line is not valid data and begins with #
	        	//lines arrive in the format <subject)> <reference> "literal"
	        	String[] items = line.split(">");
	        	String subject = format(items[0]);
	        	String reference = format(items[1]);
	        	subject = subject.toLowerCase();
	        	subject = subject.replace('_', ' ');
	        	if (references.contains(reference)){
	        		if (!dhm.lookupString(subject)){
	        			Entity toBeAdded = new Entity(subject);
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
