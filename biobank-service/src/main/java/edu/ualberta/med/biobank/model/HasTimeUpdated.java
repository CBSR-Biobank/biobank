package edu.ualberta.med.biobank.model;

import javax.validation.constraints.NotNull;

public interface HasTimeUpdated {
    /**
     * @return when the implementing instance was last updated in the database, in milliseconds.
     */
    @NotNull(message = "{HasTimeUpdated.timeUpdated.NotNull}")
    public Long getTimeUpdated();

    public void setTimeUpdated(Long timeUpdated);
}
