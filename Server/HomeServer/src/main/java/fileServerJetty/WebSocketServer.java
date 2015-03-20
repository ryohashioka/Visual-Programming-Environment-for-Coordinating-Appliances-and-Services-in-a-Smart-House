package fileServerJetty;

import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

public class WebSocketServer extends Server {

	private SelectChannelConnector connector;
	private WebSocketHandler wsHandler;
	private ResourceHandler resHandler;

	private int idCounter;

	public WebSocketServer(int port, String rootDirPath, final MainWindow m, final Statement smt, final java.sql.Connection sqlcon) {
		idCounter = 0;

		connector = new SelectChannelConnector();
		connector.setPort(port);
		addConnector(connector);

		wsHandler = new WebSocketHandler() {
			@Override
			public WebSocket doWebSocketConnect(HttpServletRequest request,
					String protocol) {
				idCounter++;
				System.out.printf("Protocol:%s\n", protocol);
				return new MyWebSocket(idCounter, m, smt, sqlcon);
			}
		};

		resHandler = new ResourceHandler();
		resHandler.setDirectoriesListed(true);
		resHandler.setResourceBase(rootDirPath);

		wsHandler.setHandler(resHandler);
		setHandler(wsHandler);
	}

//	private String usage() {
//		String msg = "";
//
//		msg += "java -cp CLASSPATH " + WebSocketServer.class + " [ OPTIONS ]\n";
//		msg += "  -p|--port PORT    (default 8080)\n";
//		msg += "  -d|--docroot file (default 'src/main/resources/html')\n";
//
//		return msg;
//	}
}