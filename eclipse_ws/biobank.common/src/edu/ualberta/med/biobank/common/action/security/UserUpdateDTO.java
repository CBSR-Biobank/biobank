package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.model.User;

public class UserUpdateDTO {
    private String login;
    private boolean recvBulkEmails = true;
    private String fullName;
    private String email;
    private boolean needPwdChange = true;
    private Set<Integer> groupIds = new HashSet<Integer>(0);

    public UserUpdateDTO(User user) {
        this.login = user.getLogin();
        this.recvBulkEmails = user.getRecvBulkEmails();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.needPwdChange = user.getNeedPwdChange();
        this.groupIds = null; // TODO: this;
    }

    public void update(User user) {

    }
}
