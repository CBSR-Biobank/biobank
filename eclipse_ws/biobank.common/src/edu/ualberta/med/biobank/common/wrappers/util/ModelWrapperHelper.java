package edu.ualberta.med.biobank.common.wrappers.util;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class ModelWrapperHelper {
    private static final Map<Class<?>, Class<? extends ModelWrapper<?>>> modelKlazzMap = new HashMap<Class<?>, Class<? extends ModelWrapper<?>>>();

    public static <M> Class<ModelWrapper<M>> getWrapperClass(Class<M> modelKlazz) {
        @SuppressWarnings("unchecked")
        Class<ModelWrapper<M>> wrapperKlazz = (Class<ModelWrapper<M>>) modelKlazzMap
            .get(modelKlazz);

        if (wrapperKlazz == null && !modelKlazzMap.containsKey(modelKlazz)) {
            StringBuilder sb = new StringBuilder();

            sb.append(ModelWrapper.class.getPackage().getName());
            sb.append(".");
            sb.append(modelKlazz.getSimpleName());
            sb.append("Wrapper");

            String wrapperKlazzName = sb.toString();
            try {
                @SuppressWarnings("unchecked")
                Class<ModelWrapper<M>> tmp = (Class<ModelWrapper<M>>) Class
                    .forName(wrapperKlazzName);
                wrapperKlazz = tmp;
            } catch (ClassNotFoundException e) {
            }

            modelKlazzMap.put(modelKlazz, wrapperKlazz);
        }

        return wrapperKlazz;
    }
}
