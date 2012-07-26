package edu.ualberta.med.biobank.model.constraint;

import java.util.Date;

import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.HasInsertTime;

public interface HasValidInsertTime extends HasInsertTime {
    @Override
    @NotNull(message = "{constraint.HasValidInsertTime.insertTime.NotNull}")
    public Date getInsertTime();
}
