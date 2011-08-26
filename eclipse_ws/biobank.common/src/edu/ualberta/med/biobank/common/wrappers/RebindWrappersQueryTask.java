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
        @SuppressWarnings("unchecked")
        List<Object> newModels = (List<Object>) result.getObjectResult();

        updateWrappedObjects(newModels);
        updateSession();
    }

    private void updateWrappedObjects(List<Object> newModels) {
        for (int i = 0, n = wrappersToRebind.size(); i < n; i++) {
            ModelWrapper<?> wrapper = wrappersToRebind.get(i);

            setWrappedObject(wrapper, newModels.get(i));
            // TODO: only thing that may have changed is the id, notify
            // listeners?
            // TODO: direct properties may have changed, but not
            // associations (e.g. label if container rename) so should do
            // some sort of notification.

            // clear old values since they are no longer valid after an insert,
            // update, or delete
            wrapper.getElementTracker().clear();
        }
    }

    /**
     * Need to clean up the model wrapper map because after coming back from the
     * server, it will contain model objects that are no longer referenced by
     * the wrappers. Remove all old model object keys and replace with the new
     * model objects.
     * 
     * @param newModels
     */
    private void updateSession() {
        for (int i = 0, n = wrappersToRebind.size(); i < n; i++) {
            ModelWrapper<?> wrapper = wrappersToRebind.get(i);
            Object oldModel = oldModels.get(i);

            // TODO: note that uni-directional references INTO these model
            // objects will not be updated, they will still reference a now
            // detached model object graph. The wrappers could inform an object
            // when it calls set on it, then it could notify the object that has
            // a reference to it when it is updated, then a rebind could be
            // done? For example, if persist was called on an AddressWrapper
            // object (but not the CenterWrapper object that has it) then the
            // old center model object would contain a reference to the old
            // address model object, but the CenterWrapper would contain a
            // reference to the same AddressWrapper, which was rebound to a new
            // address model object.

            wrapper.session.remove(oldModel);
            wrapper.session.add(wrapper);
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