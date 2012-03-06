package edu.ualberta.med.biobank.common.action.security;

import java.util.HashSet;
import java.util.Set;

public class UserCreateDTO {
    private String login;
    private boolean recvBulkEmails = true;
    private String fullName;
    private String email;
    private boolean needPwdChange = true;
    private Set<Integer> groupIds = new HashSet<Integer>(0);
}
