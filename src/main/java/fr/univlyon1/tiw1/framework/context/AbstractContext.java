package fr.univlyon1.tiw1.framework.context;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

abstract class AbstractContext implements Registry {

    private final PropertyChangeSupport support;

    protected AbstractContext() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(String name, PropertyChangeListener pcl) {
        support.addPropertyChangeListener(name, pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    protected void updatePropertyChangeListener(String name, Object oldValue, Object newValue) {
        support.firePropertyChange(name, oldValue, newValue);
    }
}
