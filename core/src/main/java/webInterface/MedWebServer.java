package webInterface;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.lemurproject.galago.tupleflow.Utility;
import org.lemurproject.galago.tupleflow.web.WebHandler;
import org.lemurproject.galago.tupleflow.web.WebServerException;
import org.lemurproject.galago.utility.Parameters;
import org.lemurproject.galago.utility.json.JSONUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MedWebServer{

	private final Server server;

	  public MedWebServer(Server server) {
	    this.server = server;
	  }
	  
	  public void stop() throws WebServerException {
		    try {
		      this.server.stop();
		    } catch (Exception ex) {
		      throw new WebServerException(ex);
		    }
		  }

		  public void join() throws WebServerException {
		    try {
		      this.server.join();
		    } catch (InterruptedException e) {
		      throw new WebServerException(e);
		    }
		  }
		  
		  
	public static MedWebServer start(int port, WebHandler handler) throws WebServerException {
	    Server server = new Server(port);
	    server.setHandler(new JettyHandler(handler));
	    try {
	      server.start();
	    } catch (Exception ex) {
	      throw new WebServerException(ex);
	    }

	    return new MedWebServer(server);
	  }
	
	public static MedWebServer start(Parameters p, WebHandler handler) throws WebServerException {
	    int port = p.get("port", 0);
	    if (port == 0) {
	      try {
	        port = Utility.getFreePort();
	      } catch (IOException e) {
	        throw new WebServerException(e);
	      }
	    }
	    return start(port, handler);
	  }
	public String getURL() {
	    return server.getURI().toString();
	  }
	 public int getPort() {
		    return server.getURI().getPort();
		  }
	
	
	public static final class JettyHandler extends AbstractHandler {
	    public final WebHandler handler;

	    public JettyHandler(WebHandler handler) {
	      this.handler = handler;
	    }

	    
	    public void handle(String s, Request jettyRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	      jettyRequest.setHandled(true);
	      try {
	        handler.handle(request, response);
	      } catch (Exception e) {
	        if(!response.isCommitted()) {
	          response.sendError(501, e.getMessage());
	        }
	      }
	    }
	  }
	
	public static Parameters parseGetParameters(HttpServletRequest req) {
	    Parameters request = Parameters.create();

	    Enumeration args = req.getParameterNames();
	    while(args.hasMoreElements()) {
	      String arg = (String) args.nextElement();
	      List<Object> vals = new ArrayList<Object>();
	      for (String x : req.getParameterValues(arg)) {
	        vals.add(JSONUtil.parseString(x));
	      }
	      request.put(arg, vals.size() == 1 ? vals.get(0) : vals);
	    }

	    return request;
	  }
}
