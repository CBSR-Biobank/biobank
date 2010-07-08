package edu.ualberta.med.biobank.common.util;

public class DateRangePostProcess extends PostProcess {

    private static final long serialVersionUID = 1L;
    private boolean groupByYear;

    public DateRangePostProcess(boolean groupByYear) {
        this.groupByYear = groupByYear;
    }

    @Override
    public Object postProcess(Object object) {
        if (object != null) {
            Object[] castOb = (Object[]) object;
            if (groupByYear) {
                return new Object[] { castOb[0], castOb[1], castOb[3],
                    castOb[4] };
            } else {
                return new Object[] { castOb[0], castOb[1],
                    castOb[3] + "-" + castOb[2], castOb[4] };
            }
        }
        return null;
    }

}
