package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.treeview.util.CallRunnablePersistOnAdapter;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.views.DispatchShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class DispatchShipmentSendingEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentSendingEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchShipmentSendingEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new dispatch shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing dispatch shipment record.";

    private SiteWrapper site;

    private DispatchShipmentWrapper shipment;

    private ComboViewer studyComboViewer;

    private ComboViewer destSiteComboViewer;

    private DateTimeWidget dateShippedWidget;

    private ComboViewer shippingMethodComboViewer;

    private ComboViewer activityStatusComboViewer;

    private DispatchAliquotListInfoTable aliquotsWidget;

    private List<ContainerWrapper> removedPallets = new ArrayList<ContainerWrapper>();

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof DispatchShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipment = (DispatchShipmentWrapper) adapter.getModelObject();
        site = SessionManager.getInstance().getCurrentSite();
        shipment.setSender(site);
        try {
            shipment.reload();
        } catch (Exception e) {
            logger.error("Error while retrieving shipment", e);
        }
        String tabName;
        if (shipment.isNew()) {
            tabName = "New Dispatch Shipment";
            shipment.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            tabName = "Dispatch Shipment "
                + shipment.getFormattedDateReceived();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Shipment Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        BiobankText siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Sender Site");
        setTextValue(siteLabel, site.getName());

        List<StudyWrapper> possibleStudies = site.getDispatchStudies();
        StudyWrapper study = shipment.getStudy();
        if ((study == null) && (possibleStudies != null)
            && (possibleStudies.size() == 1)) {
            study = possibleStudies.get(0);
        }
        studyComboViewer = createComboViewerWithNoSelectionValidator(client,
            "Study", possibleStudies, study,
            "Shipment must have a receiving site");
        shipment.setStudy(study);
        studyComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection studySelection = (IStructuredSelection) studyComboViewer
                        .getSelection();
                    StudyWrapper study = null;
                    if ((studySelection != null) && (studySelection.size() > 0)) {
                        study = (StudyWrapper) studySelection.getFirstElement();
                        try {
                            List<SiteWrapper> possibleDestSites = site
                                .getStudyDispachSites(study);
                            destSiteComboViewer.setInput(possibleDestSites);

                            if (possibleDestSites.size() == 1) {
                                destSiteComboViewer
                                    .setSelection(new StructuredSelection(
                                        possibleDestSites.get(0)));
                            } else {
                                destSiteComboViewer.setSelection(null);
                            }
                        } catch (Exception e) {
                            logger
                                .error(
                                    "Error while retrieving dispatch shipment destination sites",
                                    e);
                        }
                    }
                    shipment.setStudy(study);
                }
            });

        List<SiteWrapper> possibleDestSites = null;
        SiteWrapper destSite = null;
        if (study != null) {
            possibleDestSites = site.getStudyDispachSites(study);
            destSite = shipment.getReceiver();
        }
        if ((destSite == null) && (possibleDestSites != null)
            && (possibleDestSites.size() == 1)) {
            destSite = possibleDestSites.get(0);
        }
        shipment.setReceiver(destSite);
        destSiteComboViewer = createComboViewerWithNoSelectionValidator(client,
            "Receiver Site", possibleDestSites, destSite,
            "Shipment must have an associated study");
        destSiteComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    SiteWrapper destSite = null;
                    IStructuredSelection destSiteSelecion = (IStructuredSelection) destSiteComboViewer
                        .getSelection();
                    if ((destSiteSelecion != null)
                        && (destSiteSelecion.size() > 0)) {
                        destSite = (SiteWrapper) destSiteSelecion
                            .getFirstElement();
                    }
                    shipment.setReceiver(destSite);
                }
            });

        ShippingMethodWrapper selectedShippingMethod = shipment
            .getShippingMethod();
        shippingMethodComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Shipping Method",
            ShippingMethodWrapper.getShippingMethods(appService),
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

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Waybill", null,
            BeansObservables.observeValue(shipment, "waybill"), null);

        final AbstractValidator dateNotNul = new AbstractValidator(
            "Date shipped should be set") {
            @Override
            public IStatus validate(Object value) {
                if (value == null && shipment.isInTransit()) {
                    showDecoration();
                    return ValidationStatus.error(errorMessage);
                } else {
                    hideDecoration();
                    return Status.OK_STATUS;
                }
            }
        };
        dateShippedWidget = widgetCreator.createDateTimeWidget(client,
            "Date Shipped", shipment.getDateShipped(),
            BeansObservables.observeValue(shipment, "dateShipped"), dateNotNul);

        activityStatusComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            shipment.getActivityStatus(),
            "Container must have an activity status");
        activityStatusComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    ActivityStatusWrapper activityStatus = null;
                    IStructuredSelection asSelection = (IStructuredSelection) activityStatusComboViewer
                        .getSelection();
                    if ((asSelection != null) && (asSelection.size() > 0)) {
                        activityStatus = (ActivityStatusWrapper) asSelection
                            .getFirstElement();
                    }
                    shipment.setActivityStatus(activityStatus);

                    // Do this to trigger the validator and value binding on
                    // form
                    Date date = dateShippedWidget.getDate();
                    dateShippedWidget.setDate(new Date());
                    dateShippedWidget.setDate(date);

                    if (shipment.isInTransit()
                        && shipment.getAliquotCollection().size() == 0) {
                        BioBankPlugin
                            .openAsyncInformation(
                                "No aliquots",
                                "There are no aliquots added to this shipment, are you sure it should be set to 'In Transit' status ?");
                    }
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null,
            BeansObservables.observeValue(shipment, "comment"), null);

        createAliquotsSection();

        // ---

        setFirstControl(studyComboViewer.getControl());

        if ((possibleStudies == null) || (possibleStudies.size() == 0)) {
            BioBankPlugin.openAsyncError("Sender Site Error",
                "The current site does not have any dispatch studies associated with it.\n"
                    + "Please close the form.");
        }
    }

    private void createAliquotsSection() {
        Section section = createSection("Aliquots");
        if (shipment.isInCreation()) {
            addSectionToolbar(section, "Add aliquots to this shipment",
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        DispatchCreateScanDialog dialog = new DispatchCreateScanDialog(
                            PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getShell(),
                            shipment);
                        dialog.open();
                        setDirty(true); // FIXME need to do this better !
                        aliquotsWidget.reloadCollection(shipment
                            .getAliquotCollection());
                        removedPallets.addAll(dialog.getRemovedPallets());
                    }
                });
        }
        aliquotsWidget = new DispatchAliquotListInfoTable(section, shipment,
            shipment.getAliquotCollection(), true);
        aliquotsWidget.adaptToToolkit(toolkit, true);
        aliquotsWidget.addDoubleClickListener(collectionDoubleClickListener);
        aliquotsWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        section.setClient(aliquotsWidget);
    }

    @Override
    protected void saveForm() throws Exception {
        if (shipment.isInTransit()
            && shipment.getAliquotCollection().size() == 0) {
            boolean ok = BioBankPlugin.openConfirm("No aliquots",
                "There are no aliquots added to this shipment, are you sure"
                    + " it should be save with a 'Sent' status ?");
            if (!ok) {
                setDirty(true);
                return;
            }
        }
        IRunnableContext context = new ProgressMonitorDialog(Display
            .getDefault().getActiveShell());
        context.run(true, false, new CallRunnablePersistOnAdapter(adapter) {
            @Override
            public void afterPersist() throws Exception {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        DispatchShipmentAdministrationView.getCurrent()
                            .reload();
                    }
                });
            }

            @Override
            public void doSetDirty(boolean b) {
                setDirty(b);
            }
        });

    }

    @Override
    protected String getOkMessage() {
        return (shipment.isNew()) ? MSG_NEW_SHIPMENT_OK : MSG_SHIPMENT_OK;
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchShipmentViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        StudyWrapper study = shipment.getStudy();
        if (study != null) {
            studyComboViewer.setSelection(new StructuredSelection(study));
        } else if (studyComboViewer.getCombo().getItemCount() > 1) {
            studyComboViewer.getCombo().deselectAll();
        }
        SiteWrapper destSite = shipment.getReceiver();
        if (destSite != null) {
            destSiteComboViewer.setSelection(new StructuredSelection(destSite));
        } else if (destSiteComboViewer.getCombo().getItemCount() > 1) {
            destSiteComboViewer.getCombo().deselectAll();
        }
        ShippingMethodWrapper shipMethod = shipment.getShippingMethod();
        if (shipMethod != null) {
            shippingMethodComboViewer.setSelection(new StructuredSelection(
                shipMethod));
        } else if (shippingMethodComboViewer.getCombo().getItemCount() > 1) {
            shippingMethodComboViewer.getCombo().deselectAll();
        }
        ActivityStatusWrapper activity = shipment.getActivityStatus();
        if (activity != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                activity));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
        dateShippedWidget.setDate(new Date());
    }
}
