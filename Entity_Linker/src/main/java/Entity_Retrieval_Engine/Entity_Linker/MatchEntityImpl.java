package Entity_Retrieval_Engine.Entity_Linker;

import java.util.ArrayList;

public class MatchEntityImpl implements MatchEntity{

	public Boolean lookup(String entity, ArrayList<String> list) {
		for(String s: list){
			if(s==entity)
				return true;
		}
		return false;
	}

}
