package edu.ualberta.med.biobank.common.util;

import java.text.DecimalFormat;

public class CapacityPostProcess extends AbstractRowPostProcess {

    private static final long serialVersionUID = 1L;
    private int col1;
    private int col2;

    public CapacityPostProcess(Integer col1, Integer col2) {
        this.col1 = col1;
        this.col2 = col2;
    }

    @Override
    public Object rowPostProcess(Object object) {
        if (object != null) {
            Object[] castOb = (Object[]) object;
            Object[] rowObject = new Object[castOb.length + 1];
            DecimalFormat format = new DecimalFormat("#0.00%"); //$NON-NLS-1$
            for (int i = 0; i < castOb.length; i++)
                rowObject[i] = castOb[i];
            rowObject[castOb.length] = format.format(((Long) castOb[col2])
                * 1.0 / ((Long) castOb[col1]));
            return rowObject;
        }
        return null;
    }
}
