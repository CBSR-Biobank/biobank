package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionOutput;

public class MembershipContextGetOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;

    private final MembershipContext context;

    public MembershipContextGetOutput(MembershipContext context) {
        this.context = context;
    }

    public MembershipContext getContext() {
        return context;
    }
}
