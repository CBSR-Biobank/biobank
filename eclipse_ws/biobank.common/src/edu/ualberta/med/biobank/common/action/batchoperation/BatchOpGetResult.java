package edu.ualberta.med.biobank.common.action.batchoperation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.AbstractBiobankModel;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.FileMetaData;

public class BatchOpGetResult<T extends AbstractBiobankModel>
    implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final String executedBy;
    private final Date timeExecuted;
    private final FileMetaData input;
    private final List<T> modelObjects = new ArrayList<T>();

    public BatchOpGetResult(BatchOperation batch, FileMetaData input, List<T> modelObjects) {
        this.executedBy = batch.getExecutedBy().getLogin();
        this.timeExecuted = batch.getTimeExecuted();
        this.input = input;
        this.modelObjects.addAll(modelObjects);
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

    public List<T> getModelObjects() {
        return modelObjects;
    }
}
