package edu.ualberta.med.biobank.test.action.csvimport;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import edu.ualberta.med.biobank.common.action.exception.CsvImportException;
import edu.ualberta.med.biobank.common.action.exception.CsvImportException.ImportError;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.LString;

public class AssertCsvImportException {

    private final Set<LString> expectedMessages = new HashSet<LString>(0);

    private final Set<String> errorMessages = new HashSet<String>(0);

    public AssertCsvImportException withMessage(LString message) {
        expectedMessages.add(message);
        return this;
    }

    public void assertIn(CsvImportException e) {
        for (ImportError ie : e.getErrors()) {
            errorMessages.add(ie.getMessage().toString());
        }
        assertIn(e.getErrors());
    }

    private void assertIn(Set<ImportError> errors) {
        boolean found = false;
        for (ImportError error : errors) {
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
    public String toString() {
        return StringUtil.join(expectedMessages, "\r\n");
    }

}
