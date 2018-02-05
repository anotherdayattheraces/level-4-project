package dictionary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.lemurproject.galago.core.index.disk.DiskIndex;

import entityRetrieval.core.Entity;
import entityRetrieval.core.Pair;
import entityRetrieval.core.SnomedEntity;
import knowledgeBase.KBFilter;
import knowledgeBase.SnomedToWikiMapper;

public class SnomedDictionaryEnhancer {

	private String path;
	private DictionaryHashMap dictionary;
	private PrintStream outputStream;
	private String newEntitiesPath;
	private String kbPath;
	private String newMappedEntitiesPath;
	private ArrayList<String> categories;
	private String categoryPath;
	private String finishedEntitiesPath;

	
	
	public SnomedDictionaryEnhancer(){
		this.path="C:/Work/Project/samples/prototype4/level-4-project/core/SnomedDictionary.txt";
		this.newEntitiesPath="C:/Work/Project/samples/prototype4/level-4-project/core/NewEntities.txt";
		this.kbPath="C:/Work/Project/samples/Unprocessed_Index";
		this.newMappedEntitiesPath="C:/Work/Project/samples/prototype4/level-4-project/core/NewMappedEntities.txt";
		this.categoryPath="C:/Work/Project/samples/prototype4/level-4-project/core/categories.txt";
		this.finishedEntitiesPath="C:/Work/Project/samples/prototype4/level-4-project/core/newFilteredMappedEntities.txt";
		this.categories=KBFilter.readInCategories(categoryPath);

		
		try {
			this.outputStream = new PrintStream(new FileOutputStream("NewEntities.txt",true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	public void enhanceDictionary(){
		SnomedDictionaryInitializer sdi = new SnomedDictionaryInitializer();
		try {
			this.dictionary=sdi.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Pair<String,Integer>> onePrefixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> oneSuffixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> twoPrefixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> twoSuffixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> threePrefixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> threeSuffixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> fourPrefixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> fourSuffixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> allPrefixes = new ArrayList<Pair<String,Integer>>();
		ArrayList<Pair<String,Integer>> allSuffixes = new ArrayList<Pair<String,Integer>>();
		FileReader file = null;
		try {
			file = new FileReader(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		try {
			while((line=br.readLine())!=null){
				String[] terms = line.split(" //")[0].split(" ");
				
				switch (terms.length){
				case 3:
					Addfix(terms[0],onePrefixes);
					Addfix(terms[2],oneSuffixes);
					Addfix(terms[0]+" "+terms[1],twoPrefixes);
					Addfix(terms[1]+" "+terms[2],twoSuffixes);
					break;
				case 4:
					Addfix(terms[0],onePrefixes);
					Addfix(terms[3],oneSuffixes);
					Addfix(terms[0]+" "+terms[1],twoPrefixes);
					Addfix(terms[2]+" "+terms[3],twoSuffixes);
					Addfix(terms[0]+" "+terms[1]+" "+terms[2],threePrefixes);
					Addfix(terms[1]+" "+terms[2]+" "+terms[3],threeSuffixes);
					break;
				case 5:
					Addfix(terms[0],onePrefixes);
					Addfix(terms[4],oneSuffixes);
					Addfix(terms[0]+" "+terms[1],twoPrefixes);
					Addfix(terms[3]+" "+terms[4],twoSuffixes);
					Addfix(terms[0]+" "+terms[1]+" "+terms[2],threePrefixes);
					Addfix(terms[2]+" "+terms[3]+" "+terms[4],threeSuffixes);
					Addfix(terms[0]+" "+terms[1]+" "+terms[2]+" "+terms[3],fourPrefixes);
					Addfix(terms[1]+" "+terms[2]+" "+terms[3]+" "+terms[4],fourSuffixes);
					break;
				case 6:
					Addfix(terms[0],onePrefixes);
					Addfix(terms[5],oneSuffixes);
					Addfix(terms[0]+" "+terms[1],twoPrefixes);
					Addfix(terms[4]+" "+terms[5],twoSuffixes);
					Addfix(terms[0]+" "+terms[1]+" "+terms[2],threePrefixes);
					Addfix(terms[3]+" "+terms[4]+" "+terms[5],threeSuffixes);
					Addfix(terms[0]+" "+terms[1]+" "+terms[2]+" "+terms[3],fourPrefixes);
					Addfix(terms[2]+" "+terms[3]+" "+terms[4]+" "+terms[5],fourSuffixes);
					break;
					
					default:
						break;
			}
				
				}
				
		
		Collections.sort(onePrefixes, score);
		Collections.sort(onePrefixes, score);
		Collections.sort(twoPrefixes, score);
		Collections.sort(twoPrefixes, score);
		Collections.sort(threePrefixes, score);
		Collections.sort(threeSuffixes, score);
		Collections.sort(fourPrefixes, score);
		Collections.sort(fourPrefixes, score);
		int min=50;
		System.out.println("One term prefixes ----------------------------------------------------------------------");
		for(double i=0;i<(double)onePrefixes.size();i++){
			if(onePrefixes.get((int)i).getR()>=min){
				allPrefixes.add(onePrefixes.get((int)i));
			}
			//System.out.println("One term prefix: "+onePrefixes.get((int)i).getL()+" apps: "+onePrefixes.get((int)i).getR());	
		}
		System.out.println("One term suffixes ----------------------------------------------------------------------");
		for(double i=0;i<(double)oneSuffixes.size();i++){
			if(oneSuffixes.get((int)i).getR()>=min){
				allSuffixes.add(oneSuffixes.get((int)i));
			}
			//System.out.println("One term prefix: "+onePrefixes.get((int)i).getL()+" apps: "+onePrefixes.get((int)i).getR());
			
		}
		System.out.println("Two term prefixes ----------------------------------------------------------------------");
		for(double i=0;i<(double)twoPrefixes.size();i++){
			if(twoPrefixes.get((int)i).getR()>=min){
				allPrefixes.add(twoPrefixes.get((int)i));
			}
			//System.out.println("Two term prefix: "+twoPrefixes.get((int)i).getL()+" apps: "+twoPrefixes.get((int)i).getR());
			
		}
		System.out.println("Two term suffixes ----------------------------------------------------------------------");
		for(double i=0;i<(double)twoSuffixes.size();i++){
			if(twoSuffixes.get((int)i).getR()>=min){
				allSuffixes.add(twoSuffixes.get((int)i));
			}
			//System.out.println("Two term prefix: "+twoPrefixes.get((int)i).getL()+" apps: "+twoPrefixes.get((int)i).getR());
			
		}
		System.out.println("Three term prefixes ----------------------------------------------------------------------");
		for(double i=0;i<(double)threePrefixes.size();i++){
			if(threePrefixes.get((int)i).getR()>=min){
				allPrefixes.add(threePrefixes.get((int)i));
			}
			//System.out.println("Three term prefix: "+threePrefixes.get((int)i).getL()+" apps: "+threePrefixes.get((int)i).getR());	
		}
		System.out.println("Three term suffixes ----------------------------------------------------------------------");

		for(double i=0;i<(double)threeSuffixes.size();i++){
			if(threeSuffixes.get((int)i).getR()>=min){
				allSuffixes.add(threeSuffixes.get((int)i));
			}
			//System.out.println("Three term prefix: "+threePrefixes.get((int)i).getL()+" apps: "+threePrefixes.get((int)i).getR());
			
		}
		System.out.println("Four term prefixes ----------------------------------------------------------------------");

		for(double i=0;i<(double)fourPrefixes.size();i++){
			if(fourPrefixes.get((int)i).getR()>=min){
				allPrefixes.add(fourPrefixes.get((int)i));
			}
			//System.out.println("Four term prefix: "+fourPrefixes.get((int)i).getL()+" apps: "+fourPrefixes.get((int)i).getR());	
		}
		System.out.println("Four term suffixes ----------------------------------------------------------------------");

		for(double i=0;i<(double)fourSuffixes.size();i++){
			if(fourSuffixes.get((int)i).getR()>=min){
				allSuffixes.add(fourSuffixes.get((int)i));
			}
			//System.out.println("Four term prefix: "+fourPrefixes.get((int)i).getL()+" apps: "+fourPrefixes.get((int)i).getR());
			
		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("All prefixes ----------------------------------------------------------------------");
		for(Pair<String,Integer> pair:allPrefixes){
			System.out.println(pair.getL()+" "+pair.getR());
			if(pair.getL().length()==2){
				pair.setL(pair.getL()+" ");
			}
			if(pair.getL().length()==1){
				continue;
			}
			for(Entity entity:dictionary.getDictionary().get(pair.getL().substring(0, 3))){
				if(entity.getName().contains(pair.getL())){
					String newEntity = entity.getName().substring(pair.getL().length()).trim();
					if(!dictionary.lookupString(newEntity)){
						System.out.println("Found new entity: "+newEntity);
						outputStream.println(newEntity);
					}
				}
			}
		}
		for(Pair<String,Integer> pair:allSuffixes){
			for(String key:dictionary.getDictionary().keySet()){
				for(Entity entity:dictionary.getDictionary().get(key)){
					if(entity.getName().endsWith(pair.getL())){
						String newEntity = entity.getName().substring(0, entity.getName().length()-pair.getL().length()).trim();
						if(!dictionary.lookupString(newEntity)){
							System.out.println("Found new entity: "+newEntity);
							outputStream.println(newEntity);
						}
					}
				}
			}
		}
		System.out.println("All suffixes ----------------------------------------------------------------------");
		for(Pair<String,Integer> pair:allSuffixes){
			System.out.println(pair.getL()+" "+pair.getR());
		}
	}
	
	public void mapSythesizedEntities(){
		PrintStream newMappedEntities=null;
		try {
			newMappedEntities = new PrintStream(new FileOutputStream("NewMappedEntities.txt",true));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		DiskIndex index = null;
		try {
			index = new DiskIndex(kbPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileReader file = null;
		try {
			file = new FileReader(newEntitiesPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		try {
			while((line=br.readLine())!=null){
				if(line.length()<3){
					continue;
				}
				if(line.toCharArray()[1]==' '){
					continue;
				}
				Long id=null;
				String wikiEntity=null;
				id=index.getIdentifier(SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(line));
				if(id!=-1){
					
					wikiEntity=SnomedToWikiMapper.formatEntityNameFirstLetterUpperCase(line);
					System.out.println("Found new entity: "+wikiEntity);
					newMappedEntities.println(wikiEntity);
				}
			}
				
			}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	
	}
	public void filterSythesizedEntitiesByCategory(){
		FileReader file = null;
		try {
			file = new FileReader(newMappedEntitiesPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		ArrayList<Entity> unfilteredEntities = new ArrayList<Entity>();
		try {
			while((line=br.readLine())!=null){
				Boolean exists=false;
				for(Entity entity:unfilteredEntities){
					if(entity.getName().equals(line)){
						exists=true;
						break;
					}
				}
				if(!exists){
					unfilteredEntities.add(new Entity(line,line.replaceAll(" ", "%20")));
				}
			}
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		KBFilter kbf = new KBFilter(unfilteredEntities);
		unfilteredEntities = kbf.filterEntities(System.out);
		PrintStream newFilteredMappedEntities = null;
		try {
			newFilteredMappedEntities = new PrintStream(new FileOutputStream("newFilteredMappedEntities.txt",true));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		for(Entity e:unfilteredEntities){
			newFilteredMappedEntities.println(e.getName());
		}
	}
	public ArrayList<String> readInCompleteEntities(){
		FileReader file = null;
		try {
			file = new FileReader(finishedEntitiesPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(file);
		String line;
		ArrayList<String> entities = new ArrayList<String>();
		try {
			while((line=br.readLine())!=null){
				entities.add(line);
			}
				
			}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entities;
		
	}
	
		
	
	

	public static void sortByScore(ArrayList<Pair<String,Integer>> unorderedList){ // method for sorting entities in a list by their score
	}
	public static Comparator<Pair<String,Integer>> score = new Comparator<Pair<String,Integer>>() {

		public int compare(Pair<String, Integer> arg0, Pair<String, Integer> arg1) {
			int one = arg0.getR();
			int two = arg0.getR();
			return Integer.compare(one, two);
		}};
		public void Addfix(String fix, ArrayList<Pair<String,Integer>> fixes){
			for(Pair<String,Integer> pair:fixes){
				if(pair.getL().equals(fix)){
					pair.setR(pair.getR()+1);
					return;
				}
			}
			fixes.add(new Pair<String,Integer>(fix,1));
			
		}
}
