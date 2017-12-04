package level_4_project.entity_linker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
	
	public ArrayList<String> generateEntities() throws FileNotFoundException{
		FileInputStream inputStream = new FileInputStream(path);
		Scanner sc = new Scanner(inputStream, "UTF-8");
		ArrayList<String> entities = new ArrayList();
		while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        //example line: <http://dbpedia.org/resource/Tell_Hawsh> <http://dbpedia.org/ontology/utcOffset> "+2"
	        if(!this.isFirstLine && !line.startsWith("#")){ //final line is not valid data and begins with #
	        	int endOfSubject = line.indexOf('>'); //lines arrive in the format <subject)> <reference> "literal"
	        	int startOfReference = endOfSubject+3;
	        	String reference = line.substring(startOfReference);
	        	reference = reference.substring(0, reference.indexOf('>'));
	        	reference = reference.substring(reference.lastIndexOf('/')+1);
	        	reference = reference.trim();
	        	String subject = line.substring(1, endOfSubject);
	        	subject = subject.substring(subject.lastIndexOf('/')+1);
	        	subject = subject.trim();
	        	if (reference.equals("icd9") || reference.equals("icd10")){
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

}
