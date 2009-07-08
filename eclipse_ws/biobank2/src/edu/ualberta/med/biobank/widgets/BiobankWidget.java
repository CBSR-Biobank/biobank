
package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BiobankWidget extends Composite {

    public BiobankWidget(Composite parent, int style) {
        super(parent, style);
    }

    public void adaptToToolkit(FormToolkit toolkit) {
        toolkit.adapt(this, true, true);
        adaptAllChildren(this, toolkit);
    }

    private void adaptAllChildren(Composite container, FormToolkit toolkit) {
        Control [] children = container.getChildren();
        for (Control aChild : children) {
            toolkit.adapt(aChild, true, true);
            if (aChild instanceof Composite) {
                adaptAllChildren((Composite) aChild, toolkit);
            }
        }
    }
}
