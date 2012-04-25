package edu.ualberta.med.biobank.widgets.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.ReportFilterValue;

public class SelectFilterValueWidget implements FilterValueWidget {
    private ComboViewer listViewer;
    private Map<String, String> options;

    public SelectFilterValueWidget(Composite parent) {
        this.listViewer = new ComboViewer(parent, SWT.READ_ONLY);

        setLabelProvider();
    }

    @Override
    public Collection<ReportFilterValue> getValues() {
        Collection<ReportFilterValue> values = new ArrayList<ReportFilterValue>();
        ISelection selection = listViewer.getSelection();
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
        if (!listViewer.getControl().isDisposed()) {
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
            listViewer.setSelection(selection);
        }
    }

    @Override
    public Control getControl() {
        return listViewer.getControl();
    }

    @Override
    public void addChangeListener(
        final ChangeListener<ChangeEvent> changeListener) {
        Combo combo = listViewer.getCombo();
        if (combo.isDisposed()) {
            combo.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    changeListener.handleEvent(null);
                }
            });
        }
    }

    @Override
    public boolean isValid(ReportFilterValue value) {
        return options != null && options.containsKey(value.getValue());
    }

    @Override
    public String toString(ReportFilterValue value) {
        return options.get(value.getValue());
    }

    public void setOptions(Map<String, String> options) {
        this.options = new HashMap<String, String>(options);

        Combo combo = listViewer.getCombo();
        if (!combo.isDisposed()) {
            combo.removeAll();

            List<ViewerEntry> entries = new ArrayList<ViewerEntry>();
            for (Map.Entry<String, String> entry : options.entrySet()) {
                entries.add(new ViewerEntry(entry.getKey(), entry.getValue()));
            }

            listViewer.add(entries.toArray());
        }
    }

    private void setLabelProvider() {
        listViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ViewerEntry) {
                    ViewerEntry entry = (ViewerEntry) element;
                    return entry.getValue();
                }
                return StringUtil.EMPTY_STRING;
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
