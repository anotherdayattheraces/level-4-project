package knowledgeBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.lemurproject.galago.core.index.disk.DiskIndex;

import dictionary.DictionaryHashMap;
import dictionary.SnomedDictionaryInitializer;
import entityRetrieval.core.Entity;

public class SnomedToWikiMapper {
	private String path;
	private DictionaryHashMap snomedDictionary;
	
	public SnomedToWikiMapper(){
		this.path="C:/Work/Project/samples/Unprocessed_Index";
		SnomedDictionaryInitializer init = new SnomedDictionaryInitializer();
		try {
			this.snomedDictionary=init.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public HashMap<String,ArrayList<String>> generateMappings(){
		DiskIndex index = null;
		try {
			index = new DiskIndex(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		HashMap<String,ArrayList<String>> mappings = new HashMap<String,ArrayList<String>>();
		int lineNum=1;
		for(Entity e:snomedDictionary.toArray()){
			//if(true) return null;
			Long id=null;
			try {
				id = index.getIdentifier(formatEntityName(e.getName()));
			} catch (IOException e1) {
				//e1.printStackTrace();
			}
			if(id==-1){ //if first mapping attempt was unsuccsessful 
				try {
					id = index.getIdentifier(formatEntityNameFirstLetterUpperCase(e.getName()));
				} catch (IOException e1) {
					//e1.printStackTrace();
				}
			}
			String wikiEntity=null;
			try {
				wikiEntity = index.getName(id);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if(id!=-1){
				System.out.println("id: "+id+" lineNum: "+lineNum);
				System.out.println("New mapping, snomed entity: "+e.getName()+" to wiki entity: "+wikiEntity);
				if(mappings.containsKey(e.getName())){ //if a mapping already exists
					mappings.get(e.getName()).add(wikiEntity);
				}
				else{
					mappings.put(e.getName(), new ArrayList<String>());
					mappings.get(e.getName()).add(wikiEntity);
				}
			}
			lineNum++;
		}
		System.out.println(mappings.size());
		System.out.println(lineNum);
		try {
			index.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return mappings;

	}
	public static String formatEntityName(String name){
		String[] entityTerms = name.split(" ");
		StringBuilder formattedName = new StringBuilder();
		for(int i=0;i<entityTerms.length;i++){
			String upperCaseTerm = entityTerms[i].substring(0, 1).toUpperCase()+entityTerms[i].substring(1);
			formattedName.append(upperCaseTerm);
			if(i!=entityTerms.length-1){
				formattedName.append(" ");
			}
		}
		return formattedName.toString().trim();
		
	}
	public static String formatEntityNameFirstLetterUpperCase(String name){
		String[] entityTerms = name.split(" ");
		StringBuilder formattedName = new StringBuilder();
		for(int i=0;i<entityTerms.length;i++){
			String word = entityTerms[i];
			if(i==0){
				word = entityTerms[i].substring(0, 1).toUpperCase()+entityTerms[i].substring(1);
			}
			formattedName.append(word);
			if(i!=entityTerms.length-1){
				formattedName.append(" ");
			}
		}
		return formattedName.toString().trim();
	}
	
	public void saveMappings(HashMap<String,ArrayList<String>> mappings){
		String filename = "SnomedToWikiMappings";
		File fout = new File(filename+".txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for(String snomedEntity:mappings.keySet()){
			try {
				bw.write(snomedEntity+"///"+mappings.get(snomedEntity).get(0));
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
