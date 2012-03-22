package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.model.Group;

public class GroupDeleteInput {
    private final Integer groupId;

    public GroupDeleteInput(Group group) {
        if (group == null)
            throw new IllegalArgumentException("null group");
        if (group.getId() == null)
            throw new IllegalArgumentException("null group id");

        this.groupId = group.getId();
    }

    public Integer getGroupId() {
        return groupId;
    }
}
