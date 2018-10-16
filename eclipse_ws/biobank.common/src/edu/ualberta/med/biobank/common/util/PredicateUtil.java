package edu.ualberta.med.biobank.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PredicateUtil {
    public static <T> Collection<T> filter(Collection<T> source,
        Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        filterInto(source, predicate, result);
        return result;
    }

    public static <T> void filterInto(Collection<T> source,
        Predicate<T> predicate, Collection<T> dest) {
        for (T element : source) {
            if (predicate.evaluate(element)) {
                dest.add(element);
            }
        }
    }

    public static <T> void filterOut(Collection<T> target,
        Predicate<T> predicate) {
        Iterator<T> it = target.iterator();
        while (it.hasNext()) {
            if (!predicate.evaluate(it.next())) {
                it.remove();
            }
        }
    }

    public static <T> Predicate<T> andPredicate(final Predicate<T> p1,
        final Predicate<T> p2, @SuppressWarnings("unchecked") final Predicate<T>... pN) {
        return new Predicate<T>() {
            @Override
            public boolean evaluate(T type) {
                if (!p1.evaluate(type) || !p2.evaluate(type)) {
                    return false;
                }

                for (Predicate<T> p : pN) {
                    if (!p.evaluate(type)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    public static <T> Predicate<T> orPredicate(final Predicate<T> p1,
        final Predicate<T> p2, @SuppressWarnings("unchecked") final Predicate<T>... pN) {
        return new Predicate<T>() {
            @Override
            public boolean evaluate(T type) {
                if (p1.evaluate(type) || p2.evaluate(type)) {
                    return true;
                }

                for (Predicate<T> p : pN) {
                    if (p.evaluate(type)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static <T> Predicate<T> notPredicate(final Predicate<T> predicate) {
        return new Predicate<T>() {
            @Override
            public boolean evaluate(T type) {
                return !predicate.evaluate(type);
            }
        };
    }
}
