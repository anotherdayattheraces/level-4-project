package entityRetrieval.core;

public class SnomedEntity extends Entity{
	private String conceptID;
	private String type;

	public SnomedEntity(String name) {
		super(name);
	}
	
	public SnomedEntity(String name,String conceptID, String type) {
		super(name);
		this.conceptID=conceptID;
		this.type=type;
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
