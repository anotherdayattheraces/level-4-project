package webInterface;



import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class WebServer {
	public static int port = 9000;
	public static void main(String[] args) {
	final ServerSocket server = new ServerSocket(8080);
		
		
		
		
		
		
		
		
		
		
		
		//		// start http server
//		SimpleHttpServer httpServer = new SimpleHttpServer();
//		httpServer.Start(port);
		
		// start https server
		SimpleHttpsServer httpsServer = new SimpleHttpsServer();
		httpsServer.Start(port);
		
//		System.out.println(System.getProperty("user.dir"));
//		System.out.println(Main.class.getClassLoader().getResource("").getPath());
		
	}

}
