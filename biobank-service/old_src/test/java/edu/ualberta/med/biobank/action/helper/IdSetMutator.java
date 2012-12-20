package edu.ualberta.med.biobank.action.helper;

import java.util.HashSet;
import java.util.Set;

public class IdSetMutator {

    public Set<Integer> getEmpty() {
        return new HashSet<Integer>();
    }

    public Set<Integer> getNull() {
        return null;
    }

    public Set<Integer> getSetWithNull() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(null);
        return set;
    }

    public Set<Integer> getOutOfBounds() {
        Set<Integer> set = new HashSet<Integer>();
        set.add(-14);
        set.add(9999999);
        return set;
    }

}
