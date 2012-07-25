package edu.ualberta.med.biobank.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapperUtil {
    public static <T, K, V> Map<K, V> map(Collection<T> target,
        Mapper<T, K, V> mapper) {

        Map<K, V> result = new HashMap<K, V>();

        for (T element : target) {
            K key = mapper.getKey(element);
            V value = result.get(key);

            result.put(key, mapper.getValue(element, value));
        }

        return result;
    }
}
