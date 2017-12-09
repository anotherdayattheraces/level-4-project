package entityRetrieval.core;

import java.util.ArrayList;
import java.util.Collections;
import entityRetrieval.core.Entity;;

public class ResultSet {
	public ArrayList<Pair<Entity,Integer>> resultSet;
	public int size;
	
	public ResultSet(){
		this.resultSet = new ArrayList<Pair<Entity,Integer>>();
		this.size=0;
	}
	
	public ResultSet(ArrayList<Pair<Entity,Integer>> resultSet){
		this.resultSet = resultSet;
		this.size=resultSet.size();
	}
	
	public ArrayList<Pair<Entity,Integer>> getResultSet(){
		return this.resultSet;
	}
	public ResultSet sort(){
		Collections.sort(this.resultSet, Pair.EntityMentionsComparator);
		return this;
	}

}
