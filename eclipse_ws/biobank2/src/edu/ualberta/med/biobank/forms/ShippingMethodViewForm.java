package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.widgets.infotables.entry.ShippingMethodEntryInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShippingMethodViewForm extends BiobankFormBase {

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ShippingMethodViewForm";

    public static final String OK_MESSAGE =
        "Add or edit a shipping method";

    private ShippingMethodEntryInfoTable statusWidget;
    private List<ShippingMethodWrapper> globalShippingMethods;

    @Override
    public void init() throws Exception {
        setPartName(ShippingMethod.NAME.singular().toString());
        setShippingMethodInfo();
    }

    private void setShippingMethodInfo() throws ApplicationException {
        globalShippingMethods =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                SessionManager.getAppService()
                    .doAction(
                        new ShippingMethodGetInfoAction()).getList(),
                ShippingMethodWrapper.class);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(ShippingMethod.NAME.singular().toString());
        page.setLayout(new GridLayout(1, false));
        createGlobalShippingMethodSection();
    }

    private void createGlobalShippingMethodSection() throws Exception {
        Section section =
            createSection(ShippingMethod.NAME.singular().toString());
        if (globalShippingMethods == null) {
            globalShippingMethods = new ArrayList<ShippingMethodWrapper>();
        }
        statusWidget = new ShippingMethodEntryInfoTable(section,
            globalShippingMethods, "Add a new global shipping method",
            "Edit the global shipping method");
        statusWidget.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(statusWidget);

        addSectionToolbar(section,
            "Add a shipping method",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    statusWidget.addShippingMethod();
                }
            }, ShippingMethodWrapper.class);
        section.setClient(statusWidget);
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }

}
