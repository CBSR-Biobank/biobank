package edu.ualberta.med.biobank.common.util;

import java.util.Collection;
import java.util.Iterator;

public class StringUtil {
    /**
     * Returns a concatenation of the {@link String} form of each element in the
     * given {@code Collection}, delimited by the given delimiter. Rolled our
     * own because don't want to include a whole jar for this method.
     * 
     * @param <T>
     * @param s
     * @param delimiter
     * @return
     */
    public static <T> String join(Collection<T> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<T> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    /**
     * @param unit to repeat
     * @param n number of times to repeat
     * @param delimiter to separate unit-s
     * @return a string of {@code unit} repeated {@code n} times, with a
     *         {@code delimiter} in between each {@code unit}.
     */
    public static String repeat(String unit, int n, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(unit);
            if (i < n) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String truncate(String string, int n, String suffix) {
        if (string.length() - suffix.length() > n) {
            return string.substring(0, n) + suffix;
        }
        return string;
    }
}
