package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

public interface IBatchOpSpecimenInputPojo extends IBatchOpInputPojo {

    String getParentInventoryId();

    String getPatientNumber();

}
