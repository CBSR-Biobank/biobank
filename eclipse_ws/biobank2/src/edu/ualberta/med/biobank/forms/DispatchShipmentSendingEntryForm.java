package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper.STATE;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.views.DispatchShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class DispatchShipmentSendingEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentSendingEntryForm.class.getName());

    public static final String ID =
        "edu.ualberta.med.biobank.forms.DispatchShipmentSendingEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK =
        "Creating a new dispatch shipment record.";

    public static final String MSG_SHIPMENT_OK =
        "Editing an existing dispatch shipment record.";

    private SiteWrapper site;

    private DispatchShipmentWrapper shipment;

    private ComboViewer studyComboViewer;

    private ComboViewer destSiteComboViewer;

    private DispatchAliquotListInfoTable aliquotsWidget;

    private List<ContainerWrapper> removedPallets =
        new ArrayList<ContainerWrapper>();

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
        } else {
            tabName =
                "Dispatch Shipment " + shipment.getFormattedDateReceived();
        }
        setPartName(tabName);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // if the shipment is new, and if the combos hold only one element,
        // there will be default selections but dirty will be set to false by
        // default anyway
        if (shipment.isNew()) {
            setDirty(true);
        }
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

        BiobankText siteLabel =
            createReadOnlyLabelledField(client, SWT.NONE, "Sender Site");
        setTextValue(siteLabel, site.getName());

        createStudyAndReceiverCombos(client);

        BiobankText commentText =
            (BiobankText) createBoundWidgetWithLabel(client, BiobankText.class,
                SWT.MULTI, "Comments", null,
                BeansObservables.observeValue(shipment, "comment"), null);

        createAliquotsSection();

        if (studyComboViewer == null)
            setFirstControl(commentText);
        else
            setFirstControl(studyComboViewer.getCombo());
    }

    private void createStudyAndReceiverCombos(Composite client) {
        StudyWrapper study = shipment.getStudy();
        if (shipment.isInTransitState()) {
            BiobankText studyLabel =
                createReadOnlyLabelledField(client, SWT.NONE, "Study");
            setTextValue(studyLabel, shipment.getStudy().getNameShort());
            BiobankText receiverLabel =
                createReadOnlyLabelledField(client, SWT.NONE, "Receiver Site");
            setTextValue(receiverLabel, shipment.getReceiver().getNameShort());
        } else {
            List<StudyWrapper> possibleStudies =
                site.getDispatchStudiesAsSender();
            if ((study == null) && (possibleStudies != null)
                && (possibleStudies.size() == 1)) {
                study = possibleStudies.get(0);
            }
            studyComboViewer =
                createComboViewer(client, "Study", possibleStudies, study,
                    "Shipment must have a receiving site",
                    new ComboSelectionUpdate() {
                        @Override
                        public void doSelection(Object selectedObject) {
                            StudyWrapper study = (StudyWrapper) selectedObject;
                            if (destSiteComboViewer != null) {
                                try {
                                    List<SiteWrapper> possibleDestSites =
                                        site.getStudyDispachSites(study);
                                    destSiteComboViewer
                                        .setInput(possibleDestSites);

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
                            setDirty(true);
                        }
                    });

            destSiteComboViewer =
                createComboViewer(client, "Receiver Site", null, null,
                    "Shipment must have an associated study",
                    new ComboSelectionUpdate() {
                        @Override
                        public void doSelection(Object selectedObject) {
                            shipment.setReceiver((SiteWrapper) selectedObject);
                            setDirty(true);
                        }
                    });

            if ((possibleStudies == null) || (possibleStudies.size() == 0)) {
                BioBankPlugin.openAsyncError("Sender Site Error",
                    "The current site does not have any dispatch studies associated with it.\n"
                        + "Please close the form.");
            }
            if (study != null) {
                // will trigger the listener and set the value :
                studyComboViewer.setSelection(new StructuredSelection(study));
            }
        }
    }

    private void createAliquotsSection() {
        Section section = createSection("Aliquots");
        Composite composite = toolkit.createComposite(section);
        composite.setLayout(new GridLayout(1, false));
        section.setClient(composite);
        if (shipment.isInCreationState()) {
            addSectionToolbar(section, "Add aliquots to this shipment",
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        openScanDialog();
                    }
                }, null, BioBankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT);

            Composite addComposite = toolkit.createComposite(composite);
            addComposite.setLayout(new GridLayout(5, false));
            toolkit.createLabel(addComposite, "Enter inventory ID to add:");
            final BiobankText newAliquotText =
                new BiobankText(addComposite, SWT.NONE, toolkit);
            newAliquotText.addListener(SWT.DefaultSelection, new Listener() {
                @Override
                public void handleEvent(Event e) {
                    addAliquot(newAliquotText.getText());
                    newAliquotText.setFocus();
                    newAliquotText.setText("");
                }
            });
            Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
            addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
                .get(BioBankPlugin.IMG_ADD));
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addAliquot(newAliquotText.getText());
                }
            });
            toolkit.createLabel(addComposite, "or open scan dialog:");
            Button openScanButton =
                toolkit.createButton(addComposite, "", SWT.PUSH);
            openScanButton.setImage(BioBankPlugin.getDefault()
                .getImageRegistry()
                .get(BioBankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT));
            openScanButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    openScanDialog();
                }
            });
        }
        aliquotsWidget =
            new DispatchAliquotListInfoTable(composite, shipment, true) {
                @Override
                public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                    return shipment.getDispatchShipmentAliquotCollection();
                }
            };
        aliquotsWidget.adaptToToolkit(toolkit, true);
        aliquotsWidget.addDoubleClickListener(collectionDoubleClickListener);
        aliquotsWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
    }

    private void openScanDialog() {
        DispatchCreateScanDialog dialog =
            new DispatchCreateScanDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), shipment);
        dialog.open();
        setDirty(true); // FIXME need to do this better !
        aliquotsWidget.reloadCollection();
        removedPallets.addAll(dialog.getRemovedPallets());
    }

    protected void addAliquot(String inventoryId) {
        if (!inventoryId.isEmpty()) {
            try {
                List<AliquotWrapper> aliquots =
                    AliquotWrapper.getAliquots(shipment.getAppService(),
                        inventoryId);
                if (aliquots.size() == 0)
                    BioBankPlugin.openError("Aliquot not found",
                        "Aliquot with inventory id " + inventoryId
                            + " has not been found.");
                else if (aliquots.size() > 1)
                    BioBankPlugin.openError("Aliquots problems",
                        "More than one aliquots with inventory id "
                            + inventoryId + " has been found.");
                else
                    addAliquot(aliquots.get(0));

            } catch (ApplicationException ae) {
                BioBankPlugin.openAsyncError("Error while looking up patient",
                    ae);
            }
        }
    }

    private void addAliquot(AliquotWrapper aliquot) {
        List<AliquotWrapper> aliquots = shipment.getAliquotCollection();
        if (aliquots != null && aliquots.contains(aliquot)) {
            BioBankPlugin.openAsyncError("Error",
                "Aliquot " + aliquot.getInventoryId()
                    + " has already been added to this shipment");
            return;
        }
        if (!aliquot.isDispatched()) {
            try {
                shipment.addAliquots(Arrays.asList(aliquot), STATE.NONE_STATE);
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Error adding aliquots", e);
            }
            aliquotsWidget.reloadCollection();
            aliquotsWidget.notifyListeners();
        } else {
            BioBankPlugin
                .openAsyncError(
                    "Error",
                    "Aliquot "
                        + aliquot.getInventoryId()
                        + " can't be added to this shipment: it is already dispatched.");
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (shipment.isInTransitState()
            && shipment.getAliquotCollection().size() == 0) {
            boolean ok =
                BioBankPlugin.openConfirm("No aliquots",
                    "There are no aliquots added to this shipment, are you sure"
                        + " it should be save with a 'Sent' status ?");
            if (!ok) {
                setDirty(true);
                return;
            }
        }
        shipment.persist();
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                DispatchShipmentAdministrationView.getCurrent().reload();
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
    }
}
