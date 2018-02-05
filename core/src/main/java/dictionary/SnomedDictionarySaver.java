package dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import entityRetrieval.core.Entity;

public class SnomedDictionarySaver extends DictionarySaver{
	private DictionaryHashMap dictionary;
	private String filename;

	public SnomedDictionarySaver(DictionaryHashMap dhm, String filename){
		this.dictionary=dhm;
		this.filename=filename;
	}
	public SnomedDictionarySaver(DictionaryHashMap dhm){
		this.dictionary=dhm;
		this.filename="SnomedDictionary";
	}
	public SnomedDictionarySaver(DictionaryHashMap dhm, ArrayList<String> newEntities){
		this.dictionary = addNewEntities(dhm,newEntities);
		this.filename="EnhancedSnomedDictionary";
		
	}
	public void save() throws IOException {
		File fout = new File(filename+".txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos)); 
		Set<String> keys = dictionary.getDictionary().keySet();
		Iterator<String> i = keys.iterator();
		while(i.hasNext()){
			String currentKey = i.next();
			for(Entity e:dictionary.getDictionary().get(currentKey)){
				bw.write((e.getName()));
				bw.write("//");
				if(e.getId()==null){
					bw.write("000");
				}
				else{
					bw.write(e.getId());
				}
				bw.newLine();
				
			}
		}
		bw.close();

	}
	public DictionaryHashMap addNewEntities(DictionaryHashMap dhm, ArrayList<String> newEntities){
		int num=1;
		int succ=0;
		for(String entity:newEntities){
			if(!dhm.lookupString(entity.toLowerCase())){
				dhm.addEntity(new Entity(entity.toLowerCase(),null));
				System.out.println("Adding new entity: "+entity+" num: "+ ++succ+ " of "+num);
				
			}
			num++;
		}
		return dhm;
	}

}
