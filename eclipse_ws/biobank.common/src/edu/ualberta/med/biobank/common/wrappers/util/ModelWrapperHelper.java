package edu.ualberta.med.biobank.common.wrappers.util;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class ModelWrapperHelper {
    private static final Map<Class<?>, Class<?>> modelKlazzMap = new HashMap<Class<?>, Class<?>>();

    public static Class<?> getWrapperClass(Class<?> modelKlazz) {
        Class<?> wrapperKlazz = modelKlazzMap.get(modelKlazz);

        if (wrapperKlazz == null && !modelKlazzMap.containsKey(modelKlazz)) {
            StringBuilder sb = new StringBuilder();

            sb.append(ModelWrapper.class.getPackage().getName());
            sb.append(".");
            sb.append(modelKlazz.getSimpleName());
            sb.append("Wrapper");

            String wrapperKlazzName = sb.toString();
            try {
                Class<?> tmp = Class.forName(wrapperKlazzName);
                wrapperKlazz = tmp;
            } catch (ClassNotFoundException e) {
            }

            modelKlazzMap.put(modelKlazz, wrapperKlazz);
        }

        return wrapperKlazz;
    }
}
