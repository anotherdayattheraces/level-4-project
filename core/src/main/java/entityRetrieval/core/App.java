package entityRetrieval.core;


import java.util.ArrayList;
import java.util.HashMap;
import evaluation.SearchEvaluator;




public class App {
	
	
	
    public static void main( String[] args ) throws Exception{
    	HashMap<String, ArrayList<String>> appFunctions = App.initializeFunctions();
    	App.run(args, appFunctions);
    }
    
    public static void run(String[] args, HashMap<String, ArrayList<String>> appFunctions) throws Exception{
    	String fn = "help";
    	if(args.length>0 && appFunctions.containsKey(args[0])){
    		fn = args[0];
    	}
    	if(fn.equals("search")){
    		SingleQuerySearch sqs = new SingleQuerySearch(args[1]);
    		sqs.search();
    	}
    	else if(fn.equals("evaluate")){
    		SearchEvaluator se = new SearchEvaluator();
    		se.evaluate();
    	}
    	
    }
    private static HashMap<String, ArrayList<String>> initializeFunctions(){
    	HashMap<String, ArrayList<String>> appFunctions = new HashMap<String, ArrayList<String>>();
    	appFunctions.put("search", new ArrayList<String>());
    	appFunctions.put("evaluate", new ArrayList<String>());
		return appFunctions;

    }
	    
}

