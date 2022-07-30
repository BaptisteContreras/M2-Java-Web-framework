package fr.univlyon1.tiw1.framework.context;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;

import java.util.*;
import java.util.stream.Collectors;


public class ContainerContext extends AbstractContext {

    protected final MutablePicoContainer container;

    protected Registry parent;
    protected Map<String, Registry> children;
    protected String name;

    public ContainerContext(MutablePicoContainer container) {
        this.container = container;
        children = new HashMap<>();
    }

    @Override
    public void bind(String name, Object object) {
        container.addComponent(name, object);
    }

    @Override
    public void bind(Object object) {
        container.addComponent(object);
    }

    @Override
    public void bind(Object name, Object object, Parameter... parameters) {
        container.addComponent(name, object, parameters);
    }

    @Override
    public void rebind(String name, Object object) {
        Object oldValue = lookup(name);
        if (oldValue != null) {
            unbind(name);
        }
        bind(name, object);
        updatePropertyChangeListener(getName(), oldValue, object);
    }

    @Override
    public void rebind(Object name, Object object, Parameter... parameters) {
        if (lookup(name) != null) {
            unbind(name);
        }
        bind(name, object, parameters);
    }

    @Override
    public Object lookup(Object name) {
        return container.getComponent(name);
    }

    @Override
    public void unbind(Object name) {
        container.removeComponent(name);
    }

    @Override
    public void start() {
        container.start();
    }

    @Override
    public void setParent(Registry parent) {
        if (parent instanceof ContainerContext){
            parent.setChild(this, container);
            this.parent = parent;
        }
    }


    @Override
    public void setChild(Registry child, Object payload) {
        if (payload instanceof PicoContainer){
            container.addChildContainer((PicoContainer) payload);
            children.put(child.getName(), child);
        }
    }

    @Override
    public Registry getParent() {
        return parent;
    }

    @Override
    public Map<String, Registry> getChildren() {
        return children;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Registry resolveLocaly(String path) {
        String[] pathSplited = path.split("/");
        Registry next = getChildren().get(pathSplited[0]);
        if (null != next ){
            return next
                    .resolveLocaly(Arrays.asList(pathSplited).stream().skip(1).collect(Collectors.joining("/")));
        }

        return this;
    }

    @Override
    public void stop() {
        container.stop();
    }

    @Override
    public void dispose() {
        container.dispose();
    }
}
