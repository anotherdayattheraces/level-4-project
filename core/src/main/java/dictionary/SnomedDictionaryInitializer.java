package dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import entityRetrieval.core.Entity;

public class SnomedDictionaryInitializer extends DictionaryInitializer{
	
	private String filepath;
	
	public SnomedDictionaryInitializer(String filepath){
		this.filepath=filepath;
	}
	public SnomedDictionaryInitializer(){
		this.filepath="C:/Work/Project/samples/prototype4/level-4-project/core/EnhancedSnomedDictionary.txt";
	}

	public DictionaryHashMap initialize() throws IOException {
		FileReader file = new FileReader(filepath);
		BufferedReader br = new BufferedReader(file);
		String line;
		DictionaryHashMap dictionary = new DictionaryHashMap();
		while((line=br.readLine())!=null){
			String[] entityDetails = line.split("//");
			String name = entityDetails[0].trim();
			String id = entityDetails[1].trim();
			if(name.length()<3) continue;
			//System.out.println("adding entity with name: "+name+" id: "+id);
			dictionary.addEntity(new Entity(name,id));
			
		}
		printStats(dictionary);
		br.close();
		return dictionary;
	}
	
	public void printStats(DictionaryHashMap dictionary){
		//System.out.println("Num keys :"+dictionary.getDictionary().keySet().size());
		int max =0;
		String maxkey = null;
		for(String key:dictionary.getDictionary().keySet()){
			if(dictionary.getDictionary().get(key).size()>max){
				max=dictionary.getDictionary().get(key).size();
				maxkey=key;
			}
		}
		//System.out.println("max bucket: "+max+" string: "+maxkey);
		
	}

}
