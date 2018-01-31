package edu.ualberta.med.biobank.common.action.batchoperation.specimen.position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.FileMetaData;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenPositionBatchOpGetResult implements ActionResult {
    private static final long serialVersionUID = 1L;

    public static class SpecimenInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;
        public Specimen specimen;
        public String   fullPositionString;
    }

    private final String executedBy;
    private final Date timeExecuted;
    private final FileMetaData input;
    private final List<SpecimenInfo> specimenData = new ArrayList<SpecimenInfo>();

    public SpecimenPositionBatchOpGetResult(BatchOperation batch,
                                            FileMetaData input, List<SpecimenInfo> specimenData) {
        this.executedBy = batch.getExecutedBy().getLogin();
        this.timeExecuted = batch.getTimeExecuted();
        this.input = input;
        this.specimenData.addAll(specimenData);
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

    public List<SpecimenInfo> getSpecimenData() {
        return specimenData;
    }

}
