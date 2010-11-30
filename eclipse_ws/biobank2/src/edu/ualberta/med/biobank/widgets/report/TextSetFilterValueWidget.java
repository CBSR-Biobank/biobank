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

public class TextSetFilterValueWidget extends Text implements FilterValueWidget {
    public TextSetFilterValueWidget(Composite parent) {
        super(parent, SWT.NONE);
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        if (!isDisposed()) {
            String[] rawValues = getText().split(",");
            int position = 0;
            for (String rawValue : rawValues) {
                ReportFilterValue value = new ReportFilterValue();
                value.setPosition(position);
                value.setValue(rawValue.trim());
                values.add(value);
                position++;
            }
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        if (!isDisposed()) {
            final String delimiter = ", ";
            StringBuilder builder = new StringBuilder();
            for (ReportFilterValue value : values) {
                builder.append(value.getValue());
                builder.append(delimiter);
            }
            builder.delete(builder.length() - delimiter.length() - 1,
                builder.length());
            setText(builder.toString());
        }
    }

    @Override
    public void addChangeListener(final ChangeListener<Object> changeListener) {
        addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                changeListener.handleEvent(null);
            }
        });
    }

    @Override
    public Control getControl() {
        return this;
    }
}
