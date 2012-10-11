package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

public interface HasTimeInserted {
    /**
     * @return the {@link Date} when the implementing instance was inserted into
     *         the database.
     */
    @NotNull(message = "{HasTimeInserted.timeInserted.NotNull}")
    public Date getTimeInserted();

    public void setTimeInserted(Date timeInserted);
}
