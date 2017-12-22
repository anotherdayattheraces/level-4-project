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
		this.filepath="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedDictionary.txt";
	}

	public DictionaryHashMap initialize() throws IOException {
		FileReader file = new FileReader(filepath);
		BufferedReader br = new BufferedReader(file);
		String line;
		DictionaryHashMap dictionary = new DictionaryHashMap();
		while((line=br.readLine())!=null){
			//System.out.println("adding entity with name: "+line);
			dictionary.addEntity(new Entity(line));
			
		}

		return dictionary;
	}

}
