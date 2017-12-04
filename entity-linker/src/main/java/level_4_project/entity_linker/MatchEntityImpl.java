package level_4_project.entity_linker;

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
