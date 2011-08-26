package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.actions.WrapperAction;
import edu.ualberta.med.biobank.common.wrappers.tasks.QueryTask;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

// TODO: should share the map/ list for ALL wrappers?
// TODO: should rebuild the modelWrapperMap since the model objects sent to
// the server that came back are now invalid?
public class RebindWrappersQueryTask implements QueryTask {
    private final ModelWrapper<?> wrapper;
    private final List<ModelWrapper<?>> wrappersToRebind = new ArrayList<ModelWrapper<?>>();
    private final List<Object> oldModels = new ArrayList<Object>();

    public RebindWrappersQueryTask(Set<ModelWrapper<?>> wrappers) {
        // arbitrarily pick the first wrapper to use for security checks
        this.wrapper = wrappers.iterator().next();
        init(wrappers);
    }

    @Override
    public SDKQuery getSDKQuery() {
        return new ReturnAction(wrapper, oldModels);
    }

    @Override
    public void afterExecute(SDKQueryResult result) {
        // TODO: If we reattach here, no need to do so in Delete or
        // Persist!! :-)
        @SuppressWarnings("unchecked")
        List<Object> newModels = (List<Object>) result.getObjectResult();

        updateWrappedObjects(newModels);
        updateWrapperMap(newModels);
    }

    private void updateWrappedObjects(List<Object> newModels) {
        for (int i = 0, n = wrappersToRebind.size(); i < n; i++) {
            setWrappedObject(wrappersToRebind.get(i), newModels.get(i));
            // TODO: only thing that may have changed is the id, notify
            // listeners?
            // TODO: direct properties may have changed, but not
            // associations (e.g. label if container rename) so should do
            // some sort of notification.
        }
    }

    private void updateWrapperMap(List<Object> newModels) {
        // TODO:
        // TODO: NEED NEED NEED to clean up the ModelWrapper.wrappersMap
        // because it will contain model objects (as keys) that no longer
        // exist.
        // TODO:
        // TODO: this is unnecessarily slow, mostly because almost all of
        // the given wrappers will share the same wrapeprsMap. Could be
        // optimized.
        for (ModelWrapper<?> wrapper : wrappersToRebind) {
            for (int i = 0, n = oldModels.size(); i < n; i++) {
            }
        }
    }

    private static <E> void setWrappedObject(ModelWrapper<E> wrapper,
        Object object) {
        wrapper.wrappedObject = wrapper.getWrappedClass().cast(object);
    }

    private void init(Collection<ModelWrapper<?>> wrappers) {
        Map<ModelWrapper<?>, Object> map = new IdentityHashMap<ModelWrapper<?>, Object>();

        for (ModelWrapper<?> wrapper : wrappers) {
            readCache(wrapper, map);
        }

        for (Entry<ModelWrapper<?>, Object> entry : map.entrySet()) {
            wrappersToRebind.add(entry.getKey());
            oldModels.add(entry.getValue());
        }
    }

    private void readCache(ModelWrapper<?> wrapper,
        Map<ModelWrapper<?>, Object> map) {
        if (!map.containsKey(wrapper)) {
            map.put(wrapper, wrapper.wrappedObject);
            for (Object o : wrapper.propertyCache.values()) {
                if (o instanceof Collection) {
                    for (Object e : (Collection<?>) o) {
                        if (e instanceof ModelWrapper<?>) {
                            readCache((ModelWrapper<?>) e, map);
                        }
                    }
                } else if (o instanceof ModelWrapper<?>) {
                    readCache((ModelWrapper<?>) o, map);
                }
            }
        }
    }

    /**
     * Simply returns the given action. This is useful because we can pass a
     * list of wrapped objects, then get "new" references back after
     * deserialization.
     * 
     * @author jferland
     * 
     */
    @SuppressWarnings("rawtypes")
    public static class ReturnAction extends WrapperAction {
        private static final long serialVersionUID = 1L;

        private Object result;

        @SuppressWarnings("unchecked")
        public ReturnAction(ModelWrapper<?> wrapper, Object result) {
            super(wrapper);
            this.result = result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public Object getResult() {
            return result;
        }

        @Override
        public Object doAction(Session session) throws BiobankSessionException {
            return result;
        }
    }
}