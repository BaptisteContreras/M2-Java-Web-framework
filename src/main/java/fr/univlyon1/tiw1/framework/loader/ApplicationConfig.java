package fr.univlyon1.tiw1.framework.loader;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.univlyon1.tiw1.framework.annotations.COMPONENT_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicationConfig {

    @JsonProperty("name")
    private final String name = "";

    @JsonProperty("resource-components")
    private final List<ComponentConfig> resourceComponents = new ArrayList<>();

    @JsonProperty("service-components")
    private final List<ComponentConfig> serviceComponents = new ArrayList<>();

    @JsonProperty("dao-components")
    private final List<ComponentConfig> daoComponents = new ArrayList<>();

    @JsonProperty("controller-components")
    private final List<ControllerConfig> controllerComponents = new ArrayList<>();

    @JsonProperty("business-components")
    private final List<ComponentConfig> businessComponents = new ArrayList<>();

    public ApplicationConfig() {

    }

    public String getName() {
        return name;
    }

    public List<ComponentConfig> getResourceComponents() {
        return resourceComponents;
    }

    public List<ComponentConfig> getServiceComponents() {
        return serviceComponents;
    }

    public List<ComponentConfig> getDaoComponents() {
        return daoComponents;
    }

    public List<ComponentConfig> getBusinessComponents() {
        return businessComponents;
    }

    public List<ComponentConfig> getControllerComponents() {
        return controllerComponents.stream().map(
                e -> (ComponentConfig)e
        ).collect(Collectors.toList());
    }

    public void addComponent(ComponentConfig componentConfig, COMPONENT_TYPE component_type) {
        switch (component_type) {
            case CONTROLLER:
                controllerComponents.add((ControllerConfig)componentConfig);
                break;
            case SERVICE:
                serviceComponents.add(componentConfig);
                break;
            case PERSISTENCE:
                daoComponents.add(componentConfig);
                break;
            case RESOURCE:
                resourceComponents.add(componentConfig);
                break;
            case BUSINESS:
                businessComponents.add(componentConfig);
                break;
        }
    }

    public Optional<ControllerConfig> getControllerWithPath(String path, String type) {
        return controllerComponents.stream()
                .filter(e -> e.getPath().equals(path))
                .filter(e -> e.getRequestConfig(type).isPresent())
                .findFirst();
    }
}
