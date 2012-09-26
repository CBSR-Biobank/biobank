package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.FileMetaData;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenBatchOpGetResult
    implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final String executedBy;
    private final Date timeExecuted;
    private final FileMetaData input;
    private final List<Specimen> specimens = new ArrayList<Specimen>();

    SpecimenBatchOpGetResult(BatchOperation batch,
        FileMetaData input, List<Specimen> specimens) {
        this.executedBy = batch.getExecutedBy().getLogin();
        this.timeExecuted = batch.getTimeExecuted();
        this.input = input;
        this.specimens.addAll(specimens);
    }
    
    public String getExecutedBy() {
        return executedBy;
    }

    public Date getTimeExecuted() {
        return timeExecuted;
    }

    public FileMetaData getInput() {
        return input;
    }

    public List<Specimen> getSpecimens() {
        return specimens;
    }
}
