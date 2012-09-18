package edu.ualberta.med.biobank.common.action.batchoperation;

import java.io.Serializable;

/**
 * Interface for a BatchOp Pojo. These pojos are used by the BatchOp actions to
 * save information to the database.
 * 
 * @author Nelson Loyola
 * 
 */
public interface IBatchOpInputPojo extends Serializable {

    public abstract int getLineNumber();

    public abstract void setLineNumber(int lineNumber);

}