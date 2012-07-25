package edu.ualberta.med.biobank.action.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestGroupDeleteAction.class, TestGroupGetAllAction.class,
    TestGroupSaveAction.class, TestRoleDeleteAction.class,
    TestRoleGetAllAction.class, TestRoleSaveAction.class,
    TestUserDeleteAction.class, TestUserGetAction.class,
    TestUserSaveAction.class })
public class SecuritySuite {
}
