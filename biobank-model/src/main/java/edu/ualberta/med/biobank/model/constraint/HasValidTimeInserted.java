package edu.ualberta.med.biobank.model.constraint;

import java.util.Date;

import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.HasTimeInserted;

public interface HasValidTimeInserted extends HasTimeInserted {
    @Override
    @NotNull(message = "{constraint.HasValidTimeInserted.timeInserted.NotNull}")
    public Date getTimeInserted();
}
