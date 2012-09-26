package edu.ualberta.med.biobank.batchoperation;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.ICsvBeanReader;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

public interface IBatchOpPojoReader<T extends IBatchOpInputPojo> {

    public void setFilename(String filename);

    public void setReader(ICsvBeanReader reader);

    public List<T> getPojos() throws ClientBatchOpErrorsException, IOException;

    public ClientBatchOpInputErrorList getErrorList();

    public void preExecution();

    public void postExecution();

}
