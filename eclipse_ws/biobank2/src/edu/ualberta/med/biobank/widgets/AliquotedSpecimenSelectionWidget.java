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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;

/**
 * Create 3 widgets to show types selection for samples on a pallet: one label,
 * one combo with different types and one text showing total number of samples
 * found
 */
public class AliquotedSpecimenSelectionWidget {
    private ComboViewer cvSource;
    private ComboViewer cvResult;
    private ControlDecoration controlDecoration;
    private Label textNumber;
    private Integer number;

    private IObservableValue selectionsDone = new WritableValue(Boolean.TRUE,
        Boolean.class);
    // [source|result]
    private Boolean[] selections = new Boolean[2];
    private Binding binding;
    private Object nextWidget;

    public SampleTypeSelectionWidget(Composite parent, Character letter,
        List<SpecimenTypeWrapper> types, FormToolkit toolkit) {

        if (letter != null) {
            toolkit.createLabel(parent, letter.toString(), SWT.LEFT);
        }

        cvSource = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY
            | SWT.BORDER);
        setComboProperties(cvSource, toolkit, sourceTypes, 0);

        cvResult = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY
            | SWT.BORDER);
        setComboProperties(cvResult, toolkit, resultTypes, 1);

        textNumber = toolkit.createLabel(parent, "", SWT.BORDER);
        GridData data = new GridData();
        data.widthHint = 20;
        data.horizontalAlignment = SWT.LEFT;
        textNumber.setLayoutData(data);

        setNumber(null);

        controlDecoration = BiobankWidget
            .createDecorator(textNumber,
                "A source specimen type and an aliquoted specimen type should be selected");
    }

    private void setComboProperties(ComboViewer cv, FormToolkit toolkit,
        List<?> types, final int selectionPosition) {
        cv.getControl().setLayoutData(
            new GridData(SWT.FILL, SWT.TOP, true, false));
        toolkit.adapt(cv.getControl(), true, true);
        cv.setContentProvider(new ArrayContentProvider());
        cv.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SpecimenTypeWrapper) element).getName();
            }
        });
        cv.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() == null
                    || ((IStructuredSelection) event.getSelection()).size() == 0) {
                    selections[selectionPosition] = false;
                } else {
                    selections[selectionPosition] = true;
                }
                selectionsDone.setValue(selections[0] && selections[1]);
            }
        });
        cv.setComparator(new ViewerComparator());
        cv.setInput(types);
        cv.getControl().addTraverseListener(new TraverseListener() {
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
            } else if (nextWidget instanceof AliquotedSpecimenSelectionWidget) {
                ((AliquotedSpecimenSelectionWidget) nextWidget).cvSource
                    .getControl().setFocus();
            }
        }
        return true;
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        cvResult.addSelectionChangedListener(listener);
    }

    public void setNumber(Integer number) {
        this.number = number;
        String text = "";
        if (number != null) {
            text = number.toString();
        }
        if (number == null || number == 0) {
            cvSource.getControl().setEnabled(false);
            cvResult.getControl().setEnabled(false);
            selectionsDone.setValue(true);
        } else {
            cvSource.getControl().setEnabled(true);
            cvResult.getControl().setEnabled(true);
            selectionsDone.setValue(getResultSelection() != null);
        }
        textNumber.setText(text);
    }

    public void increaseNumber() {
        if (number == null)
            number = 0;
        number++;
        setNumber(number);
        selectionsDone.setValue(getResultSelection() != null);
    }

    public boolean canFocus() {
        return cvSource.getControl().isEnabled();
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

<<<<<<< HEAD:eclipse_ws/biobank2/src/edu/ualberta/med/biobank/widgets/SampleTypeSelectionWidget.java
    public SpecimenTypeWrapper getSelection() {
        return (SpecimenTypeWrapper) ((StructuredSelection) cv.getSelection())
            .getFirstElement();
=======
    public SampleTypeWrapper getResultSelection() {
        return (SampleTypeWrapper) ((StructuredSelection) cvResult
            .getSelection()).getFirstElement();
    }

    public SourceVesselWrapper getSourceSelection() {
        return (SourceVesselWrapper) ((StructuredSelection) cvSource
            .getSelection()).getFirstElement();
>>>>>>> dev-aaron:eclipse_ws/biobank2/src/edu/ualberta/med/biobank/widgets/AliquotedSpecimenSelectionWidget.java
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
                            .error("Type should be selected");
                    } else {
                        controlDecoration.hide();
                        return Status.OK_STATUS;
                    }
                }

            });
            binding = dbc.bindValue(wv, selectionsDone, uvs, uvs);
        } else {
            dbc.addBinding(binding);
        }
    }

    public void removeBinding(WidgetCreator dbc) {
        if (binding != null) {
            dbc.removeBinding(binding);
        }
    }

    public void resetValues(boolean resetSelection, boolean resetNumber) {
        if (resetSelection) {
            cvSource.setSelection(null);
            cvResult.setSelection(null);
        }
        if (resetNumber)
            setNumber(null);
        else
            setNumber(number); // to re-do the validation tests
    }

    public void resetValues(final boolean resetSelection,
        final boolean resetNumber, boolean async) {
        if (async) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    resetValues(resetSelection, resetNumber);
                }
            });
        } else {
            resetValues(resetSelection, resetNumber);
        }
    }

    public void setNextWidget(Object nextWidget) {
        this.nextWidget = nextWidget;
    }

<<<<<<< HEAD:eclipse_ws/biobank2/src/edu/ualberta/med/biobank/widgets/SampleTypeSelectionWidget.java
    public void setTypes(List<SpecimenTypeWrapper> types) {
        cv.setInput(types);
=======
    public void setTypes(List<SampleTypeWrapper> types) {
        cvResult.setInput(types);
>>>>>>> dev-aaron:eclipse_ws/biobank2/src/edu/ualberta/med/biobank/widgets/AliquotedSpecimenSelectionWidget.java
    }

    public void setFocus() {
        cvSource.getControl().setFocus();
    }
}
