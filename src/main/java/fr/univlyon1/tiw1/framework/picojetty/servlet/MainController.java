package fr.univlyon1.tiw1.framework.picojetty.servlet;

import fr.univlyon1.tiw1.framework.loader.ApplicationConfig;
import fr.univlyon1.tiw1.framework.loader.ApplicationConfigLoader;
import fr.univlyon1.tiw1.framework.loader.ControllerConfig;
import fr.univlyon1.tiw1.framework.loader.RequestConfig;
import fr.univlyon1.tiw1.framework.picojetty.request.RequestExecutor;
import fr.univlyon1.tiw1.framework.picojetty.servers.JettyServer;
import fr.univlyon1.tiw1.framework.serveur.Serveur;
import fr.univlyon1.tiw1.framework.serveur.ServeurImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebServlet(name="mainController", urlPatterns = "/*")
public class MainController extends HttpServlet {

    private static final ApplicationConfig applicationConfig = ApplicationConfigLoader.load();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {

        Optional<ControllerConfig> controllerConfig = applicationConfig.getControllerWithPath(
                req.getRequestURI(), req.getMethod());

        if (controllerConfig.isPresent()) {

            Optional<RequestConfig> optRequestConfig = controllerConfig
                    .get().getRequestConfig(req.getMethod().toLowerCase());

            RequestExecutor.exec(JettyServer.serveur, optRequestConfig.get(), req, resp);
        } else {
            resp.setStatus(404);
        }
    }
}