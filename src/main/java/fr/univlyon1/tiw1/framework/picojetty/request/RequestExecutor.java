package fr.univlyon1.tiw1.framework.picojetty.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import fr.univlyon1.tiw1.framework.loader.RequestConfig;
import fr.univlyon1.tiw1.framework.serveur.Serveur;
import fr.univlyon1.tiw1.framework.vue.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestExecutor {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static Map<String,String> getBody(HttpServletRequest req) {
        try {
            MapType mapType = objectMapper.getTypeFactory()
                    .constructMapType(HashMap.class, String.class, String.class);

            String payload = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            return objectMapper.readValue(payload, mapType);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    public static void exec(Serveur serveur, RequestConfig requestConfig, HttpServletRequest req, HttpServletResponse resp) {
        try {

            ResponseEntity<?> response = serveur.processRequest(
                    requestConfig.getController() + "@" + requestConfig.getMethod(),
                    getBody(req)
            );

            resp.setStatus(response.getStatus());
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(objectMapper.writeValueAsString(response.getBody()));
        } catch (Exception e) {
            resp.setStatus(500);
        }

    }
}
