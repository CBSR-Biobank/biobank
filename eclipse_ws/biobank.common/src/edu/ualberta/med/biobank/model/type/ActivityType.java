package edu.ualberta.med.biobank.model.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ActivityType {
    CENTER_GET_STUDIES(1);

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

    private ActivityType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
