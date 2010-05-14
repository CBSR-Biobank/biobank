package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.entry.ShippingMethodEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShippingMethodEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ShippingMethodEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ShippingMethodEntryForm";
    public static final String OK_MESSAGE = "View and edit shipping methods.";

    private ShippingMethodEntryInfoTable globalShippingWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        setPartName("Shipping Methods Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipping Method Information");
        form.getBody().setLayout(new GridLayout(1, false));

        createGlobalShippingMethodSection();
        setFirstControl(globalShippingWidget);

    }

    private void createGlobalShippingMethodSection() throws Exception {
        Section section = createSection("Global shipping methods");
        List<ShippingMethodWrapper> globalShippingMethods = ShippingMethodWrapper
            .getShippingMethods(appService);
        if (globalShippingMethods == null) {
            globalShippingMethods = new ArrayList<ShippingMethodWrapper>();
        }
        globalShippingWidget = new ShippingMethodEntryInfoTable(section,
            globalShippingMethods, "Add a new global shipping method",
            "Edit the global shipping methods", null);
        globalShippingWidget.adaptToToolkit(toolkit, true);
        globalShippingWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(globalShippingWidget);

        addSectionToolbar(section, "Add Global Shipping Method",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    globalShippingWidget.addShippingMethod();
                }
            });
        section.setClient(globalShippingWidget);
    }

    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        ShippingMethodWrapper.persistShippingMethods(globalShippingWidget
            .getAddedOrModifiedShippingMethods(), globalShippingWidget
            .getDeletedShippingMethods());
    }

    @Override
    public String getNextOpenedFormID() {
        return null;
    }

    @Override
    protected String getOkMessage() {
        return null;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        List<ShippingMethodWrapper> globalShippingMethods = null;
        try {
            globalShippingMethods = ShippingMethodWrapper
                .getShippingMethods(appService);
        } catch (ApplicationException e) {
            logger.error("Can't reset global shipping methods", e);
        }
        if (globalShippingMethods != null) {
            globalShippingWidget.setLists(globalShippingMethods);
        }
    }

    @Override
    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(ShippingMethodWrapper.class)
            && !SessionManager.canCreate(ShippingMethodWrapper.class)
            && !SessionManager.canDelete(ShippingMethodWrapper.class)) {
            BioBankPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access Shipping Method editor. Access Denied.");
        }
    }

}
