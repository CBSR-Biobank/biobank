package edu.ualberta.med.biobank.test.validation;

import org.junit.After;
import org.junit.Before;

import edu.ualberta.med.biobank.test.action.TestAction;

// extends TestAction right now only for simplicity, funcitonality from TestAction should be moved into a super class so TestValidation and TestAction are sisters.
public class TestValidation extends TestAction {
    protected Factory factory;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        factory = new Factory(session, getMethodNameR());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        factory = null;
    }
}
