package entityRetrieval.core;

import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lemurproject.galago.core.tools.Search;
import org.lemurproject.galago.core.tools.SearchWebHandler;

import webInterface.MedSearchWebHandler;
import webInterface.MedStreamContextHandler;
import webInterface.MedWebServer;
import org.lemurproject.galago.tupleflow.web.WebHandler;
import org.lemurproject.galago.tupleflow.web.WebServerException;
import org.lemurproject.galago.utility.Parameters;

public class SearchFn {
	
	public void run(PrintStream output) throws Exception{
		Parameters p = Parameters.create();
		p.set("index", "C:/Work/Project/samples/Unprocessed_Index");
		
		MedSearch medSearch = new MedSearch(p);
		
		final MedStreamContextHandler streamHandler = new MedStreamContextHandler(medSearch);
		final MedSearchWebHandler searchHandler = new MedSearchWebHandler(medSearch);
		
		MedWebServer server = MedWebServer.start(p, new WebHandler(){
			
		public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	        if(request.getPathInfo().equals("/ready")) {
	          response.setStatus(200);
	        } else if(request.getPathInfo().equals("/stream")) {
	          streamHandler.handle(request, response);
	        } else {
	          searchHandler.handle(request, response);
	        }
	      }
	    });

	    output.println("Server: "+server.getURL());
	  }
	
	}

