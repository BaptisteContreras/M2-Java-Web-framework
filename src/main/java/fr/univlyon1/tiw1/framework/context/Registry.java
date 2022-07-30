package fr.univlyon1.tiw1.framework.context;

import org.picocontainer.Parameter;

import java.util.Map;

public interface Registry {

    void bind(String name, Object object);

    void bind(Object object);

    void bind(Object name, Object object, Parameter... parameters);

    void rebind(String name, Object object);

    void rebind(Object name, Object object, Parameter... parameters);

    Object lookup(Object name);

    void unbind(Object name);

    void start();

    void setParent(Registry parent);


    void setChild(Registry child, Object payload);

    Registry getParent();

    Map<String, Registry> getChildren();

    void setName(String name);

    String getName();

    Registry resolveLocaly(String path);

    void stop();

    void dispose();
}
