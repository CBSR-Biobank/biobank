package edu.ualberta.med.biobank.common.util;

public class DateRangeRowPostProcess extends AbstractRowPostProcess {

    private static final long serialVersionUID = 1L;
    private boolean groupByYear;
    private int initialYearPosition;

    public DateRangeRowPostProcess(boolean groupByYear, int initialYearPosition) {
        this.groupByYear = groupByYear;
        this.initialYearPosition = initialYearPosition;
    }

    @Override
    public Object rowPostProcess(Object object) {
        if (object != null) {
            Object[] castOb = (Object[]) object;
            Object[] rowObject = new Object[castOb.length - 1];
            if (groupByYear) {
                rowObject[initialYearPosition] = castOb[initialYearPosition];
            } else {
                rowObject[initialYearPosition] = castOb[initialYearPosition + 1]
                    + "-" + castOb[initialYearPosition]; //$NON-NLS-1$
            }
            for (int i = 0; i < castOb.length; i++) {
                if (i < initialYearPosition) {
                    rowObject[i] = castOb[i];
                } else if (i > initialYearPosition + 1) {
                    rowObject[i - 1] = castOb[i];
                }
            }
            return rowObject;
        }
        return null;
    }
}
