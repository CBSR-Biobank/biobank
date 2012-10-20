package edu.ualberta.med.biobank.action.security;

import edu.ualberta.med.biobank.action.security.Action2p0.ActionOutput;
import edu.ualberta.med.biobank.model.security.Group;

public class GroupGetOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;

    private final Group group;
    private final MembershipContext context;

    public GroupGetOutput(Group group, MembershipContext context) {
        this.group = group;
        this.context = context;
    }

    public Group getGroup() {
        return group;
    }

    public MembershipContext getContext() {
        return context;
    }
}
