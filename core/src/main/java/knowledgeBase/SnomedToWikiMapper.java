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
import evaluation.EntityMatcher;

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
			Long id=null;
			String wikiEntity=null;
			try {
				id = index.getIdentifier(formatEntityName(e.getName()));
				wikiEntity=formatEntityName(e.getName());
			} catch (IOException e1) {
			}
			if(id==-1){ //if first mapping attempt was unsuccsessful 
				try {
					id = index.getIdentifier(formatEntityNameFirstLetterUpperCase(e.getName()));
					wikiEntity=formatEntityNameFirstLetterUpperCase(e.getName());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(id==-1&&e.getName().split(" ").length>=4){
				try {
					id=index.getIdentifier(formatNameForAtLeast4Terms(e.getName()));
					if(id!=-1){
						wikiEntity=formatNameForAtLeast4Terms(e.getName());
						System.out.println("Mapped "+e.getName()+" to: "+formatNameForAtLeast4Terms(e.getName()));
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(id==-1&&e.getName().split(" ").length>3){
				try {
					id=index.getIdentifier(EntityMatcher.lastTwoWords(e.getName()));
					if(id!=-1){
						System.out.println("Mapped "+e.getName()+" to: "+EntityMatcher.lastTwoWords(e.getName()));
						wikiEntity=EntityMatcher.lastTwoWords(e.getName());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(id==-1&&e.getName().split(" ").length>3){
				try {
					id=index.getIdentifier(EntityMatcher.FirstTwoWords(e.getName()));
					if(id!=-1){
						System.out.println("Mapped "+e.getName()+" to: "+EntityMatcher.FirstTwoWords(e.getName()));
						wikiEntity=EntityMatcher.FirstTwoWords(e.getName());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(id==-1&&e.getName().split(" ").length>=3){
				try {
					id=index.getIdentifier(takeFinalWord(e.getName()));
					if(id!=-1){
						System.out.println("Mapped "+e.getName()+" to: "+takeFinalWord(e.getName()));
						wikiEntity=takeFinalWord(e.getName());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(id==-1){
				try {
					id=index.getIdentifier(e.getName());
					wikiEntity=e.getName();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			try {
				wikiEntity = index.getName(id);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if(id!=-1){
				//System.out.println("id: "+id+" lineNum: "+lineNum);
				//System.out.println("New mapping, snomed entity: "+e.getName()+" to wiki entity: "+wikiEntity);
				if(mappings.containsKey(wikiEntity)){ //if a mapping already exists
					mappings.get(wikiEntity).add(e.getName());
				}
				else{
					mappings.put(wikiEntity, new ArrayList<String>());
					mappings.get(wikiEntity).add(e.getName());
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

	public static String formatNameForAtLeast4Terms(String name){
		String[] entityTerms = name.split(" ");
		StringBuilder formattedName = new StringBuilder();
		for(int i=0;i<entityTerms.length-1;i++){
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
	public String takeFinalWord(String name){
		String[] split = name.split(" ");
		return split[split.length-1].substring(0, 1).toUpperCase()+split[split.length-1].substring(1);
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
		for(String wikiEntity:mappings.keySet()){
			try {
				for(String snomedEntity:mappings.get(wikiEntity)){
					bw.write(wikiEntity+"///"+snomedEntity);
					bw.newLine();
				}
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
