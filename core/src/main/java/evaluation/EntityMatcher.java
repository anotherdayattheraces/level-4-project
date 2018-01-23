package evaluation;

public class EntityMatcher {
	
	public static String removeBracketDescription(String entityWithBrackets){
		if(!entityWithBrackets.contains("(")){
			return entityWithBrackets;
		}
		return entityWithBrackets.substring(entityWithBrackets.indexOf("(")-1);
	}

}
