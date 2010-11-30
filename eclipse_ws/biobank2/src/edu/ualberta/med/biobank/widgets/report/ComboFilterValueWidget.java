package edu.ualberta.med.biobank.widgets.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public class ComboFilterValueWidget implements FilterValueWidget {
    private final ComboViewer comboViewer;

    public ComboFilterValueWidget(Composite parent) {
        comboViewer = new ComboViewer(parent, SWT.NONE | SWT.READ_ONLY);
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        ISelection selection = comboViewer.getSelection();
        if (selection instanceof IStructuredSelection) {
            Iterator<?> it = ((IStructuredSelection) selection).iterator();
            int position = 0;
            while (it.hasNext()) {
                String suggestion = (String) it.next();

                ReportFilterValue value = new ReportFilterValue();
                value.setPosition(position);
                value.setValue(suggestion);

                values.add(value);

                position++;
            }
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        List<String> stringValues = new ArrayList<String>();
        for (ReportFilterValue value : values) {
            stringValues.add(value.getValue());
        }
        comboViewer.setSelection(new StructuredSelection(stringValues));
    }

    @Override
    public void addChangeListener(final ChangeListener<Object> changeListener) {
        comboViewer.getCCombo().addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                changeListener.handleEvent(null);
            }
        });
    }

    @Override
    public Control getControl() {
        return comboViewer.getControl();
    }

    public ComboViewer getComboViewer() {
        return comboViewer;
    }
}
