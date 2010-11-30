package edu.ualberta.med.biobank.widgets.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.ReportFilterValue;

/**
 * Decorates a <code>FilterValueWidget</code> to allow a set of values to be
 * stored.
 * 
 * @author jferland
 * 
 */
public class SetFilterValueWidget implements FilterValueWidget {
    private static final IElementComparer COMPARER = new IElementComparer() {
        @Override
        public boolean equals(Object a, Object b) {
            ReportFilterValue lhs = (ReportFilterValue) a;
            ReportFilterValue rhs = (ReportFilterValue) b;

            return lhs.getValue().equals(rhs.getValue())
                && lhs.getSecondValue().equals(rhs.getSecondValue());
        }

        @Override
        public int hashCode(Object element) {
            ReportFilterValue value = (ReportFilterValue) element;

            int hashCode = 31;

            if (value.getValue() != null) {
                hashCode *= 17 * value.getValue().hashCode();
            }

            if (value.getSecondValue() != null) {
                hashCode *= 17 * value.getSecondValue().hashCode();
            }

            return hashCode;
        }
    };
    private static final LabelProvider LABEL_PROVIDER = new LabelProvider() {
        @Override
        public String getText(Object element) {
            if (element instanceof ReportFilterValue) {
                ReportFilterValue value = ((ReportFilterValue) element);

                if (value.getValue() != null) {
                    if (value.getSecondValue() != null) {
                        return "\"" + value.getValue() + "\" and \""
                            + value.getSecondValue() + "\"";
                    } else {
                        return value.getValue();
                    }
                }
            }
            return "";
        };
    };

    private final Collection<ChangeListener<Object>> listeners = new ArrayList<ChangeListener<Object>>();
    private final FilterValueWidget decoratedWidget;
    private Composite container;
    private ListViewer listViewer;
    private Button addButton, removeButton, toggleSetButton;
    private Collection<ReportFilterValue> values;

    public SetFilterValueWidget(Composite parent,
        FilterValueWidget decoratedWidget) {
        this.decoratedWidget = decoratedWidget;

        createContainer(parent);
        createAddButton();
        createRemoveButton();
        createToggleSetButton();
        createListViewer();
    }

    private void createContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;

        Control valueControl = decoratedWidget.getControl();
        valueControl.setParent(container);
        valueControl.setLayoutData(layoutData);
    }

    private void createAddButton() {
        addButton = new Button(container, SWT.NONE);
        addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ADD));
        addButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // TODO: only add if not already in.
                listViewer.add(decoratedWidget.getValues().toArray());
                notifyListeners(null);
            }
        });
    }

    private void createRemoveButton() {
        removeButton = new Button(container, SWT.NONE);
        removeButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_DELETE));
        removeButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                listViewer.remove(getSelectedValues().toArray());
                notifyListeners(null);
            }
        });
    }

    private void createToggleSetButton() {
        toggleSetButton = new Button(container, SWT.NONE);
        toggleSetButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_EDIT_FORM));
        toggleSetButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // TODO: replace with image
                toggleSetButton.setText(isListVisible() ? "^" : "v");
                // TODO: only send an SWT.Resize event so only resizing is done
                // and the state of the form is not set to dirty. Use SWT.Resize
                // and SWT.Modify?
                setListVisible(!isListVisible());
                notifyListeners(null);
            }
        });
    }

    private boolean isListVisible() {
        return listViewer.getControl().getVisible();
    }

    private void setListVisible(boolean isVisible) {
        listViewer.getControl().setVisible(isVisible);
        ((GridData) listViewer.getControl().getLayoutData()).exclude = !isVisible;
    }

    private void createListViewer() {
        listViewer = new ListViewer(container, SWT.MULTI | SWT.READ_ONLY
            | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        layoutData.heightHint = 3 * listViewer.getList().getItemHeight() + 2;

        listViewer.getControl().setLayoutData(layoutData);

        listViewer.setComparer(COMPARER);
        listViewer.setLabelProvider(LABEL_PROVIDER);
    }

    private Collection<ReportFilterValue> getSelectedValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
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

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        ReportFilterValue value;
        for (String key : listViewer.getList().getItems()) {
            value = (ReportFilterValue) listViewer.getData(key);
            values.add(value);
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        listViewer.remove(getValues().toArray());
        listViewer.add(values.toArray());
    }

    @Override
    public void addChangeListener(ChangeListener<Object> changeListener) {
        listeners.add(changeListener);
    }

    private void notifyListeners(Object o) {
        for (ChangeListener<Object> listener : listeners) {
            listener.handleEvent(o);
        }
    }

    @Override
    public Control getControl() {
        return container;
    }
}
