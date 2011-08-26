package edu.ualberta.med.biobank.common.wrappers.util;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class ModelWrapperHelper {
    private static final Map<Class<?>, Class<?>> modelClassMap = new HashMap<Class<?>, Class<?>>();

    public static <E> Class<? extends ModelWrapper<E>> getWrapperClass(
        Class<E> modelClass) {
        @SuppressWarnings("unchecked")
        Class<? extends ModelWrapper<E>> wrapperClass = (Class<? extends ModelWrapper<E>>) modelClassMap
            .get(modelClass);

        if (wrapperClass == null && !modelClassMap.containsKey(modelClass)) {
            wrapperClass = findWrapperClass(modelClass);

            modelClassMap.put(modelClass, wrapperClass);
        }

        return wrapperClass;
    }

    public static <E> Class<? extends ModelWrapper<E>> findWrapperClass(
        Class<E> modelClass) {
        Class<? extends ModelWrapper<E>> wrapperClass = null;

        String packageName = ModelWrapper.class.getPackage().getName();
        String internalPackageName = packageName + ".internal";
        String classSimpleName = modelClass.getSimpleName() + "Wrapper";

        try {
            @SuppressWarnings("unchecked")
            Class<? extends ModelWrapper<E>> tmp = (Class<? extends ModelWrapper<E>>) Class
                .forName(packageName + "." + classSimpleName);
            wrapperClass = tmp;
        } catch (ClassNotFoundException e) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends ModelWrapper<E>> tmp = (Class<? extends ModelWrapper<E>>) Class
                    .forName(internalPackageName + "." + classSimpleName);
                wrapperClass = tmp;
            } catch (ClassNotFoundException e1) {
            }
        }

        return wrapperClass;
    }

    // public static <E> Property<Integer, ? super E> getIdProperty() {
    //
    // }

    // TODO: write function to return ID property for a given class!!!!
}
