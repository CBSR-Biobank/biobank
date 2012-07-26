package edu.ualberta.med.biobank.model.constraint;

import java.util.Date;

import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.HasLastUpdateTime;

public interface HasValidLastUpdateTime extends HasLastUpdateTime {
    @Override
    @NotNull(message = "{constraint.HasValidLastUpdateTime.lastUpdateTime.NotNull}")
    public Date getLastUpdateTime();
}
