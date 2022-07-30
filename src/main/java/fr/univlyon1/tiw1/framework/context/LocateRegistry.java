package fr.univlyon1.tiw1.framework.context;

import fr.univlyon1.tiw1.framework.annotations.COMPONENT_TYPE;
import fr.univlyon1.tiw1.framework.exception.ReferenceException;
import fr.univlyon1.tiw1.framework.loader.LazyLifeCycleImpl;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.behaviors.Caching;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final public class LocateRegistry {

    Map<String, AbstractContext> registryMap;
    Registry root;

    public LocateRegistry() {
        registryMap = new HashMap<>();
    }

    public void registerContext(String contextName, AbstractContext context, boolean isRoot){
        registryMap.put(contextName, context);
        context.setName(contextName);
        context.bind(LocateRegistry.class, this);
        if (isRoot){
            root = context;
        }
    }

    public Object getReference(String registryRequest) throws ReferenceException {
        Matcher m = Pattern.compile("(.*)/(.*)").matcher(registryRequest);
        if (m.matches()) {
            return resolveContext(registryRequest).lookup(extractNameFromPath(registryRequest));
        } else {
            throw new ReferenceException();
        }
    }

    public Object getReference(String registryRequest, PropertyChangeListener pcl) throws ReferenceException {
        Matcher m = Pattern.compile("(.*)/(.*)").matcher(registryRequest);
        if (m.matches()) {
            AbstractContext abstractContext = (AbstractContext) resolveContext(registryRequest);
            if (abstractContext != null) {
                abstractContext.addPropertyChangeListener(registryRequest, pcl);
                return abstractContext.lookup(m.group(2));
            }
        }
        throw new ReferenceException();
    }

    public Object getComponentReference(String registryRequest, PropertyChangeListener pcl, COMPONENT_TYPE component_type) throws ReferenceException {
        Object object = null;
        switch (component_type){
            case CONTROLLER:
                object = getReferenceContextController(registryRequest, pcl);
                if (object != null) {
                    return object;
                }
            case RESOURCE:
                object = getReferenceContextResource(registryRequest, pcl);
                if (object != null) {
                    return object;
                }
            case SERVICE:
                object = getReferenceContextService(registryRequest, pcl);
                if (object != null) {
                    return object;
                }
            case PERSISTENCE:
                object = getReferenceContextDAO(registryRequest, pcl);
                if (object != null) {
                    return object;
                }
            default: return getReferenceContextGlobal(registryRequest, pcl);
        }
    }



    private String extractNameFromPath(String path) throws ReferenceException {
        String[] split = path.split("/");

        return split[split.length - 1];
    }
    public void bind(String path, Object what) {
        bind(path,what);
    }

    public void bind(String path, Object what, Parameter ...parameters) throws ReferenceException {
        resolveContext(path).bind(extractNameFromPath(path), what, parameters);
    }

    public void unbind(String path) throws ReferenceException {
        resolveContext(path).unbind(extractNameFromPath(path));
    }

    public void start(){
        root.start();
    }

    public void stop(){
        root.stop();
    }

    public void dispose(){
        root.dispose();
    }

    public void rebind(String path, Object what) throws ReferenceException {
        resolveContext(path).rebind(extractNameFromPath(path), what);
    }

    public void rebind(String path, Object what, Parameter... parameters) throws ReferenceException {
        resolveContext(path).rebind(extractNameFromPath(path), what, parameters);
    }

    public ContainerContext addNewContext(String name, String parent){
        ContainerContext ctx = new ContainerContext(new DefaultPicoContainer(new Caching(),new LazyLifeCycleImpl(), null));
        ContainerContext p = (ContainerContext) resolveContext(parent);
        registerContext(name, ctx, false);
        ctx.setParent(p);

        return ctx;
    }

    private Registry resolveContext(String path){
        String[] pathSplited = path.split("/");

        return registryMap.get(pathSplited[0]).resolveLocaly(Arrays.asList(pathSplited).stream().skip(1).collect(Collectors.joining("/")));
    }

    public Object getReferenceContextGlobal(String path) throws ReferenceException {
        return getReference("globalContext/" + path);
    }

    public Object getReferenceContextGlobal(String path, PropertyChangeListener pcl) throws ReferenceException {
        return getReference("globalContext/" + path, pcl);
    }

    public Object getReferenceContextController(String path) throws ReferenceException {
        return getReferenceContextGlobal("controller-components/" + path);
    }

    public Object getReferenceContextController(String path, PropertyChangeListener pcl) throws ReferenceException {
        return getReferenceContextGlobal("controller-components/" + path, pcl);
    }

    public Object getReferenceContextResource(String path) throws ReferenceException {
        return getReferenceContextController("resource-components/" + path);
    }

    public Object getReferenceContextResource(String path, PropertyChangeListener pcl) throws ReferenceException {
        return getReferenceContextController("resource-components/" + path, pcl);
    }

    public Object getReferenceContextService(String path) throws ReferenceException {
        return getReferenceContextResource("service-components/" +path);
    }

    public Object getReferenceContextService(String path, PropertyChangeListener pcl) throws ReferenceException {
        return getReferenceContextResource("service-components/" + path, pcl);
    }

    public Object getReferenceContextDAO(String path) throws ReferenceException {
        return getReferenceContextService("dao-components/" + path);
    }

    public Object getReferenceContextDAO(String path, PropertyChangeListener pcl) throws ReferenceException {
        return getReferenceContextService("dao-components/" + path, pcl);
    }
}
