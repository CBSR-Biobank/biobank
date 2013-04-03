package edu.ualberta.med.biobank.forms;

import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.batchoperation.patient.PatientBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;
import edu.ualberta.med.biobank.model.Center;

public class PatientImportForm extends ImportForm {
    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientImportForm";

    @Override
    protected IBatchOpPojoReader<? extends IBatchOpInputPojo> getCsvPojoReader(Center center,
        String csvFilename, String[] csvHeaders) {
        return new PatientBatchOpPojoReader(center, csvFilename);
    }

}
