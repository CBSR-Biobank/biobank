package edu.ualberta.med.biobank.model.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

@SuppressWarnings("nls")
public enum ActivityType {
    CENTER_GET_STUDIES(1,
        Loader.i18n.tr("Did something"),
        Loader.i18n.tr("Something was done on {0} and {1}."));

    private static final List<ActivityType> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    public static List<ActivityType> valuesList() {
        return VALUES_LIST;
    }

    public static ActivityType fromId(Integer id) {
        for (ActivityType item : values()) {
            if (item.id.equals(id)) return item;
        }
        return null;
    }

    private final Integer id;
    private final String name;
    private final String template;

    private ActivityType(Integer id, String name, String template) {
        this.id = id;
        this.name = name;
        this.template = template;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
