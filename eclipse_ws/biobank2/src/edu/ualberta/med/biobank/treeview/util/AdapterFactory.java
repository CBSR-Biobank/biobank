package edu.ualberta.med.biobank.treeview.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class AdapterFactory {
    private static List<String> adaptersPackages;

    static {
        String topPackage = AbstractAdapterBase.class.getPackage().getName();
        adaptersPackages = new ArrayList<String>();
        for (Package p : Package.getPackages()) {
            if (p.getName().startsWith(topPackage)) {
                adaptersPackages.add(p.getName());
            }
        }
    }

    @SuppressWarnings("nls")
    public static AbstractAdapterBase getAdapter(Object object) {
        Class<?> objectClass = object.getClass();
        // FIXME need something better in common, or just this might be inside
        // presenters or actions ?
        if (Enhancer.isEnhanced(objectClass)) {
            objectClass = objectClass.getSuperclass();
        }
        String objectClassName = objectClass.getSimpleName();
        String adapterClassName =
            objectClassName.replace("Wrapper", StringUtil.EMPTY_STRING)
                + "Adapter";
        try {
            for (String packageName : adaptersPackages) {
                Class<?> klass;
                try {
                    klass = Class.forName(packageName + "." + adapterClassName);
                } catch (ClassNotFoundException e) {
                    // try next package
                    continue;
                }
                Class<?> parentClass = AbstractAdapterBase.class;
                if (object instanceof ModelWrapper)
                    parentClass = AdapterBase.class;
                Constructor<?> constructor = klass.getConstructor(new Class[] {
                    parentClass, objectClass });
                return (AbstractAdapterBase) constructor
                    .newInstance(new Object[] { null, object });
            }
            throw new Exception("No adapter class found:" + adapterClassName);
        } catch (Exception e) {
            throw new RuntimeException(
                "error in invoking adapter for object: " + objectClassName
                    + " (adapter name is " + adapterClassName + "). ", e);
        }
    }
}