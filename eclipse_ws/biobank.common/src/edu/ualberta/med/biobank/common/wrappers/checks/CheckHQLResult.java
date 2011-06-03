package edu.ualberta.med.biobank.common.wrappers.checks;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.BiobankHQLAction;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.UnexpectedHQLResultsException;

public class CheckHQLResult extends BiobankHQLAction {
    private static final long serialVersionUID = 1L;

    private final String errMsg;
    private final List<?> expected;

    /**
     * 
     * @param expected expected {@code List} of results.
     * @param msg the message shown in the {@code UnexpectedHQLResultsException}
     *            thrown if the results returned do not match the expected.
     * @param hql
     * @param parameters
     */
    CheckHQLResult(List<?> expected, String msg, String hql,
        Object... parameters) {
        super(hql, parameters);
        this.expected = expected;
        this.errMsg = msg;
    }

    @Override
    public Object doResults(List<?> results) throws BiobankSessionException {
        if (!results.equals(expected))
            throw new UnexpectedHQLResultsException(errMsg);
        return null;
    }
}
