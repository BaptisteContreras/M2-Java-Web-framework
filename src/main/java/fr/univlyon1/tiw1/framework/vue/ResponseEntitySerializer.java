package fr.univlyon1.tiw1.framework.vue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ResponseEntitySerializer {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private ResponseEntitySerializer() {
    }

    private static <T> String serialize(T body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public static <T> String renderView(ResponseEntity<T> responseEntity) {
        if (responseEntity.getBody() != null) {
            return "{ \"status\": " + responseEntity.getStatus() + ", \"payload\": " +
                    serialize(responseEntity.getBody()) + "}";
        } else {
            return "{ \"status\": " + responseEntity.getStatus() + "}";
        }
    }

}
