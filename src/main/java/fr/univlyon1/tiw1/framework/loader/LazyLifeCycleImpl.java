package fr.univlyon1.tiw1.framework.loader;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

public class LazyLifeCycleImpl extends StartableLifecycleStrategy {
    public LazyLifeCycleImpl() {
        super(new NullComponentMonitor());
    }

    @Override
    public boolean isLazy(ComponentAdapter<?> adapter) {
        return true;
    }
}
