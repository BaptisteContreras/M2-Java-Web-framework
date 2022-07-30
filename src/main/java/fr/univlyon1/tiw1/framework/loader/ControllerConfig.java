package fr.univlyon1.tiw1.framework.loader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerConfig extends ComponentConfig {

    @JsonProperty("path")
    String path;

    @JsonProperty("methods")
    List<RequestConfig> methods;

    public ControllerConfig() {
    }

    public ControllerConfig(String className, String implementation,
                            String path, List<RequestConfig> methods) {
        super(className, implementation);
        this.path = path;
        this.methods = methods;
    }

    public String getPath() {
        return path;
    }

    public Optional<RequestConfig> getRequestConfig(String type) {
        return methods.stream().filter(
                m -> m.getMethod().equals(type.toLowerCase())
        ).findFirst();
    }
}
