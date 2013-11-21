package edu.ualberta.med.biobank.gui.common.widgets;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import edu.ualberta.med.biobank.gui.common.widgets.databinding.RadioGroupObservableValue;

/**
 * A widget that displays a group containing radio button options and allows the user to select a
 * single value.
 * 
 * @author loyola
 * 
 * @param <T> The return type
 */
public class GroupedRadioSelectionWidget<T> extends Composite {

    private final GridData gridData;

    private final Map<T, Button> buttons;

    private final WritableValue value;

    private final RadioGroup radioGroup;

    public GroupedRadioSelectionWidget(
        Composite parent,
        String groupLabel, Map<T, String> values,
        T initialValue) {
        super(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        setLayout(layout);

        gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        setLayoutData(gridData);
        final Group g = new Group(this, SWT.SHADOW_ETCHED_IN);
        g.setLayout(new GridLayout(1, false));
        g.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        g.setText(groupLabel);

        buttons = new LinkedHashMap<T, Button>(values.size());
        for (Entry<T, String> entry : values.entrySet()) {
            Button button = new Button(g, SWT.RADIO);
            button.setText(entry.getValue());
            buttons.put(entry.getKey(), button);
        }

        radioGroup = new RadioGroup(buttons.values().toArray(), values.keySet().toArray());
        radioGroup.setSelection(initialValue);
        DataBindingContext bindingContext = new DataBindingContext();
        value = new WritableValue(initialValue, null);
        bindingContext.bindValue(new RadioGroupObservableValue(radioGroup), value, null, null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        gridData.exclude = !visible;
    }

    @SuppressWarnings("nls")
    public T getSelection() {
        for (Entry<T, Button> es : buttons.entrySet()) {
            if (es.getValue().getSelection()) {
                return es.getKey();
            }
        }
        throw new IllegalStateException("nothing was selected");
    }

    public void setSelection(T selection) {
        radioGroup.setSelection(selection);
    }

    public void addSelectionListener(SelectionListener listener) {
        radioGroup.addSelectionListener(listener);
    }
}
