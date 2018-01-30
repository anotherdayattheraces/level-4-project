package entityRetrieval.core;

import java.util.ArrayList;

public class TopicRun {
	private String topicName;
	private int topicNumber;
	private ArrayList<Entity> returnedEntities;
	
	public TopicRun(String topicName,int topicNumber, ArrayList<Entity> returnedEntities){
		this.topicName=topicName;
		this.topicNumber=topicNumber;
		this.returnedEntities=returnedEntities;
	}
	public String getTopicName(){
		return this.topicName;
	}
	public ArrayList<Entity> getTopicEntities(){
		return this.returnedEntities;
	}
	public int getTopicChoice(){
		return this.topicNumber;
	}
	
}
