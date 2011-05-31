package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankException;

public interface PreQueryTask {
    public void beforeExecute() throws BiobankException;
}
