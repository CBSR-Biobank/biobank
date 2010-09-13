package edu.ualberta.med.biobank.server.reports;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO used for Aliquot Request by CSV report.
 */
public class AliquotRequest implements Serializable {

    /**
     * An attribute to allow serialization of the domain objects
     */
    private static final long serialVersionUID = 1234567890L;

    private String pnumber;
    private Date dateDrawn;
    private String sampleTypeNameShort;
    private int maxAliquots;

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

    public String getSampleTypeNameShort() {
        return sampleTypeNameShort;
    }

    public void setSampleTypeNameShort(String sampleType) {
        this.sampleTypeNameShort = sampleType;
    }

    public int getMaxAliquots() {
        return maxAliquots;
    }

    public void setMaxAliquots(int maxAliquots) {
        this.maxAliquots = maxAliquots;
    }
}
