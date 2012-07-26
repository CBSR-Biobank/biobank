package edu.ualberta.med.biobank.model;

import java.util.Date;

public interface HasLastUpdateTime {
    /**
     * @return the {@link Date} when the implementing instance was last updated
     *         in the database.
     */
    public Date getLastUpdateTime();

    public void setLastUpdateTime(Date lastUpdateTime);
}
