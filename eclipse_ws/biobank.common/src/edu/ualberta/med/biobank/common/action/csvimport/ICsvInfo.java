package edu.ualberta.med.biobank.common.action.csvimport;

import java.io.Serializable;

public interface ICsvInfo extends Serializable {

    public abstract int getLineNumber();

    public abstract void setLineNumber(int lineNumber);

}