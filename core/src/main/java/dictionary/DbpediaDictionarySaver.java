package dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Set;

import entityRetrieval.core.Entity;

public class DbpediaDictionarySaver extends DictionarySaver{
	private DictionaryHashMap dictionary;
	private String filename;
	
	public DbpediaDictionarySaver(DictionaryHashMap dhm, String filename){
		this.dictionary=dhm;
		this.filename=filename;
	}
	public DbpediaDictionarySaver(DictionaryHashMap dhm){
		this.dictionary=dhm;
		this.filename="DbpediaDictionary";
	}
	
	public void save() throws IOException{
		File fout = new File(filename+".txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		Set<String> keys = dictionary.getDictionary().keySet();
		Iterator<String> i = keys.iterator();
		while(i.hasNext()){
			String currentKey = i.next();
			for(Entity e:dictionary.getDictionary().get(currentKey)){
				bw.write((e.getName()));
				//bw.write("/");
				//bw.write(e.getUrl());
				bw.newLine();
				
			}
		}
		bw.close();
	}
	
}
