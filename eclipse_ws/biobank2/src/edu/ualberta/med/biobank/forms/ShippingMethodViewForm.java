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
import edu.ualberta.med.biobank.widgets.infotables.entry.ShippingMethodEntryInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ShippingMethodViewForm extends BiobankFormBase {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ShippingMethodViewForm"; //$NON-NLS-1$

    public static final String OK_MESSAGE =
        Messages.ShippingMethodViewForm_ok_msg;

    private ShippingMethodEntryInfoTable statusWidget;
    private List<ShippingMethodWrapper> globalShippingMethods;

    @Override
    public void init() throws Exception {
        setPartName(Messages.ShippingMethodViewForm_title);
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
        form.setText(Messages.ShippingMethodViewForm_title);
        page.setLayout(new GridLayout(1, false));
        createGlobalShippingMethodSection();
    }

    private void createGlobalShippingMethodSection() throws Exception {
        Section section = createSection(Messages.ShippingMethodViewForm_title);
        if (globalShippingMethods == null) {
            globalShippingMethods = new ArrayList<ShippingMethodWrapper>();
        }
        statusWidget = new ShippingMethodEntryInfoTable(section,
            globalShippingMethods, Messages.ShippingMethodViewForm_add_msg,
            Messages.ShippingMethodViewForm_edit_msg);
        statusWidget.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(statusWidget);

        addSectionToolbar(section,
            Messages.ShippingMethodViewForm_add_button_label,
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
