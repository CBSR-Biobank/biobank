package edu.ualberta.med.biobank.widgets.report;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public class SetFilterValueWidget implements FilterValueWidget {
    private final Composite container;
    private final FilterValueWidget decoratedWidget;

    public SetFilterValueWidget(Composite parent,
        FilterValueWidget decoratedWidget) {
        this.decoratedWidget = decoratedWidget;

        this.container = new Composite(parent, SWT.NONE);
        decoratedWidget.getControl().setParent(container);
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addChangeListener(ChangeListener<Object> changeListener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Control getControl() {
        // TODO Auto-generated method stub
        return null;
    }
}
