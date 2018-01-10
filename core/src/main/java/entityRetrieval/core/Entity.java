package entityRetrieval.core;

public class Entity {
	private String name;
	private String id;
	
	public Entity(String name, String id){
		this.name = name;
		this.id=id;
	}
	public Entity(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public String getId(){
		return this.id;
	}
	
}
