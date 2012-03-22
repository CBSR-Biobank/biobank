package edu.ualberta.med.biobank.test;

import java.util.Random;

import org.junit.Rule;
import org.junit.rules.TestName;

public class TestBase {
    private static final Random R = new Random();

    @Rule
    public final TestName testName = new TestName();

    protected String getMethodName() {
        return testName.getMethodName();
    }

    protected String getMethodNameR() {
        return testName.getMethodName() + R.nextInt();
    }

    protected static Random getR() {
        return R;
    }
}
