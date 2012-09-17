package edu.ualberta.med.biobank.common.action.batchoperation;

import java.io.Serializable;

public interface IBatchOpInputPojo extends Serializable {

    public abstract int getLineNumber();

    public abstract void setLineNumber(int lineNumber);

}