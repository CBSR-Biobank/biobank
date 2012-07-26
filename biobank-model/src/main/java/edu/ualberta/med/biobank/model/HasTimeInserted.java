package edu.ualberta.med.biobank.model;

import java.util.Date;

public interface HasTimeInserted {
    /**
     * @return the {@link Date} when the implementing instance was inserted into
     *         the database.
     */
    public Date getTimeInserted();

    public void setTimeInserted(Date timeInserted);
}
