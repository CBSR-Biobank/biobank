package edu.ualberta.med.biobank.treeview;

import java.lang.reflect.Constructor;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class AdapterFactory {

    public static AdapterBase getAdapter(ModelWrapper<?> wrapper) {
        Class<?> wrapperClass = wrapper.getClass();
        String wrapperClassName = wrapperClass.getName();
        String adapterClassName = new String(wrapperClassName);
        adapterClassName = "edu.ualberta.med.biobank.treeview."
            + adapterClassName.substring(adapterClassName.lastIndexOf('.') + 1);
        adapterClassName = adapterClassName.replace("Wrapper", "Adapter");

        try {
            Class<?> klass = Class.forName(adapterClassName);
            Constructor<?> constructor = klass.getConstructor(new Class[] {
                AdapterBase.class, wrapperClass });
            return (AdapterBase) constructor.newInstance(new Object[] { null,
                wrapper });
        } catch (Exception e) {
            throw new RuntimeException(
                "error in invoking adapter for wrapper: " + wrapperClassName
                    + ". " + e);
        }
    }
}