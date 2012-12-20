package edu.ualberta.med.biobank.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.model.security.Group;
import edu.ualberta.med.biobank.model.security.Membership;
import edu.ualberta.med.biobank.model.util.IdUtil;

public class GroupSaveInput implements ActionInput {
    private static final long serialVersionUID = 1L;

    private final Integer groupId;
    private final String name;
    private final String description;
    private final Set<Integer> userIds;
    private final Set<Membership> memberships;

    @SuppressWarnings("nls")
    public GroupSaveInput(Group group) {
        if (group == null)
            throw new IllegalArgumentException("null group");

        this.groupId = group.getId();
        this.name = group.getName();
        this.description = group.getName();

        this.memberships = new HashSet<Membership>(group.getMemberships());
        this.userIds = new HashSet<Integer>(IdUtil.getIds(group.getUsers()));
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }
}
