package edu.ualberta.med.biobank.widgets.report;

import java.util.Collection;

import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.model.ReportFilterValue;

interface FilterValueWidget {
    public Collection<ReportFilterValue> getValues();

    public void setValues(Collection<ReportFilterValue> values);

    public void addChangeListener(ChangeListener<ChangeEvent> changeListener);

    public Control getControl();

    public boolean isValid(ReportFilterValue value);
}
