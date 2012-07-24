package edu.ualberta.med.biobank.model.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface ILogType {
    Integer getId();

    public static class Util {
        @SuppressWarnings("nls")
        public static <T extends Enum<T> & ILogType> Map<Integer, T> buildMap(
            Class<T> enumClass) {
            Map<Integer, T> map = new HashMap<Integer, T>();

            for (T constant : enumClass.getEnumConstants()) {
                T check = map.get(constant.getId());
                if (check != null) {
                    throw new IllegalStateException(enumClass + " uses the id "
                        + constant.getId() + " multiple times");
                }

                map.put(constant.getId(), constant);
            }
            return Collections.unmodifiableMap(map);
        }

        public static <T extends Enum<T> & ILogType> Map<Integer, T> buildUnmodifiableMap(
            Class<T> enumClass) {
            Map<Integer, T> map = buildMap(enumClass);
            return Collections.unmodifiableMap(map);
        }
    }
}
