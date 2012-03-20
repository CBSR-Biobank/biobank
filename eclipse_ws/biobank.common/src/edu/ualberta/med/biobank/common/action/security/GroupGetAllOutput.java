package edu.ualberta.med.biobank.common.action.security;

import java.util.SortedSet;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Group;

public class GroupGetAllOutput implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final SortedSet<Group> allManageableGroups;

    public GroupGetAllOutput(SortedSet<Group> allManageableGroups) {
        this.allManageableGroups = allManageableGroups;
    }

    public SortedSet<Group> getAllManageableGroups() {
        return allManageableGroups;
    }
}
