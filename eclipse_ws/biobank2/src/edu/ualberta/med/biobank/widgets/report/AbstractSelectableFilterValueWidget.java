package edu.ualberta.med.biobank.widgets.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public abstract class AbstractSelectableFilterValueWidget<T extends AbstractListViewer>
    implements SelectableFilterValueWidget {
    private T viewer;

    public AbstractSelectableFilterValueWidget(T viewer) {
        this.viewer = viewer;

        setLabelProvider();
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        ISelection selection = viewer.getSelection();
        if (selection instanceof IStructuredSelection) {
            Iterator<?> it = ((IStructuredSelection) selection).iterator();
            int position = 0;
            while (it.hasNext()) {
                ViewerEntry entry = (ViewerEntry) it.next();
                if (entry != null) {
                    ReportFilterValue value = new ReportFilterValue();
                    value.setPosition(position);
                    value.setValue(entry.getKey());

                    values.add(value);

                    position++;
                }
            }
        }
        return values;
    }

    @Override
    public void setValues(Collection<ReportFilterValue> values) {
        if (!viewer.getControl().isDisposed()) {
            Collection<ViewerEntry> toSelect = new ArrayList<ViewerEntry>();
            for (ReportFilterValue value : values) {
                // TODO: note that it's a major problem if we try to set values
                // that don't exist in the option, as they will be lost. DEAL
                // WITH THIS! :P
                if (value != null && value.getValue() != null) {
                    ViewerEntry entry = new ViewerEntry(value.getValue(), StringUtil.EMPTY_STRING); //$NON-NLS-1$
                    toSelect.add(entry);
                }
            }

            ISelection selection = new StructuredSelection(toSelect.toArray());
            viewer.setSelection(selection);
        }
    }

    @Override
    public Control getControl() {
        return viewer.getControl();
    }

    @Override
    public boolean isValid(ReportFilterValue value) {
        return value.getValue() != null && !value.getValue().isEmpty()
            && value.getSecondValue() == null;
    }

    @Override
    public void setOptions(Map<String, String> options) {
        clearViewer();

        List<ViewerEntry> entries = new ArrayList<ViewerEntry>();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            entries.add(new ViewerEntry(entry.getKey(), entry.getValue()));
        }

        viewer.add(entries.toArray());
    }

    protected final T getViewer() {
        return viewer;
    }

    /**
     * Remove all elements from the viewer.
     */
    protected abstract void clearViewer();

    private void setLabelProvider() {
        viewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ViewerEntry) {
                    ViewerEntry entry = (ViewerEntry) element;
                    return entry.getValue();
                }
                return StringUtil.EMPTY_STRING; //$NON-NLS-1$
            }
        });
    }

    private static class ViewerEntry {
        private final String key;
        private final String value;

        public ViewerEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ViewerEntry other = (ViewerEntry) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            return true;
        }
    }
}
