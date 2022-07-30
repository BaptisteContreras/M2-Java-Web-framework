package fr.univlyon1.tiw1.framework.loader;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestConfig {

    @JsonProperty("method")
    private String method;

    @JsonProperty("function")
    private String function;

    @JsonProperty("controller")
    private String controller;

    public RequestConfig() {

    }

    public RequestConfig(String method, String function, String controller) {
        this.method = method;
        this.function = function;
        this.controller = controller;
    }

    public String getMethod() {
        return method;
    }

    public String getFunction() {
        return function;
    }

    public String getController() {
        return controller;
    }
}
