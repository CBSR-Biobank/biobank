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
        text.setToolTipText("For string values, use '%' as a wildcard character");
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        if (!text.isDisposed() && text.getText() != null) {
            String string = text.getText().trim();
            if (!string.isEmpty()) {
                ReportFilterValue value = new ReportFilterValue();
                value.setPosition(0);
                value.setValue(string);
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        if (!text.isDisposed()) {
            text.setText(""); 
            for (ReportFilterValue value : values) {
                if (value != null && value.getValue() != null) {
                    text.setText(value.getValue());
                }
                break;
            }
        }
    }

    @Override
    public void addChangeListener(
        final ChangeListener<ChangeEvent> changeListener) {
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

    @Override
    public boolean isValid(ReportFilterValue value) {
        return value.getValue() != null && !value.getValue().isEmpty()
            && value.getSecondValue() == null;
    }

    @Override
    public String toString(ReportFilterValue value) {
        return value.getValue();
    }
}
