package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Group;

public class ManagedGroup implements ActionResult {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private final Set<ManagedMembership> memberships =
        new HashSet<ManagedMembership>();

    public ManagedGroup(Group group) {
        this.name = group.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ManagedMembership> getMemberships() {
        return memberships;
    }
}
