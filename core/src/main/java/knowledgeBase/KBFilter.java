package knowledgeBase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;

import entityRetrieval.core.Entity;

public class KBFilter {
	private ArrayList<Entity> returnedEntities;
	private ArrayList<String> categories;
	private String path;
	private String kbPath;
	
	public KBFilter(ArrayList<Entity> returnedEntities){
		this.path="C:/Work/Project/samples/prototype4/level-4-project/core/categories.txt";
		this.kbPath="C:/Work/Project/samples/Unprocessed_Index";
		this.categories=readInCategories(path);
		this.returnedEntities=returnedEntities;
	}
		
	public static ArrayList<String> readInCategories(String path){
		FileReader file = null;
		try {
			file = new FileReader(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		ArrayList<String> categories = new ArrayList<String>();
		try {
			while((line=br.readLine())!=null){
				categories.add(line.split("///")[0]);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Inital list size: "+categories.size());
		Double catSize = (double) (categories.size()/100);
		Double N = (double) 30; //the percent of the top categories to keep
		catSize=catSize*N;
		int topNPercent = Integer.valueOf(catSize.intValue());
		//System.out.println("Final list size: "+topNPercent);
		while(categories.size()!=topNPercent){
			categories.remove(categories.size()-1); //keep removing final element until list is right size
		}
		return categories;
	}
	
	public ArrayList<Entity> filterEntities(){
		HashMap<String,ArrayList<String>> entityToCategoriesMapping = getEntityCategories();
		ArrayList<Entity> filteredEntities = new ArrayList<Entity>();
		for(Entity entity:returnedEntities){
			if((entityToCategoriesMapping.get(entity.getName())!=null)){
				Boolean catMatch = findCategoryMatch(categories,entityToCategoriesMapping.get(entity.getName()),entity);
				if(catMatch){
					filteredEntities.add(entity);
				}
				else{
					System.out.println("Removed entity: "+entity.getName());
					}
				}
			else{
				System.out.println("Couldn't find any categories for: "+entity.getName());
			}
			}
		return filteredEntities;
	}
	
	public HashMap<String,ArrayList<String>> getEntityCategories(){
		DiskIndex index=null;
		Document.DocumentComponents dc = new Document.DocumentComponents( false, true, true );
		String categoryIndicator = "<link tokenizeTagContent=\"false\">Category:";
		try {
			index = new DiskIndex(kbPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<String,ArrayList<String>> entityToCategoriesMapping = new HashMap<String,ArrayList<String>>();
		for(Entity entity:returnedEntities){
			Document document=null;
			try {
				document = index.getDocument(entity.getName(), dc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(document==null){
				try {
					document=index.getDocument(SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(entity.getName()), dc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(document==null){
				System.out.println("Unable to match entity: "+entity.getName());
				continue;
			}
			Boolean first = true;
			for (int i = -1; (i = document.text.indexOf(categoryIndicator, i + 1)) != -1; i++) {
				int start = i+categoryIndicator.length();
				int end = document.text.substring(start).indexOf("</link>")+start;
				String category = document.text.substring(start, end);
				category = category.replaceAll(" ", "%20");
				if(first){ //if arraylist for this entity has not yet been initialized 
					entityToCategoriesMapping.put(entity.getName(), new ArrayList<String>());
					first=false;
				}
				entityToCategoriesMapping.get(entity.getName()).add(category);
								}	
				}
		return entityToCategoriesMapping;
	}
	public Boolean findCategoryMatch(ArrayList<String> topCategories, ArrayList<String> entityCategories, Entity currentEntity){
		for(String category:topCategories){
			for(String entityCat:entityCategories){
				if(category.equals(entityCat)){
					System.out.println("Matched entity: "+currentEntity.getName()+" for category: "+entityCat);
					return true;
					
				}
			}
		}
		return false;
	}

		 
}
