package edu.ualberta.med.biobank.test.action.batchoperation;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.LString;

public class AssertBatchOpException {

    private final Set<LString> expectedMessages = new HashSet<LString>(0);

    private final Set<String> errorMessages = new HashSet<String>(0);

    public AssertBatchOpException withMessage(LString message) {
        expectedMessages.add(message);
        return this;
    }

    public void assertIn(BatchOpErrorsException e) {
        for (BatchOpException<LString> ie : e.getErrors()) {
            errorMessages.add(ie.getMessage().toString());
        }
        assertIn(e.getErrors());
    }

    @SuppressWarnings("nls")
    private void assertIn(Set<BatchOpException<LString>> errors) {
        boolean found = false;
        for (BatchOpException<LString> error : errors) {
            found |= containsExpectedMessage(error.getMessage());
        }
        if (!found) {
            Assert.fail("Cannot find a error"
                + " with the message: \r\n" + this.toString()
                + "\r\nInstead, found the messages(s):\r\n"
                + StringUtil.join(errorMessages, "\r\n"));
        }
    }

    private boolean containsExpectedMessage(LString message) {
        for (LString expectedMessage : expectedMessages) {
            if (expectedMessage.equals(message)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return StringUtil.join(expectedMessages, "\r\n");
    }

}
