package fr.univlyon1.tiw1.framework.loader;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ComponentConfig {

    @JsonProperty("class-name")
    String className;

    @JsonProperty("implementation")
    String implementation;

    List<ParamConfig> params = new ArrayList<>();

    public ComponentConfig() {
    }

    public ComponentConfig(String className, String implementation) {
        this.className = className;
        this.implementation = implementation;
    }

    public String getClassName() {
        return className;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setParams(List<ParamConfig> params) {
        this.params = params;
    }
}
