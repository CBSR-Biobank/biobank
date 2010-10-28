package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DispatchAliquotsTreeTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class DispatchShipmentSendingEntryForm extends
    AbstractDispatchShipmentEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchShipmentSendingEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchShipmentSendingEntryForm";

    public static final String MSG_NEW_SHIPMENT_OK = "Creating a new dispatch shipment record.";

    public static final String MSG_SHIPMENT_OK = "Editing an existing dispatch shipment record.";

    private ComboViewer studyComboViewer;

    private ComboViewer destSiteComboViewer;

    protected DispatchAliquotListInfoTable aliquotsNonProcessedTable;

    private DispatchAliquotsTreeTable aliquotsTreeTable;

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

        BiobankText siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Sender Site");
        setTextValue(siteLabel, site.getName());

        createStudyAndReceiverCombos(client);

        if (!shipment.isNew() && !shipment.isInCreationState()) {
            ShippingMethodWrapper selectedShippingMethod = shipment
                .getShippingMethod();
            widgetCreator.createComboViewer(client, "Shipping Method",
                ShippingMethodWrapper.getShippingMethods(SessionManager
                    .getAppService()), selectedShippingMethod, null,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        shipment
                            .setShippingMethod((ShippingMethodWrapper) selectedObject);
                    }
                });

            createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
                "Waybill", null, shipment, "waybill", null);
        }

        BiobankText commentText = (BiobankText) createBoundWidgetWithLabel(
            client, BiobankText.class, SWT.MULTI, "Comments", null,
            BeansObservables.observeValue(shipment, "comment"), null);

        createAliquotsSelectionSection();

        if (studyComboViewer == null)
            setFirstControl(commentText);
        else
            setFirstControl(studyComboViewer.getCombo());
    }

    private void createStudyAndReceiverCombos(Composite client) {
        StudyWrapper study = shipment.getStudy();
        if (shipment.isInTransitState()) {
            BiobankText studyLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Study");
            setTextValue(studyLabel, shipment.getStudy().getNameShort());
            BiobankText receiverLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Receiver Site");
            setTextValue(receiverLabel, shipment.getReceiver().getNameShort());
        } else {
            List<StudyWrapper> possibleStudies = site
                .getDispatchStudiesAsSender();
            if ((study == null) && (possibleStudies != null)
                && (possibleStudies.size() == 1)) {
                study = possibleStudies.get(0);
            }
            studyComboViewer = createComboViewer(client, "Study",
                possibleStudies, study, "Shipment must have a receiving site",
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        StudyWrapper study = (StudyWrapper) selectedObject;
                        if (destSiteComboViewer != null) {
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
                        setDirty(true);
                    }
                });

            destSiteComboViewer = createComboViewer(client, "Receiver Site",
                null, null, "Shipment must have an associated study",
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

    private void createAliquotsSelectionSection() {
        if (shipment.isInCreationState()) {
            Section section = createSection("Aliquot added");
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

                createAliquotsSelectionActions(composite, false);
                createAliquotsNonProcessedSection(true);
            }
        } else {
            aliquotsTreeTable = new DispatchAliquotsTreeTable(page, shipment,
                !shipment.isInClosedState() && !shipment.isInLostState(), true);
            aliquotsTreeTable.addSelectionChangedListener(biobankListener);
        }
    }

    protected void createAliquotsNonProcessedSection(boolean edit) {
        String title = "Non processed aliquots";
        if (shipment.isInCreationState()) {
            title = "Added aliquots";
        }
        Composite parent = createSectionWithClient(title);
        aliquotsNonProcessedTable = new DispatchAliquotListInfoTable(parent,
            shipment, edit) {
            @Override
            public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                return shipment
                    .getNonProcessedDispatchShipmentAliquotCollection();
            }

        };
        aliquotsNonProcessedTable.adaptToToolkit(toolkit, true);
        aliquotsNonProcessedTable
            .addDoubleClickListener(new IDoubleClickListener() {
                @Override
                public void doubleClick(DoubleClickEvent event) {
                    Object selection = event.getSelection();
                    if (selection instanceof InfoTableSelection) {
                        InfoTableSelection tableSelection = (InfoTableSelection) selection;
                        DispatchShipmentAliquotWrapper dsa = (DispatchShipmentAliquotWrapper) tableSelection
                            .getObject();
                        if (dsa != null) {
                            SessionManager.openViewForm(dsa.getAliquot());
                        }
                    }
                }
            });
        aliquotsNonProcessedTable.addSelectionChangedListener(biobankListener);
    }

    @Override
    protected void openScanDialog() {
        DispatchCreateScanDialog dialog = new DispatchCreateScanDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            shipment);
        dialog.open();
        setDirty(true); // FIXME need to do this better !
        reloadAliquots();
    }

    @Override
    protected void doAliquotTextAction(String text) {
        addAliquot(text);
    }

    protected void addAliquot(String inventoryId) {
        if (!inventoryId.isEmpty()) {
            try {
                AliquotWrapper existingAliquot = AliquotWrapper.getAliquot(
                    shipment.getAppService(), inventoryId,
                    SessionManager.getUser());
                if (existingAliquot == null)
                    BioBankPlugin.openError("Aliquot not found",
                        "Aliquot with inventory id " + inventoryId
                            + " has not been found.");
                else
                    addAliquot(existingAliquot);

            } catch (Exception ae) {
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
        try {
            shipment.addNewAliquots(Arrays.asList(aliquot), true);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error adding aliquots", e);
        }
        reloadAliquots();
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
        super.reset();
        shipment.setSender(SessionManager.getCurrentSite());
        if (studyComboViewer != null) {
            StudyWrapper study = shipment.getStudy();
            if (study != null) {
                studyComboViewer.setSelection(new StructuredSelection(study));
            } else if (studyComboViewer.getCombo().getItemCount() == 1)
                studyComboViewer.setSelection(new StructuredSelection(
                    studyComboViewer.getElementAt(0)));
            else
                studyComboViewer.getCombo().deselectAll();

        }
        if (destSiteComboViewer != null) {
            SiteWrapper destSite = shipment.getReceiver();
            if (destSite != null) {
                destSiteComboViewer.setSelection(new StructuredSelection(
                    destSite));
            } else if (destSiteComboViewer.getCombo().getItemCount() == 1)
                destSiteComboViewer.setSelection(new StructuredSelection(
                    destSiteComboViewer.getElementAt(0)));
            else
                destSiteComboViewer.getCombo().deselectAll();
        }
        reloadAliquots();
    }

    @Override
    protected void reloadAliquots() {
        if (aliquotsNonProcessedTable != null) {
            aliquotsNonProcessedTable.reloadCollection();
            page.layout(true, true);
            book.reflow(true);
        }
        if (aliquotsTreeTable != null) {
            aliquotsTreeTable.refresh();
        }
    }

    @Override
    protected String getTextForPartName() {
        if (shipment.isNew()) {
            return "New Dispatch Shipment";
        } else {
            return "Dispatch Shipment " + shipment.getFormattedDateReceived();
        }
    }
}
