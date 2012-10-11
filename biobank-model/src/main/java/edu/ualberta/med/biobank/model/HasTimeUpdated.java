package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

public interface HasTimeUpdated {
    /**
     * @return the {@link Date} when the implementing instance was last updated
     *         in the database.
     */
    @NotNull(message = "{HasTimeUpdated.timeUpdated.NotNull}")
    public Date getTimeUpdated();

    public void setTimeUpdated(Date timeUpdated);
}
