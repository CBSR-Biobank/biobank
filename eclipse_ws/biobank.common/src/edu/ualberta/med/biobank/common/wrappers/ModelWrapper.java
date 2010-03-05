package edu.ualberta.med.biobank.common.wrappers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class ModelWrapper<E> implements Comparable<ModelWrapper<E>> {

    protected WritableApplicationService appService;

    protected E wrappedObject;

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
        this);

    protected HashMap<String, Object> propertiesMap = new HashMap<String, Object>();

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
        resetInternalField();
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
        persistChecks();
        SDKQuery query;
        E origObject = null;
        if (isNew()) {
            query = new InsertExampleQuery(wrappedObject);
        } else {
            query = new UpdateExampleQuery(wrappedObject);
            origObject = getObjectFromDatabase();
        }
        persistDependencies(origObject);
        SDKQueryResult result = appService.executeQuery(query);
        wrappedObject = ((E) result.getObjectResult());
        propertiesMap.clear();
        resetInternalField();
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
        appService.executeQuery(new DeleteExampleQuery(wrappedObject));
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
        resetInternalField();
    }

    /**
     * even if this object was loaded form database, start form scratch (so
     * isNew = true)
     * 
     * @throws Exception
     */
    public void resetToNewObject() throws Exception {
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

    protected boolean checkNoDuplicates(Class<?> objectClass,
        String propertyName, String value) throws ApplicationException {
        HQLCriteria c;
        if (isNew()) {
            c = new HQLCriteria("from " + objectClass.getName() + " where "
                + propertyName + "= ?", Arrays.asList(new Object[] { value }));
        } else {
            c = new HQLCriteria("from " + objectClass.getName()
                + " where id <> ? and " + propertyName + "= ?", Arrays
                .asList(new Object[] { getId(), value }));
        }

        List<Object> results = appService.query(c);
        return (results.size() == 0);
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
    protected void resetInternalField() {
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
}