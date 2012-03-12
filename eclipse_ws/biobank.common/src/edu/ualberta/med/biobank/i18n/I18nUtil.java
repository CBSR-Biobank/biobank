package edu.ualberta.med.biobank.i18n;

import java.util.Iterator;

public class I18nUtil {
    public static <T> String join(final Iterable<T> objs, final String delimiter) {
        Iterator<T> it = objs.iterator();
        if (!it.hasNext()) return "";
        StringBuilder sb = new StringBuilder(String.valueOf(it.next()));
        while (it.hasNext()) {
            sb.append(delimiter).append(String.valueOf(it.next()));
        }
        return sb.toString();
    }

    public static <T> String join(final Iterable<T> objs) {
        String delimiter =
            CommonMessages.LIST_ITEM_DELIMITER.getL10nedMessage() + " ";
        return join(objs, delimiter);
    }
}
