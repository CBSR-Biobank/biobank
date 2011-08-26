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
}
