package edu.ualberta.med.biobank.batchoperation;

import java.util.List;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

public interface IBatchOpPojoReader<T extends IBatchOpInputPojo> {

    public List<T> getPojos() throws ClientBatchOpErrorsException;

}
