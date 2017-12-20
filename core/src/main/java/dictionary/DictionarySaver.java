package dictionary;

import java.io.IOException;

public abstract class DictionarySaver {
	private DictionaryHashMap dictionary;
	private String filename;
	
	public abstract void save() throws IOException;

}
