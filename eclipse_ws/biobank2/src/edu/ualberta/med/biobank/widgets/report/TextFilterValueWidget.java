package edu.ualberta.med.biobank.widgets.report;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public class TextFilterValueWidget implements FilterValueWidget {
    private final Text text;

    public TextFilterValueWidget(Composite parent) {
        text = new Text(parent, SWT.BORDER);
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        if (!text.isDisposed()) {
            ReportFilterValue value = new ReportFilterValue();
            value.setPosition(0);
            value.setValue(text.getText());
            values.add(value);
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        if (!text.isDisposed()) {
            for (ReportFilterValue value : values) {
                text.setText(value.getValue());
                break;
            }
        }
    }

    @Override
    public void addChangeListener(final ChangeListener<Object> changeListener) {
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                changeListener.handleEvent(null);
            }
        });
    }

    @Override
    public Control getControl() {
        return text;
    }
}
