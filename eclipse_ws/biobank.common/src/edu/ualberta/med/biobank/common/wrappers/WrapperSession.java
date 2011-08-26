package edu.ualberta.med.biobank.common.wrappers;

import java.util.HashMap;

import edu.ualberta.med.biobank.common.wrappers.util.ProxyUtil;

/**
 * A registry of wrapped model objects to the {@link ModelWrapper} that wrap
 * them. Used so that when get is called on a model object graph to expand the
 * graph (by lazily loading more objects from the database), if a model object
 * already exists in the session, then it is used instead of what the database
 * returned.
 * 
 * @author jferland
 * 
 */
public class WrapperSession {
    // HashMap instead of IdentityHashMap because if the same model is read from
    // the database twice, we want to use the model we already have in memory
    // instead so there is only ever one copy of any object read from the
    // database in an object graph made by get-method calls. However, if an
    // object is set from a completely separate graph, this guarantee is no
    // longer made.
    private HashMap<Object, ModelWrapper<?>> map = new HashMap<Object, ModelWrapper<?>>();

    public WrapperSession(ModelWrapper<?> wrapper) {
        // TODO: if a wrapper already has a WrapperSession, then perhaps it
        // should be removed from its old value. This can happen on a reset or
        // reload or setWrappedObject. Note that this is really messed up
        // because wrappers will still have a reference to this wrapper, but the
        // model objects wont. I really don't like reset and reload.
        add(wrapper);
    }

    public void add(ModelWrapper<?> wrapper) {
        if (wrapper != null) {
            wrapper.session = this;
            map.put(new ModelKey(wrapper.wrappedObject), wrapper);
        }
    }

    public Object get(Object model) {
        return map.get(new ModelKey(model));
    }

    public Object remove(Object model) {
        return map.remove(new ModelKey(model));
    }

    /**
     * Necessary because model objects only equals() each other if
     * {@code getId()} is not null, otherwise they return false, even if they
     * are literally the exact same object in memory.
     * 
     * @author jferland
     * 
     */
    private static class ModelKey {
        private final Object object;

        public ModelKey(Object object) {
            // at some point it looked like multiple proxies exist for the same
            // model object, so work with the raw model object instead of the
            // possible proxy
            Object unproxied = ProxyUtil.convertProxyToObject(object);
            this.object = unproxied;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                + ((object == null) ? 0 : object.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ModelKey other = (ModelKey) obj;
            if (object == null) {
                if (other.object != null)
                    return false;
            } else if (!object.equals(other.object) && object != other.object)
                return false;
            return true;
        }
    }
}
