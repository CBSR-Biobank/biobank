package edu.ualberta.med.biobank.common.action.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.i18n.LString;

public class BatchOpErrorsException extends ActionException {
    private static final long serialVersionUID = 1L;

    private Set<BatchOpException<LString>> errors;

    public BatchOpErrorsException(Set<BatchOpException<LString>> errors) {
        super(null);
        addErrors(errors);
    }

    public void addErrors(Set<BatchOpException<LString>> errors) {
        this.errors = errors;
    }

    public Set<BatchOpException<LString>> getErrors() {
        return errors;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    @Override
    @SuppressWarnings("nls")
    public String getMessage() {
        List<String> messages = new ArrayList<String>();
        for (BatchOpException<LString> error : errors) {
            messages.add(error.getMessage().toString());
        }
        return StringUtils.join(messages, ": ");
    }

}
