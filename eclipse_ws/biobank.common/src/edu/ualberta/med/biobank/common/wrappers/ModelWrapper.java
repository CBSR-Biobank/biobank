package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.VarCharLengths;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankStringLengthException;
import edu.ualberta.med.biobank.common.security.Role;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperEvent.WrapperEventType;
import edu.ualberta.med.biobank.common.wrappers.listener.WrapperListener;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class ModelWrapper<E> implements Comparable<ModelWrapper<E>> {

    protected WritableApplicationService appService;

    protected E wrappedObject;

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
        this);

    protected HashMap<String, Object> propertiesMap = new HashMap<String, Object>();

    private List<WrapperListener> listeners = new ArrayList<WrapperListener>();

    public ModelWrapper(WritableApplicationService appService, E wrappedObject) {
        this.appService = appService;
        this.wrappedObject = wrappedObject;
    }

    /**
     * create a new wrapped object
     */
    public ModelWrapper(WritableApplicationService appService) {
        this.appService = appService;
        try {
            this.wrappedObject = getNewObject();
        } catch (Exception e) {
            Class<E> classType = getWrappedClass();
            if (classType != null) {
                throw new RuntimeException(
                    "was not able to create new object of type "
                        + classType.getName());
            } else {
                throw new RuntimeException("was not able to create new object");
            }
        }
    }

    public E getWrappedObject() {
        return wrappedObject;
    }

    public void setWrappedObject(E wrappedObject) {
        this.wrappedObject = wrappedObject;
        propertiesMap.clear();
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        String[] properties = getPropertyChangeNames();
        if ((properties == null) || (properties.length == 0)) {
            throw new RuntimeException("wrapper has not defined any properties");
        }
        List<String> propertiesList = Arrays.asList(properties);
        if (!propertiesList.contains(propertyName)) {
            throw new RuntimeException("invalid property: " + propertyName);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public boolean isNew() {
        return (getId() == null);
    }

    public Integer getId() {
        Class<?> wrappedClass = wrappedObject.getClass();
        try {
            Method methodGetId = wrappedClass.getMethod("getId");
            return (Integer) methodGetId.invoke(wrappedObject);
        } catch (Exception e) {

        }
        return null;
    }

    public WritableApplicationService getAppService() {
        return appService;
    }

    public void reload() throws Exception {
        if (!isNew()) {
            E oldValue = wrappedObject;
            wrappedObject = getObjectFromDatabase();
            firePropertyChanges(oldValue, wrappedObject);
        }
        propertiesMap.clear();
        resetInternalFields();
    }

    /**
     * return the list of the different properties we want to notify when we
     * call firePropertyChanges
     */
    protected abstract String[] getPropertyChangeNames();

    /**
     * When retrieve the values from the database, need to fire the
     * modifications for the different objects contained in the wrapped object
     */
    private void firePropertyChanges(Object oldWrappedObject,
        Object newWrappedObject) throws Exception {
        String[] memberNames = getPropertyChangeNames();
        if (memberNames == null) {
            throw new Exception("memberNames cannot be null");
        }
        for (String member : memberNames) {
            propertyChangeSupport.firePropertyChange(member, oldWrappedObject,
                newWrappedObject);
        }
    }

    /**
     * using this wrapper id, retrieve the object from the database
     */
    protected E getObjectFromDatabase() throws WrapperException {
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
            throw new WrapperException(ex);
        }
        if (list.size() == 0)
            return null;
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new WrapperException("Found " + list.size() + " objects of type "
            + classType.getName() + " with id=" + id);
    }

    public abstract Class<E> getWrappedClass();

    /**
     * insert or update the object into the database
     */
    @SuppressWarnings("unchecked")
    public void persist() throws Exception {
        checkFieldLimits();
        persistChecks();
        SDKQuery query;
        E origObject = null;
        WrapperEventType eventType;
        if (isNew()) {
            query = new InsertExampleQuery(wrappedObject);
            eventType = WrapperEventType.INSERT;
        } else {
            query = new UpdateExampleQuery(wrappedObject);
            origObject = getObjectFromDatabase();
            eventType = WrapperEventType.UPDATE;
        }
        persistDependencies(origObject);
        SDKQueryResult result = ((BiobankApplicationService) appService)
            .executeQuery(query);
        wrappedObject = ((E) result.getObjectResult());
        Log logMessage = null;
        try {
            logMessage = getLogMessage(eventType.name().toLowerCase(), null, "");
        } catch (Exception ex) {
            // Don't want the logs to affect persist
            // FIXME save somewhere this information
            ex.printStackTrace();
        }
        if (logMessage != null) {
            ((BiobankApplicationService) appService).logActivity(logMessage);
        }
        propertiesMap.clear();
        resetInternalFields();
        notifyListeners(new WrapperEvent(eventType, this));
    }

    /**
     * should redefine this method if others updates (or deletes) need to be
     * done when this object is update origObject can be null in the case of an
     * insert
     */
    @SuppressWarnings("unused")
    protected void persistDependencies(E origObject) throws Exception {
    }

    protected abstract void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException;

    protected void checkFieldLimits() throws BiobankCheckException,
        BiobankStringLengthException {
        String fieldValue = "";
        String[] fields = getPropertyChangeNames();
        for (int i = 0; i < fields.length; i++) {
            Integer maxLen = VarCharLengths.getMaxSize(
                wrappedObject.getClass(), fields[i]);
            if (maxLen == null)
                continue;

            Method method;
            try {
                method = this.getClass().getMethod(
                    "get" + Character.toUpperCase(fields[i].charAt(0))
                        + fields[i].substring(1));
                if (method.getReturnType().equals(String.class)) {
                    fieldValue = (String) method.invoke(this);
                    if ((fieldValue != null) && (fieldValue.length() > maxLen)) {
                        throw new BiobankStringLengthException(
                            "Field exceeds max length: field: " + fields[i]
                                + ", value \"" + fieldValue + "\"");
                    }
                }
            } catch (SecurityException e) {
                throwBiobankException(fields[i], e);
            } catch (NoSuchMethodException e) {
                throwBiobankException(fields[i], e);
            } catch (IllegalArgumentException e) {
                throwBiobankException(fields[i], e);
            } catch (IllegalAccessException e) {
                throwBiobankException(fields[i], e);
            } catch (InvocationTargetException e) {
                throwBiobankException(fields[i], e);
            }
        }
    }

    private void throwBiobankException(String field, Exception e)
        throws BiobankCheckException {
        throw new BiobankCheckException("Cannot get max length for field "
            + field, e);
    }

    /**
     * delete the object into the database
     * 
     * @throws ApplicationException
     */
    public void delete() throws Exception {
        if (isNew()) {
            throw new Exception("Can't delete an object not yet persisted");
        }
        reload();
        deleteChecks();
        deleteDependencies();
        Log logMessage = null;
        try {
            logMessage = getLogMessage("delete", null, "");
        } catch (Exception ex) {
            // Don't want the logs to affect delete
            // FIXME save somewhere this information
            ex.printStackTrace();
        }
        appService.executeQuery(new DeleteExampleQuery(wrappedObject));
        if (logMessage != null) {
            ((BiobankApplicationService) appService).logActivity(logMessage);
        }
        notifyListeners(new WrapperEvent(WrapperEventType.DELETE, this));
    }

    @SuppressWarnings("unused")
    protected void deleteDependencies() throws Exception {

    }

    protected abstract void deleteChecks() throws Exception;

    public void reset() throws Exception {
        if (isNew()) {
            resetToNewObject();
        } else {
            reload();
        }
        propertiesMap.clear();
        resetInternalFields();
    }

    /**
     * even if this object was loaded form database, start form scratch (so
     * isNew = true)
     * 
     * @throws Exception
     */
    private void resetToNewObject() throws Exception {
        E oldValue = wrappedObject;
        wrappedObject = getNewObject();
        firePropertyChanges(oldValue, wrappedObject);
    }

    protected E getNewObject() throws Exception {
        Constructor<E> constructor = getWrappedClass().getConstructor();
        return constructor.newInstance();
    }

    public void loadAttributes() throws Exception {
        Class<E> classType = getWrappedClass();
        if (classType == null) {
            throw new Exception("wrapped class is null");
        }
        Method[] methods = classType.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")
                && !method.getName().equals("getClass")
                && !Collection.class.isAssignableFrom(method.getReturnType())) {
                method.invoke(wrappedObject, (Object[]) null);
            }
        }
    }

    protected void checkNoDuplicates(Class<?> objectClass, String propertyName,
        String value, String errorName) throws ApplicationException,
        BiobankCheckException {
        HQLCriteria c;
        if (isNew()) {
            c = new HQLCriteria("from " + objectClass.getName() + " where "
                + propertyName + "= ?", Arrays.asList(new Object[] { value }));
        } else {
            c = new HQLCriteria("from " + objectClass.getName()
                + " where id <> ? and " + propertyName + "= ?",
                Arrays.asList(new Object[] { getId(), value }));
        }

        List<Object> results = appService.query(c);
        if (results.size() > 0) {
            throw new BiobankCheckException(errorName + " \"" + value
                + "\" already exists.");
        }
    }

    protected void checkNoDuplicatesInSite(Class<?> objectClass,
        String propertyName, String value, Integer siteId, String errorMessage)
        throws ApplicationException, BiobankCheckException {
        List<Object> parameters = new ArrayList<Object>(
            Arrays.asList(new Object[] { value }));
        String siteIdTest = "site.id=?";
        if (siteId == null) {
            siteIdTest = "site.id is null";
        } else {
            parameters.add(siteId);
        }
        String notSameObject = "";
        if (!isNew()) {
            notSameObject = " and id <> ?";
            parameters.add(getId());
        }
        HQLCriteria criteria = new HQLCriteria(
            "from " + objectClass.getName() + " where " + propertyName
                + "=? and " + siteIdTest + notSameObject, parameters);
        List<Object> results = appService.query(criteria);
        if (results.size() > 0) {
            throw new BiobankCheckException(errorMessage);
        }
    }

    protected void checkNotEmpty(String value, String errorName)
        throws BiobankCheckException {
        if (value == null || value.isEmpty()) {
            throw new BiobankCheckException(errorName + " can't be empty");
        }
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
            return toString().equals(object.toString());
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
     * if the object is new).
     */
    protected void resetInternalFields() {
        // default do nothing
    }

    /**
     * this method is used in the equals method. If it is not redefined in
     * subclasses, we want it to return something better than the default
     * toString
     */
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
    public boolean canView(User user) {
        return user.hasRoleOnObject(Role.READ, getWrappedClass().getName());
    }

    /**
     * return true if the user can edit this object
     */
    public boolean canEdit(User user) {
        return user.hasRoleOnObject(Role.UPDATE, getWrappedClass().getName());
    }

    public void addWrapperListener(WrapperListener listener) {
        listeners.add(listener);
    }

    public void removeWrapperListener(WrapperListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(WrapperEvent event) {
        for (WrapperListener listener : listeners) {
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
        throws WrapperException {
        if (otherWrapper == null) {
            throw new WrapperException(
                "Cannot init internal object with a null wrapper");
        }
        setWrappedObject(otherWrapper.wrappedObject);
    }

    public void logLookup(String site) throws Exception {
        ((BiobankApplicationService) appService).logActivity(getLogMessage(
            "select", site, getWrappedClass().getSimpleName() + " LOOKUP"));
    }

    public void logEdit(String site) throws Exception {
        if (!isNew()) {
            ((BiobankApplicationService) appService).logActivity(getLogMessage(
                "edit", site, getWrappedClass().getSimpleName() + " EDIT"));
        }
    }

    protected Log getLogMessage(@SuppressWarnings("unused") String action,
        @SuppressWarnings("unused") String site,
        @SuppressWarnings("unused") String details) {
        return null;
    }

}