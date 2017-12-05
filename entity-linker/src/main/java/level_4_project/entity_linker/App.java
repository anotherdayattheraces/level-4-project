package level_4_project.entity_linker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws FileNotFoundException, UnsupportedEncodingException{
    	String path = "C:/Work/Project/samples/Pub_Med_Data/data1/medline17n0004.xml";
    	FileInputStream inputStream = new FileInputStream(path);
		Scanner sc = new Scanner(inputStream, "UTF-8");
		//DbpediaReferenceParser drp = new DbpediaReferenceParser();
		//List<String> refs = Arrays.asList("icd9", "icd10");
		//drp.generateEntities(refs);
		int count = 0;
		while(sc.hasNext()){
			String line = sc.next();
			if(line.contains("</PMID")) System.out.println(line);
			count++;
		}


		

    }
}