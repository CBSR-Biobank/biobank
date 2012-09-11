package edu.ualberta.med.biobank.common.action.tecan;

import edu.ualberta.med.biobank.common.ICsvRow;

public class CbsrTecanCsvRow implements ICsvRow {

    private int lineNumber;
    private String rackId;
    private String cavityId;
    private String position;
    private String sourceId;
    private Integer concentration;
    private String concentrationUnit;
    private Integer volume;

    // this is the patient number
    private String userDefined1;

    private String userDefined2;
    private String userDefined3;
    private String userDefined4;
    private String userDefined5;
    private String plateErrors;
    private String samplEerrors;
    private Integer sampleInstanceId;
    private Integer sampleId;

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public String getCavityId() {
        return cavityId;
    }

    public void setCavityId(String cavityId) {
        this.cavityId = cavityId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getConcentration() {
        return concentration;
    }

    public void setConcentration(Integer concentration) {
        this.concentration = concentration;
    }

    public String getConcentrationUnit() {
        return concentrationUnit;
    }

    public void setConcentrationUnit(String concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public String getUserDefined1() {
        return userDefined1;
    }

    public void setUserDefined1(String userDefined1) {
        this.userDefined1 = userDefined1;
    }

    public String getUserDefined2() {
        return userDefined2;
    }

    public void setUserDefined2(String userDefined2) {
        this.userDefined2 = userDefined2;
    }

    public String getUserDefined3() {
        return userDefined3;
    }

    public void setUserDefined3(String userDefined3) {
        this.userDefined3 = userDefined3;
    }

    public String getUserDefined4() {
        return userDefined4;
    }

    public void setUserDefined4(String userDefined4) {
        this.userDefined4 = userDefined4;
    }

    public String getUserDefined5() {
        return userDefined5;
    }

    public void setUserDefined5(String userDefined5) {
        this.userDefined5 = userDefined5;
    }

    public String getPlateErrors() {
        return plateErrors;
    }

    public void setPlateErrors(String plateErrors) {
        this.plateErrors = plateErrors;
    }

    public String getSamplEerrors() {
        return samplEerrors;
    }

    public void setSamplEerrors(String samplEerrors) {
        this.samplEerrors = samplEerrors;
    }

    public Integer getSampleInstanceId() {
        return sampleInstanceId;
    }

    public void setSampleInstanceId(Integer sampleInstanceId) {
        this.sampleInstanceId = sampleInstanceId;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

}
