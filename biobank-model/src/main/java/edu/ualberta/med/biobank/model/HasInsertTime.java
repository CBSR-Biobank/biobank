package edu.ualberta.med.biobank.model;

public interface HasInsertTime {
    /**
     * Use a {@link Long} instead of {@link Date} because MySQL's DATETIME type
     * is precise only to the second, not the millisecond. Millisecond precision
     * is important for the logs.
     * 
     * @return
     */
    // TODO: write UserType to convert BIGINT to Date
    public Long getTimestamp();

    public void setTimestamp(Long insertTime);
}
