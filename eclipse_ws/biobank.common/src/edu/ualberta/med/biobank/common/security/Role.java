package edu.ualberta.med.biobank.common.security;

import java.util.HashSet;
import java.util.Set;

public enum Role {
    CREATE, UPDATE, READ, DELETE;

    public static Set<? extends Role> getRoles(String string) {
        Set<Role> res = new HashSet<Role>();
        for (Role r : values()) {
            if (string.toLowerCase().contains(r.name().toLowerCase())) {
                res.add(r);
            }
        }
        return res;
    }
}
