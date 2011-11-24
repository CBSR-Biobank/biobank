package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenHierarchyInfo;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;

/**
 * Create widgets to show types selection for specimens on a pallet: one label,
 * one combo with different source types, one combo with different result types
 * and one text showing total number of samples found
 */
public class AliquotedSpecimenSelectionWidget {
    private ComboViewer cvSource;
    private ComboViewer cvResult;
    private ControlDecoration rowControlDecoration;
    private ControlDecoration sourceControlDecoration;
    private ControlDecoration resultControlDecoration;
    private Label textNumber;
    private Integer number;

    private IObservableValue bothSelected = new WritableValue(Boolean.FALSE,
        Boolean.class);

    private IObservableValue sourceSelected = new WritableValue(Boolean.FALSE,
        Boolean.class);

    private IObservableValue resultSelected = new WritableValue(Boolean.FALSE,
        Boolean.class);

    private Binding oneRowBinding;
    private Binding sourceBinding;
    private Binding resultBinding;

    private Object nextWidget;
    private BgcWidgetCreator widgetCreator;
    private boolean oneRow;
    private Label sourceLabel;
    private Label resultLabel;

    private List<SpecimenTypeWrapper> sourceChildTypes = new ArrayList<SpecimenTypeWrapper>();

    public AliquotedSpecimenSelectionWidget(Composite parent, Character letter,
        BgcWidgetCreator widgetCreator, boolean oneRow) {
        this.widgetCreator = widgetCreator;
        this.oneRow = oneRow;
        if (letter != null) {
            widgetCreator.getToolkit().createLabel(parent, letter.toString(),
                SWT.LEFT);
        }
        if (!oneRow) {
            sourceLabel = widgetCreator.createLabel(parent, Messages.AliquotedSpecimenSelectionWidget_sources_spec_title);
            sourceControlDecoration = BgcBaseWidget
                .createDecorator(
                    sourceLabel,
                    Messages.AliquotedSpecimenSelectionWidget_selections_validation_msg);
        }
        cvSource = widgetCreator.createComboViewerWithoutLabel(parent, null,
            null, new BiobankLabelProvider());
        setComboProperties(cvSource, widgetCreator.getToolkit(), 0);
        cvSource.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                SpecimenWrapper spc = (SpecimenWrapper) element;
                return spc.getSpecimenType().getNameShort() + "(" //$NON-NLS-1$
                    + spc.getInventoryId() + ")"; //$NON-NLS-1$
            }
        });
        cvSource.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    SpecimenWrapper spc = (SpecimenWrapper) ((IStructuredSelection) event
                        .getSelection()).getFirstElement();
                    sourceChildTypes = spc.getSpecimenType()
                        .getChildSpecimenTypeCollection(false);
                }
                if (cvResult != null)
                    cvResult.refresh();
            }
        });

        if (!oneRow) {
            resultLabel = widgetCreator.createLabel(parent,
                Messages.AliquotedSpecimenSelectionWidget_aliquoted_spec_title);
            resultControlDecoration = BgcBaseWidget
                .createDecorator(
                    resultLabel,
                    Messages.AliquotedSpecimenSelectionWidget_selections_validation_msg);
        }
        cvResult = widgetCreator.createComboViewerWithoutLabel(parent, null,
            null, new BiobankLabelProvider());
        setComboProperties(cvResult, widgetCreator.getToolkit(), 1);
        cvResult.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((SpecimenTypeWrapper) element).getName();
            }
        });
        cvResult.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                Object element) {
                return (getSourceSelection() != null && getSourceSelection()
                    .hasUnknownImportType())
                    || sourceChildTypes.contains(element);
            }
        });
        if (oneRow) {
            textNumber = widgetCreator.getToolkit().createLabel(parent, "", //$NON-NLS-1$
                SWT.BORDER);
            GridData gd = new GridData();
            gd.widthHint = 20;
            gd.horizontalAlignment = SWT.LEFT;
            textNumber.setLayoutData(gd);
            rowControlDecoration = BgcBaseWidget
                .createDecorator(
                    textNumber,
                    Messages.AliquotedSpecimenSelectionWidget_selections_validation_msg);
        }
    }

    private void setComboProperties(ComboViewer cv, FormToolkit toolkit,
        final int selectionPosition) {
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        int parentColumns = ((GridLayout) cv.getControl().getParent()
            .getLayout()).numColumns;
        if (oneRow) {
            if (selectionPosition == 0)
                gd.widthHint = 300;
        } else if (parentColumns > 2)
            gd.horizontalSpan = parentColumns - 1;
        cv.getControl().setLayoutData(gd);
        toolkit.adapt(cv.getControl(), true, true);
        cv.setContentProvider(new ArrayContentProvider());
        cv.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                boolean res = true;
                if (event.getSelection() == null
                    || ((IStructuredSelection) event.getSelection()).size() == 0) {
                    res = false;
                }
                if (selectionPosition == 0)
                    sourceSelected.setValue(res);
                else
                    resultSelected.setValue(res);
                updateBothSelectedField();
            }
        });
        cv.setComparator(new ViewerComparator());
        if (selectionPosition != 0)
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
        cvSource.addSelectionChangedListener(listener);
        cvResult.addSelectionChangedListener(listener);
    }

    public void setNumber(Integer number) {
        if (textNumber != null) {
            this.number = number;
            String text = ""; //$NON-NLS-1$
            if (number != null) {
                text = number.toString();
            }
            if (number == null || number == 0) {
                cvSource.getControl().setEnabled(false);
                sourceSelected.setValue(true);
                cvResult.getControl().setEnabled(false);
                resultSelected.setValue(true);
                bothSelected.setValue(true);
            } else {
                cvSource.getControl().setEnabled(true);
                sourceSelected.setValue(getSourceSelection() != null);
                cvResult.getControl().setEnabled(true);
                resultSelected.setValue(getResultTypeSelection() != null);
                updateBothSelectedField();
            }
            textNumber.setText(text);
        }
    }

    public void increaseNumber() {
        if (number == null)
            number = 0;
        number++;
        setNumber(number);
        sourceSelected.setValue(getSourceSelection() != null);
        resultSelected.setValue(getResultTypeSelection() != null);
        updateBothSelectedField();
    }

    private void updateBothSelectedField() {
        bothSelected.setValue((Boolean) sourceSelected.getValue()
            && (Boolean) resultSelected.getValue());
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

    private SpecimenTypeWrapper getResultTypeSelection() {
        return (SpecimenTypeWrapper) ((StructuredSelection) cvResult
            .getSelection()).getFirstElement();
    }

    private SpecimenWrapper getSourceSelection() {
        return (SpecimenWrapper) ((StructuredSelection) cvSource.getSelection())
            .getFirstElement();
    }

    public void addBindings() {
        if (oneRow) {
            if (oneRowBinding == null) {
                UpdateValueStrategy rowUpdateValue = createOneRowUpdateValueStrategy(rowControlDecoration);
                oneRowBinding = widgetCreator.bindValue(new WritableValue(
                    Boolean.FALSE, Boolean.class), bothSelected,
                    rowUpdateValue, rowUpdateValue);
            } else {
                widgetCreator.addBinding(oneRowBinding);
            }
        } else {
            if (sourceBinding == null || resultBinding == null) {
                UpdateValueStrategy sourceUpdateValue = createOneRowUpdateValueStrategy(sourceControlDecoration);
                sourceBinding = widgetCreator.bindValue(new WritableValue(
                    Boolean.FALSE, Boolean.class), sourceSelected,
                    sourceUpdateValue, sourceUpdateValue);
                UpdateValueStrategy resultUpdateValue = createOneRowUpdateValueStrategy(resultControlDecoration);
                resultBinding = widgetCreator.bindValue(new WritableValue(
                    Boolean.FALSE, Boolean.class), resultSelected,
                    resultUpdateValue, resultUpdateValue);
            } else {
                widgetCreator.addBinding(sourceBinding);
                widgetCreator.addBinding(resultBinding);
            }
        }
    }

    private UpdateValueStrategy createOneRowUpdateValueStrategy(
        final ControlDecoration decoration) {
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    decoration.show();
                    return ValidationStatus
                        .error(Messages.AliquotedSpecimenSelectionWidget_selections_status_msg);
                } else {
                    decoration.hide();
                    return Status.OK_STATUS;
                }
            }
        });
        return uvs;
    }

    public void removeBindings() {
        bothSelected.setValue(true);
        if (oneRowBinding != null)
            widgetCreator.removeBinding(oneRowBinding);
        sourceSelected.setValue(true);
        if (sourceBinding != null)
            widgetCreator.removeBinding(sourceBinding);
        resultSelected.setValue(true);
        if (resultBinding != null)
            widgetCreator.removeBinding(resultBinding);
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

    public void setResultTypes(List<SpecimenTypeWrapper> types) {
        cvResult.setInput(types);
    }

    public void setSourceSpecimens(List<SpecimenWrapper> sourceSpecimens) {
        cvSource.setInput(sourceSpecimens);
    }

    public void setFocus() {
        cvSource.getControl().setFocus();
    }

    /**
     * @return an array of [SpecimenLink (source), SpecimenType (result)]
     */
    public SpecimenHierarchyInfo getSelection() {
        if (getSourceSelection() != null && getResultTypeSelection() != null)
            return new SpecimenHierarchyInfo(getSourceSelection(),
                getResultTypeSelection());
        return null;
    }

    public void setSelection(SpecimenHierarchyInfo previousSelection) {
        if (previousSelection != null) {
            cvSource.setSelection(new StructuredSelection(previousSelection
                .getParentSpecimen()));
            cvResult.setSelection(new StructuredSelection(previousSelection
                .getAliquotedSpecimenType()));
        }
    }

    public void setEnabled(boolean enabled) {
        cvSource.getControl().setEnabled(enabled);
        cvResult.getControl().setEnabled(enabled);
    }

    public void deselectAll() {
        cvSource.setSelection(null);
        cvResult.setSelection(null);
    }

    public void showWidget(boolean enabled) {
        if (sourceLabel != null)
            widgetCreator.showWidget(sourceLabel, enabled);
        widgetCreator.showWidget(cvSource.getControl(), enabled);
        if (resultLabel != null)
            widgetCreator.showWidget(resultLabel, enabled);
        widgetCreator.showWidget(cvResult.getControl(), enabled);
    }

    public void setReadOnlySelections(SpecimenWrapper sourceSpecimen,
        SpecimenTypeWrapper resultType) {
        cvSource.setInput(Arrays.asList(sourceSpecimen));
        cvSource.setSelection(new StructuredSelection(sourceSpecimen));
        cvResult.setInput(Arrays.asList(resultType));
        cvResult.setSelection(new StructuredSelection(resultType));
    }

}
