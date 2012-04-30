package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionOutput;

public class ManagerContextGetOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;

    private final ManagerContext context;

    public ManagerContextGetOutput(ManagerContext context) {
        this.context = context;
    }

    public ManagerContext getContext() {
        return context;
    }
}
