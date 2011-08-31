package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.security.Privilege;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListener;
import edu.ualberta.med.biobank.common.wrappers.loggers.LogAction;
import edu.ualberta.med.biobank.common.wrappers.loggers.LogGroup;
import edu.ualberta.med.biobank.common.wrappers.loggers.WrapperLogProvider;
import edu.ualberta.med.biobank.common.wrappers.util.WrapperUtil;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.aop.framework.Advised;

public abstract class ModelWrapper<E> implements Comparable<ModelWrapper<E>> {
    final Map<Property<?, ?>, Object> propertyCache = new HashMap<Property<?, ?>, Object>();

    protected WritableApplicationService appService;
    protected E wrappedObject;
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
        this);
    protected HashMap<String, Object> cache = new HashMap<String, Object>();

    private final List<WrapperListener> listeners = new ArrayList<WrapperListener>();
    private final ElementTracker<E> elementTracker = new ElementTracker<E>(this);
    private final ElementQueue<E> elementQueue = new ElementQueue<E>(this);
    private final WrapperCascader<E> cascader = new WrapperCascader<E>(this);
    private final WrapperChecker<E> preChecker = new WrapperChecker<E>(this);
    WrapperSession session;

    public ModelWrapper(WritableApplicationService appService, E wrappedObject) {
        this.appService = appService;
        this.wrappedObject = wrappedObject;

        this.session = new WrapperSession(this);
    }

    public ModelWrapper(WritableApplicationService appService) {
        this.appService = appService;
        try {
            this.wrappedObject = getNewObject();
        } catch (Exception e) {
            Class<E> classType = getWrappedClass();
            if (classType != null) {
                throw new RuntimeException(
                    "was not able to create new object of type "
                        + classType.getName(), e);
            } else {
                throw new RuntimeException("was not able to create new object",
                    e);
            }
        }

        this.session = new WrapperSession(this);
    }

    public E getWrappedObject() {
        return wrappedObject;
    }

    public abstract Property<Integer, ? super E> getIdProperty();

    public void setWrappedObject(E newWrappedObject) {
        E oldWrappedObject = wrappedObject;
        wrappedObject = newWrappedObject;

        clear();
        session = new WrapperSession(this);

        firePropertyChanges(oldWrappedObject, newWrappedObject);
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        List<Property<?, ? super E>> propertiesList = getProperties();
        if ((propertiesList == null) || (propertiesList.size() == 0)) {
            throw new RuntimeException("wrapper has not defined any properties");
        }

        for (Property<?, ? super E> property : propertiesList) {
            if (property.getPropertyChangeName().equals(propertyName)) {
                propertyChangeSupport.addPropertyChangeListener(propertyName,
                    listener);
                return;
            }
        }

        throw new RuntimeException("invalid property: " + propertyName);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public boolean isNew() {
        return (getId() == null);
    }

    public Integer getId() {
        return getIdProperty().get(wrappedObject);
    }

    protected void setId(Integer id) {
        getIdProperty().set(wrappedObject, id);
    }

    public WritableApplicationService getAppService() {
        return appService;
    }

    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }

    public void reload() throws Exception {
        clear();

        E oldWrappedObject = wrappedObject;

        if (!isNew()) {
            wrappedObject = getObjectFromDatabase();
            if (wrappedObject == null) {
                wrappedObject = getNewObject();
            }
        }

        session = new WrapperSession(this);

        firePropertyChanges(oldWrappedObject, wrappedObject);
    }

    /**
     * Add tasks to the given {@link TaskList} that will persist (i.e. insert or
     * update) the wrapped model object. The {@link TaskList}-s might also check
     * certain conditions on the client or server, as well as persist potential
     * dependent objects.
     * <p>
     * This method should be overridden as necessary to add to the
     * {@link TaskList} so the wrapped model object is properly persisted.
     * <p>
     * <strong>IMPORTANT.</strong> Checks can also be added to the
     * {@link TaskList}. However, in general, checks should be performed using
     * HQL <em>after</em> the object is persisted (and related objects are
     * cascaded) so that the database's state can be verified. This is opposed
     * to checking the in-memory model objects, since it cannot be easily
     * determined which will or have actually been persisted. Checks can be done
     * on the in-memory model objects (before persisting) before the database
     * throws an error, but it is often difficult to know what values to check.
     * 
     * @param tasks where to add the tasks
     */
    protected void addPersistTasks(TaskList tasks) {
        tasks.add(new PersistModelWrapperQueryTask<E>(this));
        tasks.add(check().stringLengths());
    }

    /**
     * Add tasks to the given {@link TaskList} that will delete the wrapped
     * model object. The {@link TaskList}-s might also check certain conditions
     * on the client or server, as well as persist potential dependent objects.
     * <p>
     * This method should be overridden as necessary to add to the
     * {@link TaskList} so the wrapped model object is properly deleted.
     * 
     * @param tasks where to add the tasks
     */
    protected void addDeleteTasks(TaskList tasks) {
        tasks.add(new DeleteModelWrapperQueryTask<E>(this));
    }

    /**
     * Same as addPersistTasks() excepts includes logging.
     * 
     * @param tasks
     */
    protected final void addPersistAndLogTasks(TaskList tasks) {
        addPersistTasks(tasks);
        log(LogAction.Type.PERSIST, tasks);
    }

    /**
     * Same as addDeleteTasks() excepts includes logging.
     * 
     * @param tasks
     */
    protected final void addDeleteAndLogTasks(TaskList tasks) {
        log(LogAction.Type.DELETE, tasks);
        addDeleteTasks(tasks);
    }

    private void log(LogAction.Type type, TaskList tasks) {
        WrapperLogProvider<E> logProvider = getLogProvider();
        if (logProvider != null) {
            LogGroup logGroup = tasks.getLogGroup();
            tasks.add(new LogAction<E>(type, this, logProvider, logGroup));
        }
    }

    /**
     * return the list of the different properties we want to notify when we
     * call firePropertyChanges
     */
    protected abstract List<Property<?, ? super E>> getProperties();

    /**
     * When retrieve the values from the database, need to fire the
     * modifications for the different objects contained in the wrapped object
     */
    private void firePropertyChanges(E oldWrappedObject, E newWrappedObject) {
        List<Property<?, ? super E>> properties = getProperties();

        if (oldWrappedObject == newWrappedObject) {
            return;
        }

        for (Property<?, ? super E> property : properties) {
            String propertyName = property.getPropertyChangeName();
            PropertyChangeListener[] listeners = propertyChangeSupport
                .getPropertyChangeListeners(propertyName);

            // if no one is listening to this property then do not send a change
            // as it may be expensive to determine the old and new values (ex:
            // lazily loading an association, such as, a Center's
            // specimenCollection).
            if (listeners.length == 0) {
                continue;
            }

            // don't fire a property change if the old model's property has not
            // even been initialized or loaded, as the old value is not
            // necessarily correct (if we lazily load it now, then it will
            if (!isInitialized(oldWrappedObject, property)) {
                continue;
            }

            Object oldValue = property.get(oldWrappedObject);
            Object newValue = property.get(newWrappedObject);

            // if the old and new property value are the same, do not send a
            // property change event
            if (oldValue == newValue
                || (oldValue != null && oldValue.equals(newValue))) {
                continue;
            }

            propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
        }
    }

    /**
     * using this wrapper id, retrieve the object from the database
     */
    protected E getObjectFromDatabase() throws BiobankException {
        Class<E> classType = null;
        Integer id = null;
        List<E> list = null;
        try {
            classType = getWrappedClass();
            Constructor<E> constructor = classType.getConstructor();
            Object instance = constructor.newInstance();
            Method setIdMethod = classType.getMethod("setId", Integer.class);
            id = getId();
            setIdMethod.invoke(instance, id);

            list = appService.search(classType, instance);
        } catch (Exception ex) {
            throw new BiobankException(ex);
        }
        if (list.size() == 0)
            return null;
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new BiobankException("Found " + list.size() + " objects of type "
            + classType.getName() + " with id=" + id);
    }

    public abstract Class<E> getWrappedClass();

    public void reset() throws Exception {
        clear();
        if (isNew()) {
            resetToNewObject();
        } else {
            reload();
        }
    }

    /**
     * even if this object was loaded form database, start form scratch (so
     * isNew = true)
     * 
     * @throws Exception
     */
    private void resetToNewObject() throws Exception {
        E oldWrappedObject = wrappedObject;
        wrappedObject = getNewObject();

        session = new WrapperSession(this);

        firePropertyChanges(oldWrappedObject, wrappedObject);
    }

    private static final String PROPERTY_COUNT_HQL = "SELECT COUNT(DISTINCT m.{0}) FROM {1} m WHERE m.id = ?";

    protected final <T> Long getPropertyCount(
        Property<Collection<T>, ? super E> property, boolean fast)
        throws BiobankQueryResultSizeException, ApplicationException {
        long count = 0;

        if (fast && !isInitialized(property)) {
            String prop = property.getName();
            String klazz = getWrappedClass().getName();
            String hql = MessageFormat.format(PROPERTY_COUNT_HQL, prop, klazz);

            List<Object> params = Arrays.asList(new Object[] { getId() });
            HQLCriteria criteria = new HQLCriteria(hql, params);

            count = getCountResult(appService, criteria);
        } else {
            Collection<T> collection = property.get(wrappedObject);
            count = collection != null ? collection.size() : 0;
        }

        return count;
    }

    /**
     * Override this method to supply default values.
     * 
     * @return
     * @throws Exception
     */
    protected E getNewObject() throws Exception {
        // TODO: could override in base classes with "new WrappedClass();" then
        // no exception would be thrown.
        Constructor<E> constructor = getWrappedClass().getConstructor();
        return constructor.newInstance();
    }

    private static final String CHECK_NO_DUPLICATES = "select count(o) from {0} "
        + "as o where {1}=? {2}";

    protected void checkNoDuplicates(Class<?> objectClass, String propertyName,
        String value, String errorName) throws ApplicationException,
        BiobankException {
        HQLCriteria c;
        final List<Object> params = new ArrayList<Object>();
        params.add(value);
        String equalsTest = "";
        if (!isNew()) {
            equalsTest = " and id <> ?";
            params.add(getId());
        }

        final String hqlString = MessageFormat.format(CHECK_NO_DUPLICATES,
            objectClass.getName(), propertyName, equalsTest);

        c = new HQLCriteria(hqlString, params);

        if (getCountResult(appService, c) > 0) {
            throw new DuplicateEntryException(errorName + " \"" + value
                + "\" already exists.");
        }
    }

    /**
     * The query should be a count query. The value returned is the result of
     * the count.
     */
    // TODO: move this to some Util class somewhere else
    public static Long getCountResult(WritableApplicationService appService,
        HQLCriteria criteria) throws BiobankQueryResultSizeException,
        ApplicationException {
        List<Long> results = appService.query(criteria);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return results.get(0);
    }

    /**
     * return true if integrity of this object is ok
     */
    public boolean checkIntegrity() {
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        Integer id = getId();
        Integer id2 = ((ModelWrapper<?>) object).getId();
        if (id == null && id2 == null) {
            return wrappedObject == ((ModelWrapper<?>) object).wrappedObject;
        }
        return id != null && id2 != null && id.equals(id2);
    }

    /**
     * Returns hash code for the primary key of the object
     **/
    @Override
    public int hashCode() {
        if (getId() != null)
            return getId().hashCode();
        return 0;
    }

    /**
     * If we want to reset internal fields when reload or reset is called (even
     * if the object is new). Please don't touch the wrapped object.
     */
    protected void resetInternalFields() {
        // default do nothing
    }

    // TODO: switch to using the properties?
    @Override
    public String toString() {
        Class<E> classType = getWrappedClass();
        if (classType != null) {
            StringBuffer sb = new StringBuffer();
            Method[] methods = classType.getMethods();
            for (Method method : methods) {
                String name = method.getName();
                Class<?> returnType = method.getReturnType();
                if (name.startsWith("get")
                    && !name.equals("getClass")
                    && (String.class.isAssignableFrom(returnType) || Number.class
                        .isAssignableFrom(returnType))) {
                    try {
                        Object res = method.invoke(wrappedObject,
                            (Object[]) null);
                        if (res != null) {
                            sb.append(name).append(":").append(res.toString())
                                .append("/");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Error in toString method",
                            e);
                    }
                }
            }
            return sb.toString();
        }
        return super.toString();
    }

    /**
     * return true if the user can view this object
     */
    public boolean canRead(User user) {
        return user.hasPrivilegeOnObject(Privilege.READ, getWrappedClass());
    }

    /**
     * return true if the user can edit this object
     */
    public boolean canUpdate(User user) {
        return user.hasPrivilegeOnObject(Privilege.UPDATE, getWrappedClass(),
            getSecuritySpecificCenters());
    }

    /**
     * return true if the user can delete this object
     */
    public boolean canDelete(User user) {
        return user.hasPrivilegeOnObject(Privilege.DELETE, getWrappedClass(),
            getSecuritySpecificCenters());
    }

    public void addWrapperListener(WrapperListener listener) {
        listeners.add(listener);
    }

    public void removeWrapperListener(WrapperListener listener) {
        listeners.add(listener);
    }

    void notifyListeners(WrapperEvent event) {
        // create a new list to avoid concurrent modification
        for (WrapperListener listener : new ArrayList<WrapperListener>(
            listeners)) {
            switch (event.getType()) {
            case UPDATE:
                listener.updated(event);
                break;
            case INSERT:
                listener.inserted(event);
                break;
            case DELETE:
                listener.deleted(event);
                break;
            }
        }
    }

    public void initObjectWith(ModelWrapper<E> otherWrapper)
        throws BiobankException {
        if (otherWrapper == null) {
            throw new BiobankCheckException(
                "Cannot init internal object with a null wrapper");
        }
        setWrappedObject(otherWrapper.wrappedObject);
    }

    public void logLookup(String center) throws Exception {
        ((BiobankApplicationService) appService).logActivity(getLogMessage(
            "select", center, getWrappedClass().getSimpleName() + " LOOKUP"));
    }

    public void logEdit(String site) throws Exception {
        if (!isNew()) {
            ((BiobankApplicationService) appService).logActivity(getLogMessage(
                "edit", site, getWrappedClass().getSimpleName() + " EDIT"));
        }
    }

    @SuppressWarnings("unused")
    protected final Log getLogMessage(String action, String site, String details)
        throws Exception {
        Log log = null;
        WrapperLogProvider<E> logProvider = getLogProvider();

        if (logProvider != null) {
            log = logProvider.getLog(getWrappedObject());

            log.setAction(action);
            log.setCenter(site);
            log.setType(getWrappedClass().getSimpleName());

            if (details != null) {
                if (log.getDetails() != null) {
                    details += log.getDetails();
                }
                log.setDetails(details);
            }
        }

        return log;
    }

    protected WrapperLogProvider<E> getLogProvider() {
        return null;
    }

    @Override
    public int compareTo(ModelWrapper<E> other) {
        return nullSafeComparator(getId(), other.getId());
    }

    /**
     * Compare two Comparable Object-s, even if either one is null.
     * 
     * @param <T>
     * @param one
     * @param two
     * @return
     */
    protected static <T extends Comparable<T>> int nullSafeComparator(
        final T one, final T two) {
        if (one == null ^ two == null) {
            return (one == null) ? -1 : 1;
        }

        if (one == null || two == null) {
            return 0;
        }
        return one.compareTo(two);
    }

    public static <W extends ModelWrapper<? extends R>, R, M> List<W> wrapModelCollection(
        WritableApplicationService appService,
        List<? extends R> modelCollection, Class<W> wrapperKlazz) {
        List<W> wrappers = new ArrayList<W>();

        if (modelCollection != null) {
            for (R element : modelCollection) {
                W wrapper = WrapperUtil.wrapModel(appService, element,
                    wrapperKlazz);
                wrappers.add(wrapper);
            }
        }
        return wrappers;
    }

    protected <W extends ModelWrapper<? extends M>, M> W getWrappedProperty(
        Property<M, ? super E> property, Class<W> klazz) {
        @SuppressWarnings("unchecked")
        W wrapper = (W) recallProperty(property);

        if (wrapper == null && !isPropertyCached(property)) {
            wrapper = wrapProperty(property, klazz);
            cacheProperty(property, wrapper);
        }

        return wrapper;
    }

    private <W extends ModelWrapper<? extends M>, M> W wrapProperty(
        Property<M, ? super E> property, Class<W> klazz) {
        M model = property.get(wrappedObject);

        @SuppressWarnings("unchecked")
        W wrapper = (W) session.get(model);

        if (wrapper == null) {
            wrapper = WrapperUtil.wrapModel(appService, model, klazz);
            session.add(wrapper);
        } else {
            // if we're replacing the value with one from the cache, then make
            // sure that we set it on the underlying model object as well
            property.set(wrappedObject, wrapper.wrappedObject);
        }

        return wrapper;
    }

    protected <W extends ModelWrapper<? extends R>, R> void setWrappedProperty(
        Property<R, ? super E> property, W wrapper) {
        elementTracker.trackProperty(property);
        R newValue = wrapper != null ? wrapper.wrappedObject : null;
        setProperty(this, property, newValue, wrapper);
    }

    protected <W extends ModelWrapper<? extends R>, R> void setWrapperCollection(
        Property<Collection<R>, ? super E> property, Collection<W> wrappers) {
        Collection<R> newValues = new HashSet<R>();
        for (W element : wrappers) {
            newValues.add(element.getWrappedObject());
        }

        elementTracker.trackCollection(property);
        setModelProperty(this, property, newValues, wrappers);
    }

    private <W extends ModelWrapper<? extends M>, M> List<W> wrapCollectionProperty(
        Property<Collection<M>, ? super E> property, Class<W> klazz) {
        List<W> wrappers = new ArrayList<W>();

        Collection<M> modelCollection = property.get(wrappedObject);
        if (modelCollection != null) {
            Set<M> newModels = new HashSet<M>();
            for (M model : modelCollection) {
                @SuppressWarnings("unchecked")
                W wrapper = (W) session.get(model);

                if (wrapper == null) {
                    wrapper = WrapperUtil.wrapModel(appService, model, klazz);
                    session.add(wrapper);
                }

                // the wrapper might have come from the session so remember it
                // to re-set the value of this property later.
                newModels.add(wrapper.wrappedObject);

                wrappers.add(wrapper);
            }
            property.set(wrappedObject, newModels);
        }

        return wrappers;
    }

    protected <W extends ModelWrapper<? extends R>, R> List<W> getWrapperCollection(
        Property<Collection<R>, ? super E> property, Class<W> wrapperKlazz,
        boolean sort) {

        @SuppressWarnings("unchecked")
        List<W> wrappers = (List<W>) recallProperty(property);

        if (wrappers == null && !isPropertyCached(property)) {
            wrappers = wrapCollectionProperty(property, wrapperKlazz);
            // TODO: if an object or collection of objects is lazily loaded, if
            // there are any references between the objects, ALL references must
            // be replaced with the object in the session, if an object exists
            // in the session. This can be done later since it is probably
            // unlikely. But if not done, will yield incorrect results.

            cacheProperty(property, wrappers);
            elementQueue.flush(property);
        }

        if (wrappers != null && sort) {
            // TODO: should do this once per property?
            Collections.sort(wrappers);
        }

        // return a copy of the internally stored list so that someone
        // externally modifying the collection does not modify the internal
        // collection.
        return new ArrayList<W>(wrappers);
    }

    protected <W extends ModelWrapper<? extends R>, R> void addToWrapperCollection(
        Property<Collection<R>, ? super E> property, List<W> newWrappers) {
        if (newWrappers == null || newWrappers.isEmpty()) {
            return;
        }

        // Use a set so that wrappers of the same object are not double-added.
        Set<W> allWrappers = new HashSet<W>();

        @SuppressWarnings("unchecked")
        Class<W> wrapperKlazz = (Class<W>) newWrappers.get(0).getClass();

        List<W> currentWrappers = getWrapperCollection(property, wrapperKlazz,
            false);

        // if the new wrapper is already in the collection, use the new one
        allWrappers.addAll(newWrappers);

        if (currentWrappers != null) {
            currentWrappers.removeAll(newWrappers);
            allWrappers.addAll(currentWrappers);
        }

        setWrapperCollection(property, new ArrayList<W>(allWrappers));
    }

    protected <W extends ModelWrapper<? extends R>, R> void removeFromWrapperCollection(
        Property<Collection<R>, ? super E> property, List<W> wrappersToRemove) {
        if (wrappersToRemove == null || wrappersToRemove.isEmpty()) {
            return;
        }

        Collection<W> allWrappers = new ArrayList<W>();

        @SuppressWarnings("unchecked")
        Class<W> wrapperKlazz = (Class<W>) wrappersToRemove.get(0).getClass();

        List<W> currentWrappers = getWrapperCollection(property, wrapperKlazz,
            false);

        allWrappers.addAll(currentWrappers);
        allWrappers.removeAll(wrappersToRemove);

        setWrapperCollection(property, allWrappers);
    }

    public <W extends ModelWrapper<? extends R>, R> void removeFromWrapperCollectionWithCheck(
        Property<Collection<R>, ? super E> property, List<W> wrappersToRemove)
        throws BiobankCheckException {
        if (wrappersToRemove == null || wrappersToRemove.isEmpty()) {
            return;
        }

        @SuppressWarnings("unchecked")
        Class<W> wrapperKlazz = (Class<W>) wrappersToRemove.get(0).getClass();
        List<W> currentWrappers = getWrapperCollection(property, wrapperKlazz,
            false);

        if (!currentWrappers.containsAll(wrappersToRemove)) {
            throw new BiobankCheckException("object not in list");
        }

        removeFromWrapperCollection(property, wrappersToRemove);
    }

    protected <T> T getProperty(Property<T, ? super E> property) {
        return getProperty(this, property);
    }

    protected <T, M> T getProperty(ModelWrapper<M> modelWrapper,
        Property<T, ? super M> property) {
        if (modelWrapper == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T value = (T) modelWrapper.recallProperty(property);

        if (value == null && !modelWrapper.isPropertyCached(property)) {
            value = property.get(modelWrapper.getWrappedObject());
            modelWrapper.cacheProperty(property, value);
        }

        return value;
    }

    protected <T> void setProperty(Property<T, ? super E> property, T newValue) {
        setProperty(this, property, newValue, newValue);
    }

    private <T, M> void setProperty(ModelWrapper<M> modelWrapper,
        Property<T, ? super M> property, T newValue, Object valueToCache) {
        setModelProperty(modelWrapper, property, newValue, valueToCache);
    }

    /**
     * Determines whether the given property of the wrapped object has been
     * initialized.
     * 
     * @param property of the wrapped object
     * @return true if the given {@code Property} has been initialized or if the
     *         wrapped object is not a proxy (and therefore new), otherwise
     *         false.
     */
    protected boolean isInitialized(Property<?, ? super E> property) {
        return isNew() || isInitialized(wrappedObject, property);
    }

    /**
     * Determines whether the given property of the given model object has been
     * initialized (loaded).
     * 
     * @param model object with the {@code property}
     * @param property of the wrapped object
     * @return true if the given {@code Property} has been initialized or if the
     *         wrapped object is not a proxy (and therefore new), otherwise
     *         false.
     */
    private static <E> boolean isInitialized(E model,
        Property<?, ? super E> property) {
        if (model instanceof Advised) {
            Advised proxy = (Advised) model;
            try {
                @SuppressWarnings("unchecked")
                E tmp = (E) proxy.getTargetSource().getTarget();
                model = tmp;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return Hibernate.isPropertyInitialized(model, property.getName());
    }

    private <T, M, R> void setModelProperty(ModelWrapper<M> modelWrapper,
        Property<T, ? super M> property, T newValue, R valueForCache) {
        M model = modelWrapper.getWrappedObject();

        // TODO: whenever a property is set, the old value is retrieved from
        // the database (or memory if already loaded) to send the
        // information for the change to listeners. This should be changed
        // to either (1) only get if there are listeners or (2) remember all
        // old values so they can be removed (see cascade().persistAdded()).
        T oldValue = property.get(model);

        property.set(model, newValue);

        // need to add into cache before the firePropertyChange is called
        // because its will call the getters that refers to the cache
        modelWrapper.cacheProperty(property, valueForCache);
        propertyChangeSupport.firePropertyChange(property.getName(), oldValue,
            newValue);
    }

    private void cacheProperty(Property<?, ?> property, Object value) {
        propertyCache.put(property, value);
    }

    protected boolean isPropertyCached(Property<?, ? super E> property) {
        return propertyCache.containsKey(property);
    }

    protected Object recallProperty(Property<?, ?> property) {
        return propertyCache.get(property);
    }

    @SuppressWarnings("unchecked")
    public ModelWrapper<E> getDatabaseClone() throws Exception {
        ModelWrapper<E> wrapper = null;

        Constructor<?> c = getClass().getDeclaredConstructor(
            WritableApplicationService.class);
        Object[] arglist = new Object[] { appService };
        wrapper = (ModelWrapper<E>) c.newInstance(arglist);
        wrapper.setId(getId());
        wrapper.reload();
        return wrapper;
    }

    /**
     * @return a list of center security should check for modifications
     */
    public List<? extends CenterWrapper<?>> getSecuritySpecificCenters() {
        return Collections.emptyList();
    }

    /**
     * Clear internal state, cached, and state-tracking objects.
     */
    private void clear() {
        elementQueue.clear();
        elementTracker.clear();
        propertyCache.clear();
        cache.clear();

        session = new WrapperSession(this);

        resetInternalFields();
    }

    public ElementTracker<E> getElementTracker() {
        return elementTracker;
    }

    protected ElementQueue<E> getElementQueue() {
        return elementQueue;
    }

    protected WrapperCascader<E> cascade() {
        return cascader;
    }

    protected WrapperChecker<E> check() {
        return preChecker;
    }

    /**
     * Will consider the date and not the time.
     */
    // TODO: why is this in ModelWrapper? It's a DateUtil function.
    public static Date endOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * Remove time on this date to get time set to 00:00
     */
    // TODO: why is this in ModelWrapper? It's a DateUtil function.
    public static Date startOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}