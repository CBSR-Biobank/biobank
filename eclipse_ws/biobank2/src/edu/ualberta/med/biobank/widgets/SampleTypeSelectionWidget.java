package edu.ualberta.med.biobank.widgets;

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

/**
 * Create 3 widgets to show types selection for samples on a pallet: one label,
 * one combo with different types and one text showing total number of samples
 * found
 */
public class SampleTypeSelectionWidget {
    private Combo combo;
    private ComboViewer cv;
    private ControlDecoration controlDecoration;
    private Label textNumber;
    private Integer number;

    private IObservableValue selectionDone = new WritableValue(Boolean.TRUE,
        Boolean.class);
    private Binding binding;
    private Object nextWidget;

    public SampleTypeSelectionWidget(Composite parent, Character letter,
        List<SampleTypeWrapper> types, FormToolkit toolkit) {

        if (letter != null) {
            toolkit.createLabel(parent, letter.toString(), SWT.LEFT);
        }

        createCombo(parent, types);
        toolkit.adapt(combo, true, true);

        textNumber = toolkit.createLabel(parent, "", SWT.RIGHT | SWT.BORDER);
        GridData data = new GridData();
        data.widthHint = 20;
        data.horizontalAlignment = SWT.LEFT;
        textNumber.setLayoutData(data);

        controlDecoration = BiobankWidget.createDecorator(combo,
            "A sample type should be selected");
        setNumber(null);
    }

    private void createCombo(Composite parent, List<SampleTypeWrapper> types) {
        combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        combo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

        cv = new ComboViewer(combo);
        cv.setContentProvider(new ArrayContentProvider());
        cv.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SampleTypeWrapper) element).getName();
            }
        });
        cv.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() == null
                    || ((IStructuredSelection) event.getSelection()).size() == 0) {
                    selectionDone.setValue(false);
                } else {
                    selectionDone.setValue(true);
                }
            }
        });
        cv.setComparator(new ViewerComparator());
        cv.setInput(types);
        combo.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT
                    || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = setNextFocus();
                }
            }
        });
    }

    private boolean setNextFocus() {
        if (nextWidget != null) {
            if (nextWidget instanceof Control) {
                ((Control) nextWidget).setFocus();
                return false; // cancel doit
            } else if (nextWidget instanceof SampleTypeSelectionWidget) {
                ((SampleTypeSelectionWidget) nextWidget).combo.setFocus();
            }
        }
        return true;
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        cv.addSelectionChangedListener(listener);
    }

    public void setNumber(Integer number) {
        this.number = number;
        String text = "";
        if (number != null) {
            text = number.toString();
        }
        if (number == null || number == 0) {
            combo.setEnabled(false);
            selectionDone.setValue(true);
        } else {
            combo.setEnabled(true);
            selectionDone.setValue(false);
        }
        textNumber.setText(text);
    }

    /**
     * return true if this selection need to be save, i.e. number > 0
     */
    public boolean needToSave() {
        if (number == null) {
            return false;
        } else {
            return number > 0;
        }
    }

    public SampleTypeWrapper getSelection() {
        return (SampleTypeWrapper) ((StructuredSelection) cv.getSelection())
            .getFirstElement();
    }

    public void addBinding(WidgetCreator dbc) {
        if (binding == null) {
            WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
            UpdateValueStrategy uvs = new UpdateValueStrategy();
            uvs.setAfterGetValidator(new IValidator() {
                @Override
                public IStatus validate(Object value) {
                    if (value instanceof Boolean && !(Boolean) value) {
                        controlDecoration.show();
                        return ValidationStatus
                            .error("Types should be selected");
                    } else {
                        controlDecoration.hide();
                        return Status.OK_STATUS;
                    }
                }

            });
            binding = dbc.bindValue(wv, selectionDone, uvs, uvs);
        } else {
            dbc.addBinding(binding);
        }
    }

    public void removeBinding(WidgetCreator dbc) {
        if (binding != null) {
            dbc.removeBinding(binding);
        }
    }

    public void resetValues(boolean resetNumber) {
        cv.setSelection(null);
        if (resetNumber) {
            setNumber(null);
        } else {
            setNumber(number); // to re-do the validation tests
        }
    }

    public void setNextWidget(Object nextWidget) {
        this.nextWidget = nextWidget;
    }
}
