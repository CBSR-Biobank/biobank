package edu.ualberta.med.biobank.widgets.utils;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;

public class GuiUtil {
    public static void resetComboViewer(ComboViewer comboViewer,
        Object defaultValue) {
        if (comboViewer != null) {
            if (defaultValue != null) {
                comboViewer.setSelection(new StructuredSelection(defaultValue));
            } else if (comboViewer.getCombo().getItemCount() == 1)
                comboViewer.setSelection(new StructuredSelection(comboViewer
                    .getElementAt(0)));
            else
                comboViewer.getCombo().deselectAll();
        }
    }
}
