package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.Group;

public class GroupGetInput implements ActionInput {
    private static final long serialVersionUID = 1L;

    private final Integer groupId;

    public GroupGetInput(Group group) {
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
