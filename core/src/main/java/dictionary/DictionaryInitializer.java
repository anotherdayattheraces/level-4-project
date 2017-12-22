package dictionary;

import java.io.IOException;

public abstract class DictionaryInitializer {
	private String filepath;
	public abstract DictionaryHashMap initialize() throws IOException;

}
