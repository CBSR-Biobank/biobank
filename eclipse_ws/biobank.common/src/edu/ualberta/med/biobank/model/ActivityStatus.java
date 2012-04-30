package edu.ualberta.med.biobank.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.util.NotAProxy;

public enum ActivityStatus implements NotAProxy, Serializable {
    // TODO: a key for translation? Even if comes from server is enum, so will
    // use local translation.
    // TODO: comment on NOT CHANGING THE ID!!!
    ACTIVE(1, "Active"),
    CLOSED(2, "Closed"),
    // TODO: why can't there be a closed and flagged item or an active but
    // flagged
    // item? Especially for users that are mean to be enabled or disabled.
    // When is ActivityStatus.FLAGGED even used in the source code? Can a
    // flagged
    // user log in? What is the point of flagged? Shouldn't it be separate?
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

    @Override
    public String toString() {
        return getName();
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
