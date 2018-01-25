package evaluation;

public class EntityMatcher {
	
	public static String removeBracketDescription(String entityWithBrackets){
		if(!entityWithBrackets.contains("(")){
			return entityWithBrackets;
		}
		return entityWithBrackets.substring(entityWithBrackets.indexOf("(")-1);
	}
	public static String lastTwoWords(String name){
		String[] split = name.split(" ");
		return split[split.length-2].substring(0, 1).toUpperCase()+split[split.length-2].substring(1)+" "+split[split.length-1];
	}

}
