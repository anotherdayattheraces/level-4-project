package evaluation;

import java.util.ArrayList;
import java.util.Random;

public class SearchEvaluator {
	private ArrayList<Topic> topics;
	private Topic topic;
	private ArrayList<Long> docIds;
	
	public SearchEvaluator(ArrayList<Topic> topics){
		this.topics=topics;
		this.docIds = new ArrayList<Long>();
		int numTopics=topics.size();
		Random r = new Random();
		int topicChoice = r.nextInt(numTopics-1);
		this.topic = topics.get(topicChoice);
		for(String pmid:this.topic.getPMIDS()){
			Long id = Long.valueOf(pmid).longValue();
			System.out.println(id);
			this.docIds.add(id);
			
		}
		System.out.println("Chosen topic #"+topicChoice+" with disease: "+this.topic.getDisease());
	}
	
	
	public void evaluate(){
		
	}
	

}
