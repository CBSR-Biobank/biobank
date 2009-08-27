package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.DatabaseResult;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public boolean isNew() {
        return getId() == null;
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

    protected abstract void firePropertyChanges(E oldValue, E wrappedObject2);

    protected abstract void internalReload() throws Exception;

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

    public void reset() throws Exception {
        if (isNew()) {
            wrappedObject = getNewObject();
        } else {
            reload();
        }
    }

    @SuppressWarnings("unchecked")
    protected E getNewObject() {
        Class<E> wrappedClass = (Class<E>) wrappedObject.getClass();
        try {
            Constructor<E> constructor = wrappedClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
        }
        return null;
    }
}
