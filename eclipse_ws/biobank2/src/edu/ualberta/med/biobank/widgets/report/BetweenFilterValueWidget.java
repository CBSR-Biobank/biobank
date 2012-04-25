package edu.ualberta.med.biobank.widgets.report;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.model.ReportFilterValue;

/**
 * Decorates two <code>FilterValueWidget</code>-s to provide between x and y
 * functionality.
 * 
 * @author jferland
 * 
 */
public class BetweenFilterValueWidget implements FilterValueWidget {
    private final Composite container;
    private final FilterValueWidget minValueWidget;
    private final FilterValueWidget maxValueWidget;

    public BetweenFilterValueWidget(Composite parent,
        FilterValueWidget minValueWidget, FilterValueWidget maxValueWidget) {
        this.minValueWidget = minValueWidget;
        this.maxValueWidget = maxValueWidget;

        container = createControls(parent);
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        ReportFilterValue value = new ReportFilterValue();
        value.setPosition(0);

        for (ReportFilterValue minValue : minValueWidget.getValues()) {
            value.setValue(minValue.getValue());
            for (ReportFilterValue maxValue : maxValueWidget.getValues()) {
                value.setSecondValue(maxValue.getValue());
                return Arrays.asList(value);
            }
        }

        return Arrays.asList();
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        for (ReportFilterValue value : values) {
            ReportFilterValue minValue = new ReportFilterValue();
            minValue.setPosition(0);
            minValue.setValue(value.getValue());
            minValueWidget.setValues(Arrays.asList(minValue));

            ReportFilterValue maxValue = new ReportFilterValue();
            maxValue.setPosition(0);
            maxValue.setValue(value.getSecondValue());
            maxValueWidget.setValues(Arrays.asList(maxValue));
            break;
        }
    }

    @Override
    public void addChangeListener(
        final ChangeListener<ChangeEvent> changeListener) {
        minValueWidget.addChangeListener(new ChangeListener<ChangeEvent>() {
            @Override
            public void handleEvent(ChangeEvent event) {
                changeListener.handleEvent(event);
            }
        });

        maxValueWidget.addChangeListener(new ChangeListener<ChangeEvent>() {
            @Override
            public void handleEvent(ChangeEvent event) {
                changeListener.handleEvent(event);
            }
        });
    }

    @Override
    public Control getControl() {
        return container;
    }

    @Override
    public boolean isValid(ReportFilterValue value) {
        return value.getValue() != null && !value.getValue().isEmpty()
            && value.getSecondValue() != null
            && !value.getSecondValue().isEmpty();
    }

    @SuppressWarnings("nls")
    @Override
    public String toString(ReportFilterValue value) {
        return "\"" + value.getValue() + "\" and \"" + value.getSecondValue()
            + "\"";
    }

    private Composite createControls(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 4;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalIndent = 0;
        layoutData.verticalIndent = 0;
        layoutData.grabExcessHorizontalSpace = true;

        Control minControl = minValueWidget.getControl();
        minControl.setParent(container);
        minControl.setLayoutData(layoutData);

        GridData labelLayoutData = new GridData();
        labelLayoutData.horizontalIndent = 4;
        labelLayoutData.verticalIndent = 0;

        Label label = new Label(container, SWT.NONE);
        label.setText("and "); //$NON-NLS-1$
        label.setLayoutData(labelLayoutData);

        Control maxControl = maxValueWidget.getControl();
        maxControl.setParent(container);
        maxControl.setLayoutData(layoutData);

        return container;
    }
}
