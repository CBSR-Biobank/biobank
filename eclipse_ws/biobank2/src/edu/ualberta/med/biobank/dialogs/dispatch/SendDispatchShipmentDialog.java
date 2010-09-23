package edu.ualberta.med.biobank.dialogs.dispatch;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.validators.DateNotNulValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class SendDispatchShipmentDialog extends BiobankDialog {

    private static final String TITLE = "Dispatching aliquots";
    private DispatchShipmentWrapper shipment;
    private ComboViewer shippingMethodComboViewer;

    public SendDispatchShipmentDialog(Shell parentShell,
        DispatchShipmentWrapper shipment) {
        super(parentShell);
        this.shipment = shipment;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Fill the following fields to complete the shipment";
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        ShippingMethodWrapper selectedShippingMethod = shipment
            .getShippingMethod();
        shippingMethodComboViewer = widgetCreator
            .createComboViewerWithNoSelectionValidator(contents,
                "Shipping Method", ShippingMethodWrapper
                    .getShippingMethods(SessionManager.getAppService()),
                selectedShippingMethod, null);
        shippingMethodComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ShippingMethodWrapper shippingMethod = null;
                    IStructuredSelection shippingMethodSelection = (IStructuredSelection) shippingMethodComboViewer
                        .getSelection();
                    if ((shippingMethodSelection != null)
                        && (shippingMethodSelection.size() > 0)) {
                        shippingMethod = (ShippingMethodWrapper) shippingMethodSelection
                            .getFirstElement();
                    }
                    shipment.setShippingMethod(shippingMethod);
                }
            });

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.NONE,
            "Waybill", null,
            BeansObservables.observeValue(shipment, "waybill"), null);

        widgetCreator.createDateTimeWidget(contents, "Date Shipped",
            shipment.getDateShipped(),
            BeansObservables.observeValue(shipment, "dateShipped"),
            new DateNotNulValidator("Date shipped should be set"));
    }

}
