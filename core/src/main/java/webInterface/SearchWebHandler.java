package webInterface;

import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.lemurproject.galago.core.parse.Document.DocumentComponents;

public class SearchWebHandler implements WebHandler{

	public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getParameterMap();
		 String identifier = request.getParameter("identifier");
	     identifier = URLDecoder.decode(identifier, "UTF-8");
	     DocumentComponents p = new DocumentComponents(true, true, false);
	     //Document document = Search.getDocument(identifier, p);
	     response.setContentType("text/html; charset=UTF-8");
	     String raw = request.getParameter("raw");
	     boolean doPre = raw != null && raw.isEmpty() || Boolean.parseBoolean(raw);
	}

}
