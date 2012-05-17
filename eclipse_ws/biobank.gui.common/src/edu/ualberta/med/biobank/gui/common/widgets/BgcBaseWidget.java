package edu.ualberta.med.biobank.gui.common.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BgcBaseWidget extends Composite {

    List<BgcEntryFormWidgetListener> listeners;

    public static ControlDecoration createDecorator(Control control,
        String message) {
        return createDecorator(control, message, SWT.RIGHT | SWT.TOP);
    }

    public static ControlDecoration createDecorator(Control control,
        String message, int style) {
        ControlDecoration controlDecoration = new ControlDecoration(control,
            style);
        controlDecoration.setDescriptionText(message);
        FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
            .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
        controlDecoration.setImage(fieldDecoration.getImage());
        return controlDecoration;
    }

    public BgcBaseWidget(Composite parent, int style) {
        super(parent, style); // | SWT.H_SCROLL | SWT.V_SCROLL);
        listeners = new ArrayList<BgcEntryFormWidgetListener>();
    }

    public void adaptToToolkit(FormToolkit toolkit, boolean paintBorder) {
        toolkit.adapt(this, true, false);
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
        BgcEntryFormWidgetListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionChangedListener(
        BgcEntryFormWidgetListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(MultiSelectEvent event) {
        for (BgcEntryFormWidgetListener listener : listeners) {
            listener.selectionChanged(event);
        }
    }

    public void notifyListeners() {
        notifyListeners(new MultiSelectEvent(this));
    }
}
