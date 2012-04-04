package edu.ualberta.med.biobank.widgets.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public class ComboFilterValueWidget implements FilterValueWidget {
    private static final String DEFAULT_TEXT = "<select or enter value>";
    private final ComboViewer comboViewer;
    private boolean isShowingDefaultText;

    public ComboFilterValueWidget(Composite parent) {
        comboViewer = new ComboViewer(parent, SWT.NONE);

        final Combo combo = comboViewer.getCombo();

        isShowingDefaultText = true;
        combo.setText(DEFAULT_TEXT);

        addListeners();
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        ISelection selection = comboViewer.getSelection();
        if (selection instanceof IStructuredSelection) {
            Iterator<?> it = ((IStructuredSelection) selection).iterator();
            int position = 0;
            while (it.hasNext()) {
                String selectedText = (String) it.next();
                if (selectedText != null && !selectedText.isEmpty()) {
                    ReportFilterValue value = new ReportFilterValue();
                    value.setPosition(position);
                    value.setValue(selectedText);

                    values.add(value);

                    position++;
                }
            }
        }
        if (values.isEmpty() && !comboViewer.getCombo().isDisposed()
            && !isShowingDefaultText) {
            String text = comboViewer.getCombo().getText().trim();
            if (!text.isEmpty()) {
                ReportFilterValue value = new ReportFilterValue();
                value.setValue(text);
                value.setPosition(0);
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        Combo combo = comboViewer.getCombo();
        if (!combo.isDisposed()) {
            combo.setText(""); 
            for (ReportFilterValue value : values) {
                if (value != null && value.getValue() != null) {
                    combo.setText(value.getValue());
                }
                break;
            }
        }
    }

    @Override
    public void addChangeListener(
        final ChangeListener<ChangeEvent> changeListener) {
        comboViewer.getCombo().addModifyListener(new ModifyListener() {
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

    @Override
    public boolean isValid(ReportFilterValue value) {
        return value.getValue() != null && !value.getValue().isEmpty()
            && value.getSecondValue() == null;
    }

    @Override
    public String toString(ReportFilterValue value) {
        return value.getValue();
    }

    public ComboViewer getComboViewer() {
        return comboViewer;
    }

    private void addListeners() {
        final Combo combo = comboViewer.getCombo();

        combo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (combo.getText().equals(DEFAULT_TEXT)) {
                    combo.setText(""); 
                    isShowingDefaultText = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (combo.getText().isEmpty()) {
                    combo.setText(DEFAULT_TEXT);
                    isShowingDefaultText = true;
                }
            }
        });

        combo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                isShowingDefaultText = false;
                if (combo.getText().isEmpty() && !combo.isFocusControl()) {
                    combo.setText(DEFAULT_TEXT);
                    isShowingDefaultText = true;
                }
            }
        });
    }
}
