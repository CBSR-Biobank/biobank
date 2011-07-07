package edu.ualberta.med.biobank.server.reports;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO used for Aliquot Request by CSV report.
 */
public class SpecimenRequest implements Serializable {

    /**
     * An attribute to allow serialization of the domain objects
     */
    private static final long serialVersionUID = 1234567890L;

    private String pnumber;
    private Date dateDrawn;
    private String sampleTypeNameShort;
    private long maxAliquots;

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
        return sampleTypeNameShort;
    }

    public void setSpecimenTypeNameShort(String sampleType) {
        this.sampleTypeNameShort = sampleType;
    }

    public long getMaxAliquots() {
        return maxAliquots;
    }

    public void setMaxAliquots(long maxAliquots) {
        this.maxAliquots = maxAliquots;
    }
}
