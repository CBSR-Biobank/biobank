package edu.ualberta.med.biobank.model;

import java.util.Date;

public interface HasTimeUpdated {
    /**
     * @return the {@link Date} when the implementing instance was last updated
     *         in the database.
     */
    public Date getTimeUpdated();

    public void setTimeUpdated(Date timeUpdated);
}
