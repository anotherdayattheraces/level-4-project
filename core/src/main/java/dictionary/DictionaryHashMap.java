package dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import entityRetrieval.core.Entity;

public class DictionaryHashMap {
	private HashMap<String,ArrayList<Entity>> dictionary;
	
	public DictionaryHashMap(){
		this.dictionary = new HashMap<String,ArrayList<Entity>>();
	}
	
	public HashMap<String, ArrayList<Entity>> getDictionary(){
		return dictionary;
	}
	
	public void addEntity(Entity e){
		String key = e.getName().substring(0,3);
		if(!dictionary.containsKey(key)){ //if hashmap contains not entrys for given key yet
			dictionary.put(key, new ArrayList<Entity>());}
		dictionary.get(key).add(e); //add entity to dictionary	
	}
	
	public Boolean lookupEntity(Entity e){
		String key = e.getName().substring(0,3);
		Set<String> keys = dictionary.keySet();
		Iterator<String> i = keys.iterator();
		while(i.hasNext()){
			String currentKey = i.next();
			if(key==currentKey){
				for(Entity currentValue:dictionary.get(currentKey)){
					if(currentValue.getName()==e.getName()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Boolean lookupString(String s){
		if(s.length()<3) return false; //string is not large enough to have a key
		String key = s.substring(0,3);
		Set<String> keys = dictionary.keySet();
		Iterator<String> i = keys.iterator();
		while(i.hasNext()){
			String currentKey = i.next();
			if(key==currentKey){
				for(Entity currentValue:dictionary.get(currentKey)){
					if(currentValue.getName()==s){
						return true;
					}
				}
			}
		}
		return false;
	}

}
