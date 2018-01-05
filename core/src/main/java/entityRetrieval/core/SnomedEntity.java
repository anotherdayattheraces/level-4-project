package entityRetrieval.core;

public class SnomedEntity extends Entity{
	private String conceptID;
	private String type;
	private int score;

	public SnomedEntity(String name) {
		super(name);
	}
	
	public SnomedEntity(String name,String conceptID, String type, int score) {
		super(name);
		this.conceptID=conceptID;
		this.type=type;
		this.score=score;
	}
	public SnomedEntity(String name,String conceptID, int score) {
		super(name);
		this.conceptID=conceptID;
		this.score=score;
	}
	public void addScore(int score){
		this.score+=score;
	}
	public String getName(){
		return super.getName();
	}
	public String getID(){
		return this.conceptID;
	}
	public String getType(){
		return this.type;
	}

}
