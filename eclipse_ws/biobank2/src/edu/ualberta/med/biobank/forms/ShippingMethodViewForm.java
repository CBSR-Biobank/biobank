package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.widgets.infotables.entry.ShippingMethodEntryInfoTable;

public class ShippingMethodViewForm extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.ShippingMethodViewForm";

    public static final String OK_MESSAGE = "Add or edit a shipping method";

    private ShippingMethodEntryInfoTable statusWidget;

    @Override
    public void init() throws Exception {
        setPartName("Shipping Method");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipping Method");
        page.setLayout(new GridLayout(1, false));
        createGlobalSampleTypeSection();
    }

    private void createGlobalSampleTypeSection() throws Exception {
        Section section = createSection("Shipping Method");
        List<ShippingMethodWrapper> globalShippingMethod = ShippingMethodWrapper
            .getShippingMethods(appService);
        if (globalShippingMethod == null) {
            globalShippingMethod = new ArrayList<ShippingMethodWrapper>();
        }
        statusWidget = new ShippingMethodEntryInfoTable(section,
            globalShippingMethod, "Add a new global shipping method",
            "Edit the global shipping method");
        statusWidget.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(statusWidget);

        addSectionToolbar(section, "Add a shipping method",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    statusWidget.addShippingMethod();
                }
            }, ShippingMethodWrapper.class);
        section.setClient(statusWidget);
    }

    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(ShippingMethodWrapper.class)
            && !SessionManager.canCreate(ShippingMethodWrapper.class)
            && !SessionManager.canDelete(ShippingMethodWrapper.class)) {
            BiobankGuiCommonPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access  Shipping Method editor. Access Denied.");
        }
    }

}
