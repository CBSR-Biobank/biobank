package edu.ualberta.med.biobank.widgets.specimentypeselection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.action.scanprocess.SpecimenHierarchyInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.util.SbsLabeling;

public class SpecimenTypeSelectionWidget extends BgcBaseWidget
    implements ISpecimenTypeSelectionChangedListener {
    private static final I18n i18n = I18nFactory.getI18n(AliquotedSpecimenSelectionWidget.class);

    private final int maxRows;

    private int currentRows;

    private final FormToolkit toolkit;

    private final ListenerList selectionChangedListeners = new ListenerList();

    private final List<AliquotedSpecimenSelectionWidget> specimenTypesWidgets;

    @SuppressWarnings("nls")
    public SpecimenTypeSelectionWidget(Composite parent, BgcWidgetCreator widgetCreator,
        int maxRows, int currentRows) {
        super(parent, SWT.NONE);
        this.maxRows = maxRows;
        this.currentRows = currentRows;
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        setLayout(layout);

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        setLayoutData(gd);

        toolkit = widgetCreator.getToolkit();

        toolkit.paintBordersFor(this);
        toolkit.createLabel(this, StringUtil.EMPTY_STRING);
        toolkit.createLabel(this, SourceSpecimen.NAME.singular().toString());
        toolkit.createLabel(this,
            // label
            i18n.tr("Aliquoted Specimen Types"));
        toolkit.createLabel(this, StringUtil.EMPTY_STRING);

        specimenTypesWidgets = new ArrayList<AliquotedSpecimenSelectionWidget>();
        AliquotedSpecimenSelectionWidget precedent = null;

        for (int i = 0; i < maxRows; i++) {
            final AliquotedSpecimenSelectionWidget widget = new AliquotedSpecimenSelectionWidget(
                this, i, SbsLabeling.SBS_ROW_LABELLING_PATTERN.charAt(i),
                widgetCreator, true);
            widget.addSelectionChangedListener(this);
            widget.addBindings();
            specimenTypesWidgets.add(widget);
            if (precedent != null) {
                precedent.setNextWidget(widget);
            }
            precedent = widget;
            widget.setNumber(0);
            widget.setEnabled(false);

            specimenTypesWidgets.get(i).showWidget(i < currentRows);
        }
    }

    public void addSelectionChangedListener(ISpecimenTypeSelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public void removeSelectionChangedListener(ISpecimenTypeSelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    @Override
    public void selectionChanged(SpecimenTypeSelectionEvent event) {
        fireSelectionChanged(event);
    }

    protected void fireSelectionChanged(final SpecimenTypeSelectionEvent event) {
        Object[] listeners = selectionChangedListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final ISpecimenTypeSelectionChangedListener l =
                (ISpecimenTypeSelectionChangedListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    l.selectionChanged(event);
                }
            });
        }
    }

    public void setSelections(List<Specimen> sourceSpecimens,
        List<AliquotedSpecimen> studiesAliquotedTypes) {
        int rowCount = 0;
        for (AliquotedSpecimenSelectionWidget widget : specimenTypesWidgets) {
            SpecimenHierarchyInfo previousSelection = widget.getSelection();
            widget.setSourceSpecimens(sourceSpecimens);
            widget.setResultTypes(studiesAliquotedTypes);
            widget.setSelection(previousSelection);
            ++rowCount;

            // only assign for the currently configured rows
            if (rowCount >= currentRows) break;
        }
    }

    public void resetValues(boolean resetSelection, boolean resetNumber) {
        for (AliquotedSpecimenSelectionWidget widget : specimenTypesWidgets) {
            widget.resetValues(resetSelection, resetNumber);
        }

    }

    public List<SpecimenHierarchyInfo> getSelections() {
        List<SpecimenHierarchyInfo> result = new ArrayList<SpecimenHierarchyInfo>();
        int rowCount = 0;
        for (AliquotedSpecimenSelectionWidget widget : specimenTypesWidgets) {
            result.add(widget.getSelection());
            ++rowCount;

            // only return the results for the currently configured rows
            if (rowCount >= currentRows) break;
        }
        return result;
    }

    @SuppressWarnings("nls")
    public void setCount(Integer row, Integer count) {
        if (row > specimenTypesWidgets.size()) {
            throw new IllegalArgumentException("row exeeds maximum: " + row);
        }
        AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets.get(row);

        if (count == null) {
            widget.deselectAll();
            count = 0;
        }
        widget.setNumber(count);
    }

    public void setCounts(Map<Integer, Integer> counts) {
        for (Entry<Integer, Integer> entry : counts.entrySet()) {
            setCount(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean setFocus() {
        for (AliquotedSpecimenSelectionWidget widget : specimenTypesWidgets) {
            if (widget.canFocus()) {
                widget.setFocus();
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("nls")
    public void resetValues(int row, final boolean resetSelection, final boolean resetNumber,
        boolean async) {
        if (row > specimenTypesWidgets.size()) {
            throw new IllegalArgumentException("row exeeds maximum: " + row);
        }
        final AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets.get(row);
        widget.resetValues(resetSelection, resetNumber, async);
    }

    public void updateHierarchyWidgets(int rows) {
        currentRows = rows;
        for (int i = 0; i < maxRows; ++i) {
            specimenTypesWidgets.get(i).showWidget(i < currentRows);
        }
    }
}
