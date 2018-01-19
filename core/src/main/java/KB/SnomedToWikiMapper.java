package KB;

import java.io.IOException;
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
			Long id=null;
			try {
				id = index.getIdentifier(formatEntityName(e.getName()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
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
		return mappings;

	}
	public static String formatEntityName(String name){
		String[] entityTerms = name.split(" ");
		StringBuilder formattedName = new StringBuilder();
		for(int i=0;i<entityTerms.length;i++){
			String upperCaseTerm = entityTerms[i].substring(0, 1).toUpperCase()+entityTerms[i].substring(1);
			formattedName.append(upperCaseTerm);
			if(i!=entityTerms.length-1){
				formattedName.append("%20");
			}
		}
		return formattedName.toString().trim();
		
	}
}
