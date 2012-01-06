package edu.ualberta.med.biobank.server.applicationservice;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TecanCSV implements Serializable {

    private static final long serialVersionUID = 1234567890L;

    private String orgSample;
    private String processId;
    private String aliquotId;
    private String aliquotType;
    private double volume;
    private Date startProcess;
    private Date endProcess;

    public String getOrgSample() {
        return orgSample;
    }

    public String getProcessId() {
        return processId;
    }

    public String getAliquotId() {
        return aliquotId;
    }

    public String getAliquotType() {
        return aliquotType;
    }

    public double getVolume() {
        return volume;
    }

    public Date getStartProcess() {
        return startProcess;
    }

    public Date getEndProcess() {
        return endProcess;
    }

    public void setOrgSample(final String orgSample) {
        this.orgSample = orgSample;
    }

    public void setProcessId(final String processId) {
        this.processId = processId;
    }

    public void setAliquotId(final String aliquotId) {
        this.aliquotId = aliquotId;
    }

    public void setAliquotType(final String aliquotType) {
        this.aliquotType = aliquotType;
    }

    public void setVolume(final double volume) {
        this.volume = volume;
    }

    public void setStartProcess(final Date startProcess) {
        this.startProcess = startProcess;
    }

    public void setEndProcess(final Date endProcess) {
        this.endProcess = endProcess;
    }

    public String getLine() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(startProcess);
        String sDate = formatter.format(cal.getTime());
        cal.setTime(endProcess);
        String eDate = formatter.format(cal.getTime());
        return orgSample + "," + processId + "," + aliquotId + ","
            + aliquotType + "," + volume + "," + sDate + "," + eDate;
    }
}
