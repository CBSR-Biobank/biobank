package edu.ualberta.med.biobank.model.constraint;

import java.util.Date;

import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.HasTimeUpdated;

public interface HasValidTimeUpdated extends HasTimeUpdated {
    @Override
    @NotNull(message = "{constraint.HasValidTimeUpdated.timeUpdated.NotNull}")
    public Date getTimeUpdated();
}
