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
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.dispatch.DispatchCreateScanDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.BasicSiteCombo;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DispatchAliquotsTreeTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class DispatchSendingEntryForm extends AbstractShipmentEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(DispatchSendingEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.DispatchSendingEntryForm";

    public static final String MSG_NEW_DISPATCH_OK = "Creating a new dispatch record.";

    public static final String MSG_DISPATCH_OK = "Editing an existing dispatch record.";

    private ComboViewer studyComboViewer;

    private ComboViewer destSiteComboViewer;

    protected DispatchAliquotListInfoTable aliquotsNonProcessedTable;

    private DispatchAliquotsTreeTable aliquotsTreeTable;

    private BasicSiteCombo siteCombo;

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        // if the shipment is new, and if the combos hold only one element,
        // there will be default selections but dirty will be set to false by
        // default anyway
        if (dispatch.isNew()) {
            setDirty(true);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Dispatch Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteCombo = createBasicSiteCombo(client, true,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    SiteWrapper currentSite = siteCombo.getSelectedSite();
                    dispatch.setSender(currentSite);
                }
            });
        setFirstControl(siteCombo);

        createReceiverCombo(client);

        siteCombo.setSelectedSite(dispatch.getSender(), true);

        if (!dispatch.isNew() && !dispatch.isInCreationState()) {
            ShippingMethodWrapper selectedShippingMethod = dispatch
                .getShippingMethod();
            widgetCreator.createComboViewer(client, "Shipping Method",
                ShippingMethodWrapper.getShippingMethods(SessionManager
                    .getAppService()), selectedShippingMethod, null,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        dispatch
                            .setShippingMethod((ShippingMethodWrapper) selectedObject);
                    }
                });

            createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
                "Waybill", null, dispatch, "waybill", null);
        }

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null,
            BeansObservables.observeValue(dispatch, "comment"), null);

        createAliquotsSelectionSection();

    }

    private void createReceiverCombo(Composite client) {
        if (dispatch.isInTransitState()) {
            BiobankText receiverLabel = createReadOnlyLabelledField(client,
                SWT.NONE, "Receiver Site");
            setTextValue(receiverLabel, dispatch.getReceiver().getNameShort());
        } else {
            destSiteComboViewer = createComboViewer(client, "Receiver Site",
                null, null, "Dispatch must have an associated study",
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        dispatch.setReceiver((SiteWrapper) selectedObject);
                        setDirty(true);
                    }
                });
        }
    }

    private void createAliquotsSelectionSection() {
        if (dispatch.isInCreationState()) {
            Section section = createSection("Aliquot added");
            Composite composite = toolkit.createComposite(section);
            composite.setLayout(new GridLayout(1, false));
            section.setClient(composite);
            if (dispatch.isInCreationState()) {
                addSectionToolbar(section, "Add aliquots to this dispatch",
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
            aliquotsTreeTable = new DispatchAliquotsTreeTable(page, dispatch,
                !dispatch.isInClosedState() && !dispatch.isInLostState(), true);
            aliquotsTreeTable.addSelectionChangedListener(biobankListener);
        }
    }

    protected void createAliquotsNonProcessedSection(boolean edit) {
        String title = "Non processed aliquots";
        if (dispatch.isInCreationState()) {
            title = "Added aliquots";
        }
        Composite parent = createSectionWithClient(title);
        aliquotsNonProcessedTable = new DispatchAliquotListInfoTable(parent,
            dispatch, edit) {
            @Override
            public List<DispatchAliquotWrapper> getInternalDispatchAliquots() {
                return dispatch.getNonProcessedDispatchAliquotCollection();
            }

        };
        aliquotsNonProcessedTable.adaptToToolkit(toolkit, true);
        aliquotsNonProcessedTable.addClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                if (selection instanceof InfoTableSelection) {
                    InfoTableSelection tableSelection = (InfoTableSelection) selection;
                    DispatchAliquotWrapper dsa = (DispatchAliquotWrapper) tableSelection
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
            dispatch, siteCombo.getSelectedSite());
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
                    dispatch.getAppService(), inventoryId,
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
        List<AliquotWrapper> aliquots = dispatch.getAliquotCollection();
        if (aliquots != null && aliquots.contains(aliquot)) {
            BioBankPlugin.openAsyncError("Error",
                "Aliquot " + aliquot.getInventoryId()
                    + " has already been added to this dispatch");
            return;
        }
        try {
            dispatch.addNewAliquots(Arrays.asList(aliquot));
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error adding aliquots", e);
        }
        reloadAliquots();
    }

    @Override
    protected String getOkMessage() {
        return (dispatch.isNew()) ? MSG_NEW_DISPATCH_OK : MSG_DISPATCH_OK;
    }

    @Override
    public String getNextOpenedFormID() {
        return DispatchViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        dispatch.setSender(siteCombo.getSelectedSite());
        if (destSiteComboViewer != null) {
            CenterWrapper<?> destSite = dispatch.getReceiver();
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
        if (dispatch.isNew()) {
            return "New Dispatch";
        } else {
            return "Dispatch " + dispatch.getFormattedDateReceived();
        }
    }
}
