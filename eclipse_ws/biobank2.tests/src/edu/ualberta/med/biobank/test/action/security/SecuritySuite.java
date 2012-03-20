package edu.ualberta.med.biobank.test.action.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestGroup.class, TestRoleGetAllAction.class,
    TestUser.class })
public class SecuritySuite {
}
