package generation;

import java.io.FileNotFoundException;
import dictionary.DictionaryHashMap;

public abstract class DictionaryGenerator {
	private String path;


public abstract DictionaryHashMap generateEntities() throws FileNotFoundException;

}