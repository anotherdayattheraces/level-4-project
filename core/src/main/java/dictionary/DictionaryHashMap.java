package dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import entityRetrieval.core.Entity;
import entityRetrieval.core.SnomedEntity;

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
		if(!dictionary.containsKey(key)){ //if hashmap contains no entrys for given key yet
			dictionary.put(key, new ArrayList<Entity>());}
		else {
			if(lookupString(e.getName())) return;
			
		}
		dictionary.get(key).add(e); //add entity to dictionary	
	}
	public int getSize(){
		int size=0;
		Set<String> keys = dictionary.keySet();
		Iterator<String> i = keys.iterator();
		String key;
		while(i.hasNext()){
			key = i.next();
			size+=dictionary.get(key).size(); 
		}
		return size;
	}
	
	public Boolean lookupEntity(Entity e){
		if(e.getName().length()<3) return false; //string is not large enough to have a key
		String key = e.getName().substring(0,3);
		if(dictionary.get(key)==null){
			return false;
		}
		else{
			for(Entity entity:dictionary.get(key)){
				if(entity.getName().toLowerCase().equals(e.getName().toLowerCase())) {
					return true;
				}
			}
			return false;
		}

	}
	
	public Boolean lookupString(String s){
		if(s.length()<3) return false; //string is not large enough to have a key
		String key = s.toLowerCase().substring(0,3);
		if(dictionary.get(key)==null){
			return false;
		}
		else{
			for(Entity e:dictionary.get(key)){
				if(e.getName().toLowerCase().equals(s.toLowerCase())) {
					return true;
				}
			}
			return false;
		}
	}
	public ArrayList<Entity> toArray(){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(String key:dictionary.keySet()){
			for(Entity e:dictionary.get(key)){
				entities.add(e);
			}
		}
		return entities;
	}

	public Boolean lookupId(String name, String s){
		if(name.length()<3) return false; //string is not large enough to have a key
		String key = name.substring(0,3);
		if(dictionary.get(key)==null){
			return false;
		}
		else{
			for(Entity e:dictionary.get(key)){
				if(e.getId().equals(s)) {
					return true;
				}
			}
			return false;
		}
	}
	

	
	
	


}
