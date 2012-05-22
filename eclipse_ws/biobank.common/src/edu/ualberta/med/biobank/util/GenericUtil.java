package edu.ualberta.med.biobank.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("nls")
public abstract class GenericUtil {
    public static Class<?> getFirstTypeParameterDeclaredOnSuperclass(
        Class<?> klazz) {

        if (klazz == null)
            throw new IllegalArgumentException("clazz may not be null");

        Type classGenType = klazz.getGenericSuperclass();

        // special CGLIB workaround -- get generic superclass of superclass
        if (klazz.getName().contains("$$EnhancerByCGLIB$$")) {
            classGenType = klazz.getSuperclass().getGenericSuperclass();
        }

        if (classGenType instanceof ParameterizedType) {
            Type[] params =
                ((ParameterizedType) classGenType).getActualTypeArguments();

            if ((params != null) && (params.length >= 1)) {
                return (Class<?>) params[0];
            }
        }

        for (Type ifGenType : klazz.getGenericInterfaces()) {
            if (ifGenType instanceof ParameterizedType) {
                Type[] params =
                    ((ParameterizedType) ifGenType).getActualTypeArguments();

                if ((params != null) && (params.length >= 1)) {
                    return (Class<?>) params[0];
                }
            }
        }

        throw new IllegalArgumentException("No type parameters found on "
            + klazz.getSimpleName());
    }

    private GenericUtil() {
    }
}
