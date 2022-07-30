package fr.univlyon1.tiw1.framework.picojetty.servers;

import fr.univlyon1.tiw1.framework.serveur.Serveur;
import fr.univlyon1.tiw1.framework.serveur.ServeurImpl;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class JettyServer {
    private static Server server;
    public final static Serveur serveur = new ServeurImpl();

    public static void start(ServletHandler servletHandler) throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});
        server.setHandler(servletHandler);
        server.start();
    }

    public static void stop() throws Exception {
        serveur.dispose();
        server.stop();;
    }
}
