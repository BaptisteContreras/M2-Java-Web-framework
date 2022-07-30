package fr.univlyon1.tiw1.framework.loader;

public class ParamConfig {

    String name;

    String value;

    public ParamConfig() {

    }

    public ParamConfig(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
