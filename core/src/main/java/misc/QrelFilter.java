package misc;

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

public class QrelFilter {
	private String qrelPath;
	private String mappingPath;
	private HashMap<String,HashMap<String,ArrayList<String>>> mappings;


	public QrelFilter(){
		this.qrelPath="C:/Work/Project/samples/prototype4/level-4-project/core/filteredQrels.txt";
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.mappings=readMappings(mappingPath);
		
	}
	public static HashMap<String,HashMap<String,ArrayList<String>>> readMappings(String filepath){
		HashMap<String,HashMap<String,ArrayList<String>>> mappings = new HashMap<String,HashMap<String,ArrayList<String>>>();
		FileReader file = null;
		try {
			file = new FileReader(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		try {
			while((line=br.readLine())!=null){
				String[] lineSplit = line.split("///");
				String wikiEntity = lineSplit[0];
				if(wikiEntity.length()<3) continue;
				if(mappings.get(wikiEntity.substring(0,3))==null){ //if no mapping exists yet for that substring
					mappings.put(wikiEntity.substring(0, 3), new HashMap<String,ArrayList<String>>());
					mappings.get(wikiEntity.substring(0, 3)).put(wikiEntity, new ArrayList<String>());
					mappings.get(wikiEntity.substring(0, 3)).get(wikiEntity).add(lineSplit[1]);
				}
				else if(!mappings.get(wikiEntity.substring(0,3)).containsKey(wikiEntity)){
					mappings.get(wikiEntity.substring(0, 3)).put(wikiEntity, new ArrayList<String>());
					mappings.get(wikiEntity.substring(0, 3)).get(wikiEntity).add(lineSplit[1]);
				}
				else{
					mappings.get(wikiEntity.substring(0, 3)).get(wikiEntity).add(lineSplit[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mappings;
	}
	
	public void filterByMapping(){
		FileReader file = null;
		try {
			file = new FileReader(qrelPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		
		File fout = new File("snomedQrels"+".txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		try {
			while((line=br.readLine())!=null){
				String[] lineSplit=line.split(" ");
				String wikiEntity = lineSplit[2];
				if(!mappings.containsKey(wikiEntity.substring(0, 3))){
					System.out.println("No mapping for entity: "+wikiEntity);
				}
				else if(!mappings.get(wikiEntity.substring(0, 3)).containsKey(wikiEntity)){
					System.out.println("No mapping for entity: "+wikiEntity);
				}
				else{
					System.out.println("Mapping for entity: "+wikiEntity+" to "+mappings.get(wikiEntity.substring(0, 3)).get(wikiEntity).size()+" entities");
					bw.write(line);
					bw.newLine();

				}
	}	
		
	}
	 catch (IOException e) {
		e.printStackTrace();
	}
		try {
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	try {
		br.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}
