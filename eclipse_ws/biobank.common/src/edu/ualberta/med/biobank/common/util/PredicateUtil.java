package edu.ualberta.med.biobank.common.util;

import java.util.ArrayList;
import java.util.Collection;

public class PredicateUtil {
    public static <T> Collection<T> filter(Collection<T> target,
        Predicate<T> predicate) {

        Collection<T> result = new ArrayList<T>();

        for (T element : target) {
            if (predicate.evaluate(element)) {
                result.add(element);
            }
        }

        return result;
    }

    public static <T> Predicate<T> andPredicate(final Predicate<T> p1,
        final Predicate<T> p2, final Predicate<T>... pN) {
        return new Predicate<T>() {
            @Override
            public boolean evaluate(T type) {
                boolean result = p1.evaluate(type) && p2.evaluate(type);

                for (Predicate<T> p : pN) {
                    if (!result) {
                        break;
                    }

                    result &= p.evaluate(type);
                }

                return result;
            }
        };
    }
}
