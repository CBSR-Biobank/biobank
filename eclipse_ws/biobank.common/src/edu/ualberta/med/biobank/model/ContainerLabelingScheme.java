package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

// TODO: should be an enum?
@Entity
@Table(name = "CONTAINER_LABELING_SCHEME")
public class ContainerLabelingScheme extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Integer minChars;
    private Integer maxChars;
    private Integer maxRows;
    private Integer maxCols;
    private Integer maxCapacity;

    @Column(name = "NAME", length = 50, unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "MIN_CHARS")
    public Integer getMinChars() {
        return this.minChars;
    }

    public void setMinChars(Integer minChars) {
        this.minChars = minChars;
    }

    @Column(name = "MAX_CHARS")
    public Integer getMaxChars() {
        return this.maxChars;
    }

    public void setMaxChars(Integer maxChars) {
        this.maxChars = maxChars;
    }

    @Column(name = "MAX_ROWS")
    public Integer getMaxRows() {
        return this.maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    @Column(name = "MAX_COLS")
    public Integer getMaxCols() {
        return this.maxCols;
    }

    public void setMaxCols(Integer maxCols) {
        this.maxCols = maxCols;
    }

    @Column(name = "MAX_CAPACITY")
    public Integer getMaxCapacity() {
        return this.maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
