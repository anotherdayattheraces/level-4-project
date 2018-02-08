package webInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lemurproject.galago.core.tools.StreamContextHandler;
import org.lemurproject.galago.tupleflow.web.WebHandler;
import entityRetrieval.core.MedSearch;
import java.lang.reflect.Method;


public class MedStreamContextHandler implements WebHandler{
	
	private final MedSearch search;
	
	public MedStreamContextHandler(MedSearch search) {
		this.search = search;
	}

	public void handle(HttpServletRequest request,
	          HttpServletResponse response) throws IOException, ServletException {
	    try {
	      // Recover method
	      ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
	      String methodName = ois.readUTF();

	      // Get arguments
	      int numArgs = (int) ois.readShort();
	      Class argTypes[] = new Class[numArgs];

	      for (int i = 0; i < numArgs; i++) {
	        argTypes[i] = (Class) ois.readObject();
	      }

	      Object[] arguments = new Object[numArgs];
	      for (int i = 0; i < numArgs; i++) {
	        arguments[i] = ois.readObject();
	      }

	      ois.close();
	      
	      // NOW we can get the method itself and invoke it on our retrieval object
	      // with the extracted arguments
	        Method m = null;
	        for (Method method : search.getRetrieval().getClass().getMethods()) {
	            if (methodName.equals(method.getName()) && method.getParameterTypes().length == argTypes.length) {
	                m = method;
	                for (int i = 0; i < argTypes.length; i++) {
	                    if (!method.getParameterTypes()[i].isAssignableFrom(argTypes[i])) {
	                        m = null;
	                    }
	                }
	                if (m != null) {
	                    break;
	                }
	            }
	        }
	        Object result = m.invoke(search.getRetrieval(), arguments);

	      // Finally send back our result
	      ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
	      oos.writeObject(result);
	      response.flushBuffer();
	    } catch (Exception e) {
	      
	      e.printStackTrace();
	      System.err.println(e.toString());
	      
	      throw new RuntimeException(e);
	    }
	  }
	}