package webInterface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebHandler {
	
	  void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
