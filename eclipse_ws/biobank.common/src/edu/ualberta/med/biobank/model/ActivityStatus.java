package edu.ualberta.med.biobank.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ActivityStatus {
    // TODO: a key for translation? Even if comes from server is enum, so will
    // use local translation.
    // TODO: comment on NOT CHANGING THE ID!!!
    // 0 value is necessary for hibernate
    @Deprecated
    DUMMY(0, "DUMMY"),
    ACTIVE(1, "Active"),
    CLOSED(2, "Closed"),
    FLAGGED(4, "Flagged");

    private static final List<ActivityStatus> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    private final int id;
    private final String name;

    private ActivityStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<ActivityStatus> valuesList() {
        return VALUES_LIST;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static ActivityStatus fromId(int id) {
        for (ActivityStatus item : values()) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }
}
