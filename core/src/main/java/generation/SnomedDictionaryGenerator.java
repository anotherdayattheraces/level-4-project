package generation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import dictionary.DictionaryHashMap;
import entityRetrieval.core.SnomedEntity;

public class SnomedDictionaryGenerator extends DictionaryGenerator{
	private String path;
	public SnomedDictionaryGenerator(){
		this.path = "C:/Work/Project/samples/Snomed-ct/uk_sct1cl_24.0.0_20171001000001/SnomedCT_RF1Release_INT_20170731/Terminology/Content/sct1_Concepts_Core_INT_20170731.txt";
	}
	public DictionaryHashMap generateEntities() throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(path);
		Scanner sc = new Scanner(inputStream, "UTF-8");
		DictionaryHashMap dhm = new DictionaryHashMap();
		Boolean started=false;
		String type = null;
	    char endOfString = 0;
		while(sc.hasNext()){
			String line = sc.nextLine();
			if(!started&&line.contains("101009")){
				started=true;
				char[] array = line.toCharArray();
				endOfString = array[6];
			}
			if(started){
				//if(line.contains("(organism")){ removed as gave me too many non medical entities
				//	type = "organism";
				//}
				if(line.contains("(substance)")){
					type = "substance";
				}
				else if(line.contains("(disease)")){
					type = "disease";
				}
				else if(line.contains("(disorder)")){
					type = "disorder";
				}
				else if(line.contains("(observable entity)")){
					type = "observable entity";
				}
				else if(line.contains("(procedure)")){
					type = "procedure";
				}
				else if(line.contains("(body structure)")){
					type = "body structure";
				}
				else{
					continue;
				}
				
				System.out.println(line);
				char[] array = line.toCharArray();
				int position=0;
				StringBuilder idBuilder = new StringBuilder();
				StringBuilder nameBuilder = new StringBuilder();
				while(array[position]!=endOfString){
					idBuilder.append(array[position++]);
				}
				position++;
				while(array[position]!=endOfString){
					position++;
				}
				position++;
				Boolean endOfName=false;
				while(!endOfName){
					if(array[position]=='('){
						endOfName = investigate(array,position,type);
					}
					nameBuilder.append(array[position++]);
				}
				String name = nameBuilder.toString().substring(0, nameBuilder.length()-1);
				System.out.println("Adding entity with name: "+name+" id: "+idBuilder.toString()+" type: "+type);
				dhm.addEntity(new SnomedEntity(name.toLowerCase(),idBuilder.toString(),type,0));


				}
				
			}

		
		sc.close();
		return dhm;
	}
	public Boolean investigate(char[] line, int position, String type){
		int i=1;
		for(char c:type.toCharArray()){
			if(line[position+i++]!=c){
				return false;
				}
			}
			return true;
	}

}
