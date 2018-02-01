package knowledgeBase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lemurproject.galago.core.index.disk.DiskIndex;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.Document.DocumentComponents;

import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import misc.CategoryGenerator;

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
		
	public static ArrayList<String> readInCategories(String path){ //read in saved file of top categories
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
	
	public ArrayList<Entity> filterEntities(){ //using the instance arraylist of entities return a list of entities that have at least 1 category in the top N categories 
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
	
	public HashMap<String,ArrayList<String>> getEntityCategories(){ //using the list of entities given on initilization, find the associated categories for each entity
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
					document=index.getDocument(SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(entity.getName()), dc); //try to find a match in the kb if one not already found
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(document==null){
				System.out.println("Unable to match entity: "+entity.getName());
				continue;
			}
			entityToCategoriesMapping.put(entity.getName(), new ArrayList<String>());
			for (int i = -1; (i = document.text.indexOf(categoryIndicator, i + 1)) != -1; i++) { //iterate through document text looking for redirect pattern - indicates redirect 
				int start = i+categoryIndicator.length();
				int end = document.text.substring(start).indexOf("</link>")+start;
				String category = document.text.substring(start, end);
				category = category.replaceAll(" ", "%20");				
				entityToCategoriesMapping.get(entity.getName()).add(category);
																				}
			if(entityToCategoriesMapping.get(entity.getName()).isEmpty()&&document.text.contains("#REDIRECT")){ //if there is no categories for an entity - possibly is a redirect
				Pair<String, ArrayList<String>> redirectPair = searchRedirect(index,dc,document.text,entity.getName()); //retrieve the new entity name and it's categories
				if(redirectPair==null) continue;
				if(redirectPair.getR()==null) continue; //the redirected entity was not contained within the kb
				entityToCategoriesMapping.remove(entity.getName()); //remove the old entity -> categories mapping 
				System.out.println("Removed entity: "+entity.getName()+" replaced with: "+redirectPair.getL());
				entity.setName(redirectPair.getL()); //set new entity name
				entityToCategoriesMapping.put(entity.getName(), new ArrayList<String>());
				entityToCategoriesMapping.get(entity.getName()).addAll(redirectPair.getR()); 
			}
				}
		return entityToCategoriesMapping;
	}
	public Boolean findCategoryMatch(ArrayList<String> topCategories, ArrayList<String> entityCategories, Entity currentEntity){ //given list of top N categories and an entity's associated categories, see if you can match any
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
	public Pair<String,ArrayList<String>> searchRedirect(DiskIndex index, DocumentComponents dc, String docText, String entity){  //given a known redirect - find the redirected entity's categories
		int start = 0;
		if(docText.contains("#REDIRECT <link tokenizeTagContent=\"false\">")){ //there is a subtle difference between the two strings, this i think is due to incorrect formatting but it was giving me the wrong entities so i need to address both cases
			start = docText.indexOf("#REDIRECT <link tokenizeTagContent=\"false\">")+43; //plus 43 because its the length of "#REDIRECT <link tokenizeTagContent="false">" plus a space
		}
		else if(docText.contains("#REDIRECT<link tokenizeTagContent=\"false\">")){
			start = docText.indexOf("#REDIRECT<link tokenizeTagContent=\"false\">")+42;
		}
		
		else{
			System.out.println("Redirect with improper formatting");
			return null;
		}
		int end = docText.indexOf("</link>");
		String redirect = docText.substring(start,end);
		System.out.println("Redirected from entity: "+entity+" to entity: "+redirect);
		return new Pair<String, ArrayList<String>>(redirect,findEntityCategories(index,redirect,dc));
	}
	
	public static ArrayList<String> findEntityCategories(DiskIndex index, String entity, DocumentComponents dc){ //given a wiki entity, find all categories associated from its wiki page or the page it redirects to
		String categoryIndicator = "<link tokenizeTagContent=\"false\">Category:";
		ArrayList<String> categories = new ArrayList<String>();
		Document document=null;
		try {
			document = index.getDocument(entity, dc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(document==null){
			System.out.println("Couldn't find wiki entry for: "+entity);
			return null;
		}
		for (int i = -1; (i = document.text.indexOf(categoryIndicator, i + 1)) != -1; i++) {
			int start = i+categoryIndicator.length();
			int end = document.text.substring(start).indexOf("</link>")+start;
			String category = document.text.substring(start, end);
			category = category.replaceAll(" ", "%20");
			categories.add(category);
									}
		return categories;
	}


		 
}
