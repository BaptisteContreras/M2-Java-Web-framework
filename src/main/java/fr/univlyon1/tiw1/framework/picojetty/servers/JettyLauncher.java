package fr.univlyon1.tiw1.framework.picojetty.servers;

import fr.univlyon1.tiw1.framework.picojetty.servlet.MainController;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyLauncher {
    private static final Logger logger = LoggerFactory.getLogger(JettyLauncher.class);

    public static void run() {
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(MainController.class, "/");

        try {
            JettyServer.start(servletHandler);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }
}
