package edu.ualberta.med.biobank.reports.filters.types;

public class DoubleFilterType extends NumberFilterType<Double> {
    @Override
    protected Double getNumber(String string) {
        return Double.parseDouble(string);
    }
}
