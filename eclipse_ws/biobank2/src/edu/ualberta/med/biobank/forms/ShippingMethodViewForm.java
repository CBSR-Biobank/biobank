package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.infotables.entry.ShippingMethodEntryInfoTable;

public class ShippingMethodViewForm extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.ShippingMethodViewForm"; //$NON-NLS-1$

    public static final String OK_MESSAGE = Messages.ShippingMethodViewForm_ok_msg;

    private ShippingMethodEntryInfoTable statusWidget;

    @Override
    public void init() throws Exception {
        setPartName(Messages.ShippingMethodViewForm_title);
        checkEditAccess();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.ShippingMethodViewForm_title);
        page.setLayout(new GridLayout(1, false));
        createGlobalSampleTypeSection();
    }

    private void createGlobalSampleTypeSection() throws Exception {
        Section section = createSection(Messages.ShippingMethodViewForm_title);
        List<ShippingMethodWrapper> globalShippingMethod = ShippingMethodWrapper
            .getShippingMethods(SessionManager.getAppService());
        if (globalShippingMethod == null) {
            globalShippingMethod = new ArrayList<ShippingMethodWrapper>();
        }
        statusWidget = new ShippingMethodEntryInfoTable(section,
            globalShippingMethod, Messages.ShippingMethodViewForm_add_msg,
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

    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(ShippingMethodWrapper.class)
            && !SessionManager.canCreate(ShippingMethodWrapper.class)
            && !SessionManager.canDelete(ShippingMethodWrapper.class)) {
            BgcPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                Messages.ShippingMethodViewForm_access_denied_error_msg);
        }
    }

}
