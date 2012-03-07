package edu.ualberta.med.biobank.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// TODO: consider making Rank an @Embeddable with an isManager and an isAllPermissions components, perhaps serialized to an int? a bit array?
public enum Rank {
    NORMAL(0, "Normal"),
    USER_MANAGER(1, "User Manager"),
    ADMINISTRATOR(2, "Administrator");

    private static final List<Rank> VALUES_LIST = Collections
        .unmodifiableList(Arrays.asList(values()));

    private final byte rank;
    private final String name;

    private Rank(int rank, String name) {
        this.rank = (byte) rank;
        this.name = name;
    }

    public static List<Rank> valuesList() {
        return VALUES_LIST;
    }

    public byte getId() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public boolean isLt(Rank that) {
        return this.rank < that.rank;
    }

    public boolean isLe(Rank that) {
        return this.rank <= that.rank;
    }

    public boolean isGt(Rank that) {
        return this.rank > that.rank;
    }

    public boolean isGe(Rank that) {
        return this.rank >= that.rank;
    }

    public static Rank fromId(byte rank) {
        for (Rank item : values()) {
            if (item.rank == rank) {
                return item;
            }
        }
        return NORMAL;
    }
}
