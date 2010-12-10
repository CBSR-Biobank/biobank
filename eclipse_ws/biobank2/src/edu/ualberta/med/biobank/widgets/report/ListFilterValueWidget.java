package edu.ualberta.med.biobank.widgets.report;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public class ListFilterValueWidget extends
    AbstractSelectableFilterValueWidget<ListViewer> {
    public ListFilterValueWidget(Composite parent) {
        super(new ListViewer(parent, SWT.READ_ONLY));
    }

    @Override
    public void addChangeListener(
        final ChangeListener<ChangeEvent> changeListener) {
        getViewer().getList().addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeListener.handleEvent(null);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                changeListener.handleEvent(null);
            }
        });
    }

    @Override
    protected void clearViewer() {
        getViewer().getList().removeAll();
    }

    @Override
    public String toString(ReportFilterValue value) {
        return value.getValue();
    }
}
