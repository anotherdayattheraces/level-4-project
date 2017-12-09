package dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import entityRetrieval.core.Entity;

public class DictionaryInitializer {
	String filepath;
	
	public DictionaryInitializer(String filepath){
		this.filepath=filepath;
	}
	public DictionaryInitializer(){
		this.filepath="C:/Work/Project/samples/prototype4/level-4-project/core/Dictionary.txt";
	}
	public DictionaryHashMap initialize() throws IOException{
		FileReader file = new FileReader(filepath);
		BufferedReader br = new BufferedReader(file);
		String line;
		DictionaryHashMap dictionary = new DictionaryHashMap();
		while((line=br.readLine())!=null){
			line.replace('_', ' ');
			line.toLowerCase();
			dictionary.addEntity(new Entity(line));
			
		}
		br.close();
		return dictionary;
		
	}
}
