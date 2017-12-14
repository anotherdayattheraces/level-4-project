package entityRetrieval.core;

public class SnomedEntity extends Entity{
	private String conceptID;
	private String name;
	private String type;

	public SnomedEntity(String name) {
		super(name);
	}
	
	public SnomedEntity(String name,String conceptID, String type) {
		super(name);
		this.conceptID=conceptID;
		this.type=type;
	}

}
