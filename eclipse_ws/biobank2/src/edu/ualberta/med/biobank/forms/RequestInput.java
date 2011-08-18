package edu.ualberta.med.biobank.forms;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO used for Aliquot Request by CSV report.
 */
public class RequestInput implements Serializable {

    /**
     * An attribute to allow serialization of the domain objects
     */
    private static final long serialVersionUID = 1234567890L;

    private String pnumber;
    private String inventoryID;
    private Date dateDrawn;
    private String specimenTypeNameShort;
    private String location;
    private String activityStatus;

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pNumber) {
        this.pnumber = pNumber;
    }

    public Date getDateDrawn() {
        return dateDrawn;
    }

    public void setDateDrawn(Date dateDrawn) {
        this.dateDrawn = dateDrawn;
    }

    public String getSpecimenTypeNameShort() {
        return specimenTypeNameShort;
    }

    public void setSpecimenTypeNameShort(String specimenType) {
        this.specimenTypeNameShort = specimenType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }
}
