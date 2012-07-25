package edu.ualberta.med.biobank.reports.filters.types;

public class IntegerFilterType extends NumberFilterType<Integer> {
    @Override
    protected Integer getNumber(String string) {
        return Integer.parseInt(string);
    }
}
