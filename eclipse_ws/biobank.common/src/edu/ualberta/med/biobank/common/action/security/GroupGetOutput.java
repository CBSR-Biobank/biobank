package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionOutput;
import edu.ualberta.med.biobank.model.Group;

public class GroupGetOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;

    private final Group group;
    private final ManagerContext context;

    public GroupGetOutput(Group group, ManagerContext context) {
        this.group = group;
        this.context = context;
    }

    public Group getGroup() {
        return group;
    }

    public ManagerContext getContext() {
        return context;
    }
}
