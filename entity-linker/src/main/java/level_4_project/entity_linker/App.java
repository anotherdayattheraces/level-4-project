package level_4_project.entity_linker;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ){
    	DbpediaReferenceParser drp = new DbpediaReferenceParser();
    	try {
			ArrayList<String> entities = drp.generateEntities();
			for(String e:entities){
				System.out.println(e);
			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
    }
}
