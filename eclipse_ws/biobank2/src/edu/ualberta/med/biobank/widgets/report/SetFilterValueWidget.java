package edu.ualberta.med.biobank.widgets.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.model.ReportFilterValue;

/**
 * Decorates a <code>FilterValueWidget</code> to allow a set of values to be
 * stored.
 * 
 * @author jferland
 * 
 */
public class SetFilterValueWidget implements FilterValueWidget {
    private static final I18n i18n = I18nFactory
        .getI18n(SetFilterValueWidget.class);

    public enum Mode {
        ViewMode,
        EditMode;
    }

    private static final IElementComparer COMPARER = new IElementComparer() {
        @Override
        public boolean equals(Object a, Object b) {
            ReportFilterValue rfv1 = (ReportFilterValue) a;
            ReportFilterValue rfv2 = (ReportFilterValue) b;

            if (rfv1 == rfv2) {
                return true;
            }

            if (rfv1 == null) {
                if (rfv2 != null)
                    return false;
            } else if (rfv2 == null) {
                return false;
            }

            if (rfv1.getValue() == null) {
                if (rfv2.getValue() != null)
                    return false;
            } else if (!rfv1.getValue().equals(rfv2.getValue())) {
                return false;
            }

            if (rfv1.getSecondValue() == null) {
                if (rfv2.getSecondValue() != null)
                    return false;
            } else if (!rfv1.getSecondValue().equals(rfv2.getSecondValue())) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode(Object element) {
            ReportFilterValue rfv = (ReportFilterValue) element;

            int hashCode = 31;

            if (rfv.getValue() != null) {
                hashCode *= 17 * rfv.getValue().hashCode();
            }

            if (rfv.getSecondValue() != null) {
                hashCode *= 17 * rfv.getSecondValue().hashCode();
            }

            return hashCode;
        }
    };
    private static final ViewerComparator COMPARATOR = new ViewerComparator() {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            ReportFilterValue rfv1 = (ReportFilterValue) e1;
            ReportFilterValue rfv2 = (ReportFilterValue) e2;

            int cmp = 0;

            if (rfv1.getValue() != null) {
                cmp = rfv1.getValue().compareToIgnoreCase(rfv2.getValue());
            } else if (rfv2.getValue() != null) {
                cmp = -1;
            }

            if (cmp == 0) {
                if (rfv1.getSecondValue() != null) {
                    cmp = rfv1.getSecondValue().compareToIgnoreCase(
                        rfv2.getSecondValue());
                } else if (rfv2.getSecondValue() != null) {
                    cmp = -1;
                }
            }

            return cmp;
        }
    };

    private final Collection<ChangeListener<ChangeEvent>> listeners =
        new ArrayList<ChangeListener<ChangeEvent>>();
    private final Composite container;
    private final ViewModeControls viewModeControls;
    private final EditModeControls editModeControls;

    public SetFilterValueWidget(Composite parent,
        FilterValueWidget decoratedWidget) {
        container = new Composite(parent, SWT.NONE);

        setGridLayout(1, container);

        editModeControls = new EditModeControls(container, decoratedWidget);
        viewModeControls = new ViewModeControls(container);

        setGridData(viewModeControls);
        setGridData(editModeControls);

        setMode(Mode.ViewMode);
    }

    public Mode getMode() {
        return isControlVisible(viewModeControls) ? Mode.ViewMode
            : Mode.EditMode;
    }

    public void setMode(Mode mode) {
        switch (mode) {
        case ViewMode:
            viewModeControls.updateViewText();
            setControlVisible(viewModeControls, true);
            setControlVisible(editModeControls, false);
            break;
        case EditMode:
            setControlVisible(viewModeControls, false);
            setControlVisible(editModeControls, true);
            break;
        }
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values =
            new ArrayList<ReportFilterValue>();
        ListViewer listViewer = editModeControls.getListViewer();
        ReportFilterValue value;
        for (int i = 0, n = listViewer.getList().getItemCount(); i < n; i++) {
            value = (ReportFilterValue) listViewer.getElementAt(i);
            values.add(value);
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        ListViewer listViewer = editModeControls.getListViewer();
        listViewer.remove(getValues().toArray());

        Collection<ReportFilterValue> validValues =
            new ArrayList<ReportFilterValue>();
        for (ReportFilterValue value : values) {
            if (isValid(value)) {
                validValues.add(value);
            }
        }
        listViewer.add(validValues.toArray());

        viewModeControls.updateViewText();
    }

    @Override
    public void addChangeListener(ChangeListener<ChangeEvent> changeListener) {
        listeners.add(changeListener);
    }

    private void notifyListeners(ChangeEvent event) {
        for (ChangeListener<ChangeEvent> listener : listeners) {
            listener.handleEvent(event);
        }
    }

    @Override
    public Control getControl() {
        return container;
    }

    @Override
    public boolean isValid(ReportFilterValue value) {
        return editModeControls.getFilterValueWidget().isValid(value);
    }

    @Override
    public String toString(ReportFilterValue value) {
        return editModeControls.getFilterValueWidget().toString(value);
    }

    private static boolean isControlVisible(Control control) {
        return control.getVisible();
    }

    private static void setControlVisible(Control control, boolean isVisible) {
        control.setVisible(isVisible);
        ((GridData) control.getLayoutData()).exclude = !isVisible;
    }

    private static void setGridLayout(int numColumns, Composite composite) {
        GridLayout layout = new GridLayout(numColumns, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
    }

    private static void setGridData(Control control) {
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        control.setLayoutData(layoutData);
    }

    private static <E> Collection<E> minus(Collection<E> a, Collection<E> b,
        IElementComparer comparer) {
        List<E> results = new ArrayList<E>();
        for (E elementFromA : a) {
            boolean isInB = false;
            for (E elementFromB : b) {
                if (comparer.equals(elementFromA, elementFromB)) {
                    isInB = true;
                    break;
                }
            }
            if (!isInB) {
                results.add(elementFromA);
            }
        }
        return results;
    }

    private class ViewModeControls extends Composite {
        private final Text readOnlyText;
        private Button editModeButton;

        public ViewModeControls(Composite parent) {
            super(parent, SWT.NONE);

            setGridLayout(2, this);

            readOnlyText = new Text(this, SWT.BORDER | SWT.READ_ONLY);
            readOnlyText.setBackground(BgcWidgetCreator.READ_ONLY_TEXT_BGR);
            readOnlyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
                | GridData.GRAB_HORIZONTAL));

            createEditModeButton();

            updateViewText();
        }

        @SuppressWarnings("nls")
        public void updateViewText() {
            List<String> strings = new ArrayList<String>();
            for (ReportFilterValue value : getValues()) {
                strings.add(SetFilterValueWidget.this.toString(value));
            }
            String list = StringUtils.join(strings, ", ");

            if (list.isEmpty()) {
                list = i18n.tr("<no values added>");
            }

            readOnlyText.setText(list);
        }

        @SuppressWarnings("nls")
        private void createEditModeButton() {
            editModeButton = new Button(this, SWT.NONE);
            editModeButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_DOWN));
            editModeButton.setToolTipText(
                // button tooltip.
                i18n.tr("Expand to add values"));
            editModeButton.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    setMode(Mode.EditMode);
                    SetFilterValueWidget.this.notifyListeners(new ChangeEvent(
                        false));
                }
            });
        }
    }

    private class EditModeControls extends Composite {
        private final FilterValueWidget filterValueWidget;
        private ListViewer listViewer;
        private Button addButton, removeButton, viewModeButton;

        public EditModeControls(Composite parent,
            FilterValueWidget filterValueWidget) {
            super(parent, SWT.NONE);
            this.filterValueWidget = filterValueWidget;

            setGridLayout(4, this);
            filterValueWidget.getControl().setParent(this);
            setGridData(filterValueWidget.getControl());

            createAddButton();
            createRemoveButton();
            createViewModeButton();
            createListViewer();
        }

        public ListViewer getListViewer() {
            return listViewer;
        }

        public FilterValueWidget getFilterValueWidget() {
            return filterValueWidget;
        }

        @SuppressWarnings("nls")
        private void createAddButton() {
            addButton = new Button(this, SWT.NONE);
            addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_ADD));
            addButton.setToolTipText(
                // button tooltip.
                i18n.tr("Add value to list"));
            addButton.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    addValues();
                    filterValueWidget.getControl().setFocus();
                }
            });

            filterValueWidget.getControl().addListener(SWT.KeyUp,
                new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        if (event.keyCode == SWT.CR) {
                            addValues();
                        }
                    }
                });
        }

        private void addValues() {
            // only add values not already in the list
            Collection<ReportFilterValue> newValues = minus(
                filterValueWidget.getValues(), getValues(), COMPARER);
            listViewer.add(newValues.toArray());
            filterValueWidget.setValues(new ArrayList<ReportFilterValue>());
            SetFilterValueWidget.this.notifyListeners(null);
        }

        private void createListViewer() {
            listViewer = new ListViewer(this, SWT.MULTI | SWT.READ_ONLY
                | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

            GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
            layoutData.horizontalSpan = 4;
            layoutData.heightHint =
                3 * listViewer.getList().getItemHeight() + 2;

            listViewer.getControl().setLayoutData(layoutData);

            listViewer.setComparer(COMPARER);
            listViewer.setLabelProvider(new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof ReportFilterValue) {
                        ReportFilterValue value = ((ReportFilterValue) element);
                        return SetFilterValueWidget.this.toString(value);
                    }
                    return StringUtil.EMPTY_STRING;
                }
            });
            listViewer.setComparator(COMPARATOR);

            listViewer.getList().addListener(SWT.KeyUp, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (event.keyCode == SWT.DEL) {
                        removeValues();
                    }
                }
            });

        }

        @SuppressWarnings("nls")
        private void createRemoveButton() {
            removeButton = new Button(this, SWT.NONE);
            removeButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_REMOVE));
            removeButton.setToolTipText(
                // button tooltip.
                i18n.tr("Remove selected value(s) from list"));
            removeButton.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    removeValues();
                }
            });
        }

        private void removeValues() {
            listViewer.remove(getSelectedValues().toArray());
            SetFilterValueWidget.this.notifyListeners(null);
        }

        @SuppressWarnings("nls")
        private void createViewModeButton() {
            viewModeButton = new Button(this, SWT.NONE);
            viewModeButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_UP));
            viewModeButton.setToolTipText(
                // button tooltip.
                i18n.tr("Collapse"));
            viewModeButton.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    setMode(Mode.ViewMode);
                    SetFilterValueWidget.this.notifyListeners(new ChangeEvent(
                        false));
                }
            });
        }

        private Collection<ReportFilterValue> getSelectedValues() {
            Collection<ReportFilterValue> values =
                new ArrayList<ReportFilterValue>();
            ISelection selection = listViewer.getSelection();
            if (selection instanceof IStructuredSelection) {
                Iterator<?> it = ((IStructuredSelection) selection).iterator();
                while (it.hasNext()) {
                    ReportFilterValue value = (ReportFilterValue) it.next();
                    values.add(value);
                }
            }
            return values;
        }
    }
}
