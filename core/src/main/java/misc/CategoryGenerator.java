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
import entityRetrieval.core.Pair;
import knowledgeBase.KBFilter;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Document.DocumentComponents;

public class CategoryGenerator {
	private HashMap<String,ArrayList<String>> mappedEntities;
	private String path;
	private String mappingPath;
	
	public CategoryGenerator(){
		this.mappingPath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedToWikiMappings.txt";
		this.mappedEntities=readMappings(mappingPath);
		this.path="C:/Work/Project/samples/Unprocessed_Index";

	}
	public HashMap<String,Integer> findCategories(){
		return findCategories("C:/Work/Project/samples/Unprocessed_Index",mappedEntities);
	}
	public static HashMap<String,Integer> findCategories(String path, HashMap<String,ArrayList<String>> mappedEntities){ //
		HashMap<String,Integer> categoryMentions = new HashMap<String,Integer>();
		DiskIndex index=null;
		try {
			index = new DiskIndex(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
		String previous = null;
		for(String key:mappedEntities.keySet()){
			for(String wikiEntity:mappedEntities.get(key) ){
				if(previous==wikiEntity){
					continue;
				}
				ArrayList<String> entityCategories = KBFilter.findEntityCategories(index,wikiEntity,dc);
				for(String category:entityCategories){
					if(!categoryMentions.containsKey(category)){
						categoryMentions.put(category, 1);
					}
					else{
						categoryMentions.put(category, categoryMentions.get(category)+1);
					}
				}
				previous=wikiEntity;
			}
		}
		return categoryMentions;
	}
	
	
	public static HashMap<String,ArrayList<String>> readMappings(String filepath){
		HashMap<String,ArrayList<String>> entities = new HashMap<String,ArrayList<String>>();
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
				if(entities.get(wikiEntity.substring(0,3))!=null){
					entities.get(wikiEntity.substring(0, 3)).add(wikiEntity);
				}
				else{
					entities.put(wikiEntity.substring(0,3), new ArrayList<String>());
					entities.get(wikiEntity.substring(0, 3)).add(wikiEntity);
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
		return entities;
	}
	public void saveCategories(HashMap<String,Integer> categories){
		ArrayList<Pair<String,Integer>> toptencats = new ArrayList<Pair<String,Integer>>();
		for(String key:categories.keySet()){
			if(categories.get(key)>=10){
				Boolean added = false;
				for(int i=0;i<toptencats.size();i++){
		    		System.out.println("Adding category: "+key);
					if(categories.get(key)>toptencats.get(i).getR()){
						toptencats.add(i, new Pair<String, Integer>(key,categories.get(key)));
						added=true;
						break;
					}		
				}
				if(!added){
					toptencats.add(new Pair<String, Integer>(key,categories.get(key)));
				}
			}
		}
		String filename="categories";
		File fout = new File(filename+".txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		for(Pair<String,Integer> cat:toptencats){
			try {
				bw.write(cat.getL()+"///"+cat.getR());
				bw.newLine();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
