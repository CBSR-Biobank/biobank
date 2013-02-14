package edu.ualberta.med.biobank.widgets.specimentypeselection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenHierarchyInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

/**
 * Create widgets to show types selection for specimens on a pallet: one label, one combo with
 * different source types, one combo with different result types and one text showing total number
 * of samples found
 */
public class AliquotedSpecimenSelectionWidget implements ISelectionChangedListener {
    private static final I18n i18n = I18nFactory.getI18n(AliquotedSpecimenSelectionWidget.class);

    private final Integer rowNumber;
    private ComboViewer cvSource;
    private ComboViewer cvResult;
    private ControlDecoration rowControlDecoration;
    private ControlDecoration sourceControlDecoration;
    private ControlDecoration resultControlDecoration;
    private Label rowLabel;
    private Label textNumber;
    private Integer number;

    private final IObservableValue bothSelected = new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue sourceSelected = new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue resultSelected = new WritableValue(Boolean.FALSE, Boolean.class);

    private Binding oneRowBinding;
    private Binding sourceBinding;
    private Binding resultBinding;

    private Object nextWidget;
    private BgcWidgetCreator widgetCreator;
    private boolean oneRow;
    private Label sourceLabel;
    private Label resultLabel;

    private final ListenerList selectionChangedListeners = new ListenerList();

    private Set<SpecimenType> sourceChildTypes = new HashSet<SpecimenType>();

    @SuppressWarnings("nls")
    public AliquotedSpecimenSelectionWidget(Composite parent, Integer rowNumber, Character letter,
        BgcWidgetCreator widgetCreator, boolean oneRow) {
        this.widgetCreator = widgetCreator;
        this.oneRow = oneRow;
        this.rowNumber = rowNumber;

        if (letter != null) {
            rowLabel = widgetCreator.getToolkit().createLabel(parent, letter.toString(), SWT.LEFT);
        }
        if (!oneRow) {
            sourceLabel = widgetCreator.createLabel(parent, SourceSpecimen.NAME.singular().toString());
            sourceControlDecoration = BgcBaseWidget.createDecorator(sourceLabel,
                i18n.tr("A source specimen type and an aliquoted specimen type should be selected"));
        }
        cvSource = widgetCreator.createComboViewerWithoutLabel(parent, null, null,
            new BiobankLabelProvider());
        setComboProperties(cvSource, widgetCreator.getToolkit(), 0);
        cvSource.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                Specimen spc = (Specimen) element;
                StringBuffer label = new StringBuffer();
                label.append(spc.getSpecimenType().getNameShort());
                label.append(" (");
                label.append(spc.getInventoryId());
                label.append(")");
                return label.toString();
            }
        });
        cvSource.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    Specimen spc = (Specimen) ((IStructuredSelection) event.getSelection()).getFirstElement();
                    sourceChildTypes = spc.getSpecimenType().getChildSpecimenTypes();
                }
                if (cvResult != null) cvResult.refresh();
            }
        });

        if (!oneRow) {
            resultLabel = widgetCreator.createLabel(parent, i18n.tr("Aliquoted specimen type"));
            resultControlDecoration = BgcBaseWidget.createDecorator(resultLabel,
                i18n.tr("A source specimen type and an aliquoted specimen type should be selected"));
        }
        cvResult = widgetCreator.createComboViewerWithoutLabel(parent, null, null,
            new BiobankLabelProvider());
        setComboProperties(cvResult, widgetCreator.getToolkit(), 1);
        cvResult.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                AliquotedSpecimen aqSpecimen = (AliquotedSpecimen) element;
                StringBuffer label = new StringBuffer();
                label.append(aqSpecimen.getSpecimenType().getName());
                label.append("(");
                label.append(aqSpecimen.getVolume());
                label.append(")");
                return label.toString();
            }
        });
        cvResult.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                boolean filterBySource = false;

                Specimen sourceSpecimen = getSourceSelection();
                if (sourceSpecimen != null) {
                    SpecimenType sourceType = sourceSpecimen.getSpecimenType();
                    if (sourceType != null) {
                        String name = sourceType.getName();
                        if ((name != null) && name.equals(SpecimenTypeWrapper.UNKNOWN_IMPORT_NAME)) {
                            filterBySource = true;
                        }
                    }
                }

                return (filterBySource
                || sourceChildTypes.contains(((AliquotedSpecimen) element).getSpecimenType()));
            }
        });
        if (oneRow) {
            textNumber = widgetCreator.getToolkit().createLabel(parent, StringUtil.EMPTY_STRING, SWT.BORDER);
            GridData gd = new GridData();
            gd.widthHint = 20;
            gd.horizontalAlignment = SWT.LEFT;
            textNumber.setLayoutData(gd);
            rowControlDecoration = BgcBaseWidget.createDecorator(textNumber,
                i18n.tr("A source specimen type and an aliquoted specimen type should be selected"));
        }

        cvSource.addSelectionChangedListener(this);
        cvResult.addSelectionChangedListener(this);
    }

    private void setComboProperties(ComboViewer cv, FormToolkit toolkit, final int selectionPosition) {
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        int parentColumns = ((GridLayout) cv.getControl().getParent().getLayout()).numColumns;
        if (oneRow) {
            if (selectionPosition == 0) gd.widthHint = 300;
        } else if (parentColumns > 2) gd.horizontalSpan = parentColumns - 1;
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
        if (selectionPosition != 0) cv.getControl().addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_RETURN) {
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
                ((AliquotedSpecimenSelectionWidget) nextWidget).cvSource.getControl().setFocus();
            }
        }
        return true;
    }

    public void addSelectionChangedListener(ISpecimenTypeSelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public void removeSelectionChangedListener(ISpecimenTypeSelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        // only report the event if the specimen type needs to be saved
        if (!needToSave()) return;

        Specimen specimen = getSourceSelection();
        AliquotedSpecimen aliquotedSpecimen = getResultTypeSelection();

        // do not report the event if one of the selections has not been made
        if ((specimen == null) || (aliquotedSpecimen == null)) return;

        SpecimenTypeSelectionEvent clientEvent = new SpecimenTypeSelectionEvent(
            rowNumber, specimen, aliquotedSpecimen);
        fireSelectionChanged(clientEvent);
    }

    protected void fireSelectionChanged(final SpecimenTypeSelectionEvent event) {
        Object[] listeners = selectionChangedListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final ISpecimenTypeSelectionChangedListener l =
                (ISpecimenTypeSelectionChangedListener) listeners[i];
            l.selectionChanged(event);
        }
    }

    public void setNumber(Integer number) {
        if (textNumber == null) return;

        this.number = number;
        String text = StringUtil.EMPTY_STRING;
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

    public void increaseNumber() {
        if (number == null) number = 0;
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
        }
        return number > 0;
    }

    private AliquotedSpecimen getResultTypeSelection() {
        return (AliquotedSpecimen) ((StructuredSelection) cvResult.getSelection()).getFirstElement();
    }

    private Specimen getSourceSelection() {
        return (Specimen) ((StructuredSelection) cvSource.getSelection()).getFirstElement();
    }

    public void addBindings() {
        if (oneRow) {
            if (oneRowBinding == null) {
                UpdateValueStrategy rowUpdateValue =
                    createOneRowUpdateValueStrategy(rowControlDecoration);
                oneRowBinding =
                    widgetCreator.bindValue(new WritableValue(Boolean.FALSE, Boolean.class),
                        bothSelected, rowUpdateValue, rowUpdateValue);
            } else {
                widgetCreator.addBinding(oneRowBinding);
            }
        } else {
            if (sourceBinding == null || resultBinding == null) {
                UpdateValueStrategy sourceUpdateValue =
                    createOneRowUpdateValueStrategy(sourceControlDecoration);
                sourceBinding =
                    widgetCreator.bindValue(new WritableValue(Boolean.FALSE, Boolean.class),
                        sourceSelected, sourceUpdateValue, sourceUpdateValue);
                UpdateValueStrategy resultUpdateValue =
                    createOneRowUpdateValueStrategy(resultControlDecoration);
                resultBinding =
                    widgetCreator.bindValue(new WritableValue(Boolean.FALSE, Boolean.class),
                        resultSelected, resultUpdateValue, resultUpdateValue);
            } else {
                widgetCreator.addBinding(sourceBinding);
                widgetCreator.addBinding(resultBinding);
            }
        }
    }

    private UpdateValueStrategy createOneRowUpdateValueStrategy(final ControlDecoration decoration) {
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @SuppressWarnings("nls")
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    decoration.show();
                    return ValidationStatus.error(
                        // validation error message.
                        i18n.tr("Type should be selected"));
                }
                decoration.hide();
                return Status.OK_STATUS;
            }
        });
        return uvs;
    }

    public void removeBindings() {
        bothSelected.setValue(true);
        if (oneRowBinding != null) widgetCreator.removeBinding(oneRowBinding);
        sourceSelected.setValue(true);
        if (sourceBinding != null) widgetCreator.removeBinding(sourceBinding);
        resultSelected.setValue(true);
        if (resultBinding != null) widgetCreator.removeBinding(resultBinding);
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

    public void resetValues(final boolean resetSelection, final boolean resetNumber, boolean async) {
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

    public void setResultTypes(List<AliquotedSpecimen> studiesAliquotedTypes) {
        cvResult.setInput(studiesAliquotedTypes);
    }

    public void setSourceSpecimens(List<Specimen> sourceSpecimens) {
        cvSource.setInput(sourceSpecimens);
    }

    public void setFocus() {
        cvSource.getControl().setFocus();
    }

    public SpecimenHierarchyInfo getSelection() {
        if (getSourceSelection() != null && getResultTypeSelection() != null)
            return new SpecimenHierarchyInfo(getSourceSelection(),
                getResultTypeSelection());
        return null;
    }

    public void setSelection(SpecimenHierarchyInfo previousSelection) {
        if (previousSelection != null) {
            cvSource.setSelection(new StructuredSelection(previousSelection.getParentSpecimen()));
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
        if (rowLabel != null) {
            rowLabel.setVisible(enabled);
        }

        if (sourceLabel != null) widgetCreator.showWidget(sourceLabel, enabled);
        widgetCreator.showWidget(cvSource.getControl(), enabled);
        if (resultLabel != null) widgetCreator.showWidget(resultLabel, enabled);
        widgetCreator.showWidget(cvResult.getControl(), enabled);

        if (textNumber != null) {
            textNumber.setVisible(enabled);
        }
    }

    public void setReadOnlySelections(SpecimenWrapper sourceSpecimen, SpecimenTypeWrapper resultType) {
        cvSource.setInput(Arrays.asList(sourceSpecimen));
        cvSource.setSelection(new StructuredSelection(sourceSpecimen));
        cvResult.setInput(Arrays.asList(resultType));
        cvResult.setSelection(new StructuredSelection(resultType));
    }

}
