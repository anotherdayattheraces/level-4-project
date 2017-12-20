package entityRetrieval.core;

public class Entity {
	private String name;
	private String url;
	
	public Entity(String name, String url){
		this.name = name;
		this.url=url;
	}
	public Entity(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public String getUrl(){
		return this.url;
	}
	
}
