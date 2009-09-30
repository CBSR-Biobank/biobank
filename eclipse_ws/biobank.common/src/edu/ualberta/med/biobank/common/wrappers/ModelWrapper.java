package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public abstract class ModelWrapper<E> {

    protected WritableApplicationService appService;

    protected E wrappedObject;

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
        this);

    public ModelWrapper(WritableApplicationService appService, E wrappedObject) {
        this.appService = appService;
        this.wrappedObject = wrappedObject;
    }

    public E getWrappedObject() {
        return wrappedObject;
    }

    public void setWrappedObject(E wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
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

    public void reload() throws Exception {
        E oldValue = wrappedObject;
        if (!isNew()) {
            internalReload();
            firePropertyChanges(oldValue, wrappedObject);
        }
    }

    /**
     * When retrieve the values from the database, need to fire the
     * modifications for the different objects contained in the wrapped object
     */
    protected abstract void firePropertyChanges(E oldWrappedObject,
        E newWrappedObject);

    protected void firePropertyChanges(String[] memberNames,
        Object oldWrappedObject, Object newWrappedObject) throws Exception {
        if (memberNames == null) {
            throw new Exception("memberNames cannot be null");
        }
        for (String member : memberNames) {
            propertyChangeSupport.firePropertyChange(member, oldWrappedObject,
                newWrappedObject);
        }
    }

    private void internalReload() throws Exception {
        Class<E> classType = getWrappedClass();
        Constructor<E> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        Method setIdMethod = classType.getMethod("setId", Integer.class);
        setIdMethod.invoke(instance, getId());

        List<E> list = appService.search(classType, instance);
        if (list.size() == 0)
            wrappedObject = null;
        if (list.size() != 1) {
            throw new Exception("expected size to be 1");
        }
        wrappedObject = list.get(0);
    }

    protected abstract Class<E> getWrappedClass();

    /**
     * insert or update the object into the database
     */
    @SuppressWarnings("unchecked")
    public DatabaseResult persist() throws ApplicationException {
        DatabaseResult checkResult = persistChecks();

        if (checkResult == DatabaseResult.OK) {
            SDKQuery query;
            if (isNew()) {
                query = new InsertExampleQuery(wrappedObject);
            } else {
                query = new UpdateExampleQuery(wrappedObject);
            }

            SDKQueryResult result = appService.executeQuery(query);
            wrappedObject = ((E) result.getObjectResult());
        }
        return checkResult;
    }

    protected abstract DatabaseResult persistChecks()
        throws ApplicationException;

    /**
     * delete the object into the database
     */
    public DatabaseResult delete() throws ApplicationException {
        DatabaseResult checkResult = DatabaseResult.OK;
        if (!isNew()) {
            checkResult = deleteChecks();
            if (checkResult == DatabaseResult.OK) {
                appService.executeQuery(new DeleteExampleQuery(wrappedObject));
            }
        }
        return checkResult;
    }

    protected abstract DatabaseResult deleteChecks()
        throws ApplicationException;

    public void reset() throws Exception {
        if (isNew()) {
            wrappedObject = getNewObject();
        } else {
            reload();
        }
    }

    protected E getNewObject() throws Exception {
        Constructor<E> constructor = getWrappedClass().getConstructor();
        return constructor.newInstance();
    }

    public void loadAttributes() throws Exception {
        Class<E> classType = getWrappedClass();

        Method[] methods = classType.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")
                && !method.getName().equals("getClass")
                && !method.getReturnType().getName().equals("java.util.Set")) {
                method.invoke(wrappedObject, (Object[]) null);
            }
        }
    }

    /**
     * return true if integrity of this object is ok
     */
    public abstract boolean checkIntegrity();
}
