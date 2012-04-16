package edu.ualberta.med.biobank.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.util.StringUtil;

public class LocalizedList extends LazyString {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static LocalizedString LIST_SEPARATOR =
        LocalizedString.trc("List item separator", ", ");

    private final List<Object> elements;

    public LocalizedList(Collection<Object> c) {
        elements = new ArrayList<Object>(c);
    }

    @Override
    protected String loadString() {
        String s = StringUtil.join(elements, LIST_SEPARATOR.toString());
        return s;
    }
}
