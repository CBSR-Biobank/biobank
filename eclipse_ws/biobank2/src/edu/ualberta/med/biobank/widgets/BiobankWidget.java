package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class BiobankWidget extends Composite {

    List<BiobankEntryFormWidgetListener> listeners;

    public BiobankWidget(Composite parent, int style) {
        super(parent, style);
        listeners = new ArrayList<BiobankEntryFormWidgetListener>();
    }

    public void adaptToToolkit(FormToolkit toolkit, boolean paintBorder) {
        toolkit.adapt(this, true, true);
        adaptAllChildren(this, toolkit);
        if (paintBorder) {
            toolkit.paintBordersFor(this);
        }
    }

    private void adaptAllChildren(Composite container, FormToolkit toolkit) {
        Control[] children = container.getChildren();
        for (Control aChild : children) {
            toolkit.adapt(aChild, true, true);
            if (aChild instanceof Composite) {
                adaptAllChildren((Composite) aChild, toolkit);
            }
        }
    }

    public void addSelectionChangedListener(
        BiobankEntryFormWidgetListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionChangedListener(
        BiobankEntryFormWidgetListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(MultiSelectEvent event) {
        for (BiobankEntryFormWidgetListener listener : listeners) {
            listener.selectionChanged(event);
        }
    }

    public void notifyListeners() {
        notifyListeners(new MultiSelectEvent(this));
    }
}
