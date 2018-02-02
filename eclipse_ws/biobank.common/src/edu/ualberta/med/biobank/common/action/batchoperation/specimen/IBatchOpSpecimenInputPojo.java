package edu.ualberta.med.biobank.common.action.batchoperation.specimen;


public interface IBatchOpSpecimenInputPojo extends IBatchOpSpecimenPositionPojo {

    String getParentInventoryId();

    String getPatientNumber();

    void setOriginCenter(String nameShort);

    void setCurrentCenter(String nameShort);

}
