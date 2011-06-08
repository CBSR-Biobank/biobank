package edu.ualberta.med.biobank.widgets.utils;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.gui.common.widgets.utils.CommonWidgetCreator;

public class WidgetCreator extends CommonWidgetCreator {

    public WidgetCreator(Map<String, Control> controls) {
        super(controls);
    }

    public void addSectionToolbar(Section section, String tooltip,
        SelectionListener listener, Class<?> wrapperTypeToAdd, String imageKey) {
        if (wrapperTypeToAdd == null
            || SessionManager.canCreate(wrapperTypeToAdd)) {
            ToolBar tbar = (ToolBar) section.getTextClient();
            if (tbar == null) {
                tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
                section.setTextClient(tbar);
            }

            ToolItem titem = new ToolItem(tbar, SWT.NULL);
            if (imageKey == null) {
                imageKey = BiobankPlugin.IMG_ADD;
            }
            titem.setImage(BiobankPlugin.getDefault().getImageRegistry()
                .get(imageKey));
            titem.setToolTipText(tooltip);
            titem.addSelectionListener(listener);
        }
    }
}
