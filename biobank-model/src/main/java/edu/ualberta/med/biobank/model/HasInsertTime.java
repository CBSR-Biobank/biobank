package edu.ualberta.med.biobank.model;

import java.util.Date;

public interface HasInsertTime {
    /**
     * @return the {@link Date} when the implementing instance was inserted into
     *         the database.
     */
    public Date getInsertTime();

    public void setInsertTime(Date insertTime);
}
