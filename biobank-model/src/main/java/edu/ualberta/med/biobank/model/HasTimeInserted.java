package edu.ualberta.med.biobank.model;

import javax.validation.constraints.NotNull;

public interface HasTimeInserted {
    /**
     * @return when the implementing instance was inserted into the database, in
     *         milliseconds.
     */
    @NotNull(message = "{HasTimeInserted.timeInserted.NotNull}")
    public Long getTimeInserted();

    public void setTimeInserted(Long timeInserted);
}
