package edu.ualberta.med.biobank.batchoperation;

import java.util.List;

import org.supercsv.io.ICsvBeanReader;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

public interface IBatchOpPojoReader<T extends IBatchOpInputPojo> {

    public void setReader(ICsvBeanReader reader);

    public List<T> getPojos() throws ClientBatchOpErrorsException;

    public ClientBatchOpInputErrorList getErrorList();

    public void preExecution();

    public void postExecution();

}
