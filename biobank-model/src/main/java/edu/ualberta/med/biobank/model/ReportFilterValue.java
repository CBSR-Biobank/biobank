package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "REPORT_FILTER_VALUE")
public class ReportFilterValue extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private Integer position;
    private String value;
    private String secondValue;

    @Column(name = "POSITION")
    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Column(name = "VALUE", columnDefinition = "TEXT")
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "SECOND_VALUE", columnDefinition = "TEXT")
    public String getSecondValue() {
        return this.secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }
}
