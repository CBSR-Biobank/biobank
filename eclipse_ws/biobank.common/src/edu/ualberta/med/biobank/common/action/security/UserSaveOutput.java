package edu.ualberta.med.biobank.common.action.security;

import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionOutput;

public class UserSaveOutput implements ActionOutput {
    private static final long serialVersionUID = 1L;

    private final Integer userId;
    private final Long csmUserId;

    public UserSaveOutput(Integer userId, Long csmUserId) {
        this.userId = userId;
        this.csmUserId = csmUserId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Long getCsmUserId() {
        return csmUserId;
    }
}
