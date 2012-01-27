package edu.ualberta.med.biobank.model;

// TODO: convert to an enum? Not sure...
public class ContainerLabelingScheme extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Integer minChars;
    private Integer maxChars;
    private Integer maxRows;
    private Integer maxCols;
    private Integer maxCapacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinChars() {
        return minChars;
    }

    public void setMinChars(Integer minChars) {
        this.minChars = minChars;
    }

    public Integer getMaxChars() {
        return maxChars;
    }

    public void setMaxChars(Integer maxChars) {
        this.maxChars = maxChars;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public Integer getMaxCols() {
        return maxCols;
    }

    public void setMaxCols(Integer maxCols) {
        this.maxCols = maxCols;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
