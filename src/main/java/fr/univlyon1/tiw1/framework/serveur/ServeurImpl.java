package fr.univlyon1.tiw1.framework.serveur;

import fr.univlyon1.tiw1.framework.context.ContainerContext;
import fr.univlyon1.tiw1.framework.context.LocateRegistry;
import fr.univlyon1.tiw1.framework.exception.ReferenceException;
import fr.univlyon1.tiw1.framework.loader.*;
import fr.univlyon1.tiw1.framework.vue.ResponseEntity;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.behaviors.Caching;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServeurImpl implements Serveur {

    /**
     * Properties
     **/

    protected ContainerContext container;
    private LocateRegistry annuaire;


    Properties properties;

    // Constantes

    private final String NOM_ORGA = "WordLine";

    public ServeurImpl() {
        this(PropertiesLoader.load(), ApplicationConfigLoader.load());
    }

    public ServeurImpl(Properties properties, ApplicationConfig applicationConfig) {
        this.properties = properties;

        container = new ContainerContext(new DefaultPicoContainer(new Caching()));

        registerRegistry();
        try {
            register(applicationConfig);
            annuaire.start();
            annuaire.stop();
        } catch (ClassNotFoundException ignored) {

        }
    }

    private Optional<String> getFunctionControllerCommande(String commande) {
        java.util.regex.Matcher m = Pattern.compile(".*@(.*)").matcher(commande);
        if (m.matches()) {
            return Optional.of(m.group(1));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public ResponseEntity<?> processRequest(String commande, Map<String, String> parameters) {
        Optional<String> optNameController = getNameControllerCommande(commande);

        if (optNameController.isPresent()) {
            try {

                Object controller = annuaire.getReferenceContextController(
                         optNameController.get());

                Optional<String> optFunctionName = getFunctionControllerCommande(commande);
                if (optFunctionName.isPresent()) {
                    return (ResponseEntity<?>) controller.getClass().getMethod(optFunctionName.get(), Map.class).invoke(controller, parameters);
                } else {
                    return ResponseEntity.empty(404);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {

            } catch (ReferenceException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.empty(404);
    }

    public void bind(String path, Object what, Parameter ...parameters) throws ReferenceException {
        annuaire.bind(path, what, parameters);
    }
    public void rebind(String path, Object what, Parameter ...parameters) throws ReferenceException {
        annuaire.rebind(path, what, parameters);
    }

    @Override
    public Object lookup(String name) throws ReferenceException {
        return annuaire.getReference(name);
    }

    @Override
    public void dispose() {
        annuaire.dispose();
    }

    private void addComponentConfig(ComponentConfig componentConfig, ContainerContext context) throws ClassNotFoundException {
        Class<?> cl = Class.forName(componentConfig.getImplementation());
        context.bind(componentConfig.getClassName(), cl);
    }

    private void addComponentConfig(List<ComponentConfig> componentConfigList, String contextName, String parentContext) throws ClassNotFoundException {
        ContainerContext containerContext = annuaire.addNewContext(contextName, parentContext);
        for (ComponentConfig componentConfig : componentConfigList) {
            addComponentConfig(componentConfig, containerContext);
        }
    }

    @Override
    public void register(ApplicationConfig applicationConfig) throws ClassNotFoundException {
        container.bind("name", applicationConfig.getName());
        for (ComponentConfig componentConfig : applicationConfig.getBusinessComponents()) {
            addComponentConfig(componentConfig, container);
        }
        addComponentConfig(applicationConfig.getControllerComponents(), "controller-components", "globalContext");
        addComponentConfig(applicationConfig.getResourceComponents(), "resource-components", "globalContext/controller-components");
        addComponentConfig(applicationConfig.getServiceComponents(), "service-components", "globalContext/controller-components/resource-components");
        addComponentConfig(applicationConfig.getDaoComponents(), "dao-components", "globalContext/controller-components/resource-components/service-components");
        }

    @Override
    public void registerRegistry() {
        annuaire = new LocateRegistry();

        annuaire.registerContext("globalContext", container, true);

    }

    private Optional<String> getNameControllerCommande(String commande) {
        Matcher m = Pattern.compile("(.*)@.*").matcher(commande);
        if (m.matches()) {
            return Optional.of(m.group(1));
        } else {
            return Optional.empty();
        }
    }
}
