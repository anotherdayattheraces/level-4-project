package level_4_project.entity_linker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DbpediaReferenceParser {
	String path;
	Boolean isFirstLine;
	public DbpediaReferenceParser(String pathName){
		this.path = pathName;
		this.isFirstLine = true;
	}
	public DbpediaReferenceParser(){
		this.path = "C:/Work/Project/samples/dbpediaDataDump/mappingbased_literals_en.ttl/mappingbased_literals_en.ttl";
		this.isFirstLine = true;
	}
	
	public ArrayList<String> generateEntities(List<String> references) throws FileNotFoundException{
		FileInputStream inputStream = new FileInputStream(path);
		Scanner sc = new Scanner(inputStream, "UTF-8");
		ArrayList<String> entities = new ArrayList();
		while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        //example line: <http://dbpedia.org/resource/Tell_Hawsh> <http://dbpedia.org/ontology/utcOffset> "+2"
	        if(!this.isFirstLine && !line.startsWith("#")){ //final line is not valid data and begins with #
	        	//lines arrive in the format <subject)> <reference> "literal"
	        	String[] items = line.split(">");
	        	String subject = format(items[0]);
	        	String reference = format(items[1]);
	        	if (references.contains(reference)){
	        		subject = subject.replace('_', ' ');
	        		if (!entities.contains(subject)){
	        			entities.add(subject);}
	        				}
	        			}
	        else{
	        	isFirstLine=false;
	        	
	        	}
	        }
		
		
		return entities;
	}
	public String format(String s){
		int start = s.lastIndexOf('/')+1;
		return s.substring(start).trim();

	}

}
