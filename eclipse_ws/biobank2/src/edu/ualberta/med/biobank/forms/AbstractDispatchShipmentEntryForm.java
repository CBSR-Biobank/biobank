package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchShipmentAdapter;
import edu.ualberta.med.biobank.views.DispatchShipmentAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.DispatchAliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public abstract class AbstractDispatchShipmentEntryForm extends
    BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AbstractDispatchShipmentEntryForm.class.getName());

    protected SiteWrapper site;

    protected DispatchShipmentWrapper shipment;

    protected DispatchAliquotListInfoTable aliquotsNonProcessedTable;

    protected DispatchAliquotListInfoTable aliquotsReceivedTable;

    protected DispatchAliquotListInfoTable aliquotsExtraTable;

    protected DispatchAliquotListInfoTable aliquotsMissingTable;

    protected BiobankEntryFormWidgetListener biobankTableListener =
        new BiobankEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                reloadAliquotsTables();
                setDirty(true);
            }
        };

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof DispatchShipmentAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        shipment = (DispatchShipmentWrapper) adapter.getModelObject();
        site = SessionManager.getInstance().getCurrentSite();
        if (shipment.isNew()) {
            shipment.setSender(site);
        }
        retrieveShipment();

        setPartName(getTextForPartName());
    }

    private void retrieveShipment() {
        try {
            shipment.reload();
        } catch (Exception ex) {
            logger.error(
                "Error while retrieving shipment " + shipment.getWaybill(), ex);
        }
    }

    protected abstract String getTextForPartName();

    protected void createAliquotsNonProcessedSection(boolean edit) {
        Composite parent = createSectionWithClient("Non processed aliquots");
        aliquotsNonProcessedTable =
            new DispatchAliquotListInfoTable(parent, shipment, edit) {
                @Override
                public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                    return shipment
                        .getNonProcessedDispatchShipmentAliquotCollection();
                }

            };
        aliquotsNonProcessedTable.adaptToToolkit(toolkit, true);
        aliquotsNonProcessedTable
            .addDoubleClickListener(collectionDoubleClickListener);
        aliquotsNonProcessedTable
            .addSelectionChangedListener(biobankTableListener);
    }

    protected void createAliquotsReceivedSection(boolean edit) {
        if (!shipment.isInCreationState()) {
            Composite parent = createSectionWithClient("Aliquots received");
            aliquotsReceivedTable =
                new DispatchAliquotListInfoTable(parent, shipment, edit) {
                    @Override
                    public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                        return shipment.getReceivedDispatchShipmentAliquots();
                    }
                };
            aliquotsReceivedTable.adaptToToolkit(toolkit, true);
            aliquotsReceivedTable
                .addDoubleClickListener(collectionDoubleClickListener);
            aliquotsReceivedTable
                .addSelectionChangedListener(biobankTableListener);
        }
    }

    protected void createAliquotsExtraSection(boolean edit) {
        if (!shipment.isInCreationState()) {
            Composite parent = createSectionWithClient("Extra Aliquots");
            aliquotsExtraTable =
                new DispatchAliquotListInfoTable(parent, shipment, edit) {
                    @Override
                    public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                        return shipment.getExtraDispatchShipmentAliquots();
                    }

                };
            aliquotsExtraTable.adaptToToolkit(toolkit, true);
            aliquotsExtraTable
                .addDoubleClickListener(collectionDoubleClickListener);
            aliquotsExtraTable
                .addSelectionChangedListener(biobankTableListener);
        }
    }

    protected void createAliquotsMissingSection(boolean edit) {
        if (!shipment.isInCreationState()) {
            Composite parent = createSectionWithClient("Missing Aliquots");
            aliquotsMissingTable =
                new DispatchAliquotListInfoTable(parent, shipment, edit) {
                    @Override
                    public List<DispatchShipmentAliquotWrapper> getInternalDispatchShipmentAliquots() {
                        return shipment.getMissingDispatchShipmentAliquots();
                    }

                };
            aliquotsMissingTable.adaptToToolkit(toolkit, true);
            aliquotsMissingTable
                .addDoubleClickListener(collectionDoubleClickListener);
            aliquotsExtraTable
                .addSelectionChangedListener(biobankTableListener);
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        reloadAliquotsTables();
    }

    protected void reloadAliquotsTables() {
        if (aliquotsNonProcessedTable != null)
            aliquotsNonProcessedTable.reloadCollection();
        if (aliquotsReceivedTable != null)
            aliquotsReceivedTable.reloadCollection();
        if (aliquotsExtraTable != null)
            aliquotsExtraTable.reloadCollection();
        if (aliquotsMissingTable != null)
            aliquotsMissingTable.reloadCollection();
        page.layout(true, true);
        book.reflow(true);
    }

    protected void createAliquotsSelectionActions(Composite composite,
        boolean setAsFirstControl) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter inventory ID to add:");
        final BiobankText newAliquotText =
            new BiobankText(addComposite, SWT.NONE, toolkit);
        newAliquotText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                doAliquotTextAction(newAliquotText.getText());
                newAliquotText.setFocus();
                newAliquotText.setText("");
            }
        });
        if (setAsFirstControl) {
            setFirstControl(newAliquotText);
        }
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
        addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doAliquotTextAction(newAliquotText.getText());
                newAliquotText.setFocus();
                newAliquotText.setText("");
            }
        });
        toolkit.createLabel(addComposite, "or open scan dialog:");
        Button openScanButton =
            toolkit.createButton(addComposite, "", SWT.PUSH);
        openScanButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT));
        openScanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openScanDialog();
            }
        });
    }

    protected abstract void openScanDialog();

    protected abstract void doAliquotTextAction(String text);

    @Override
    protected void saveForm() throws Exception {
        shipment.persist();
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                DispatchShipmentAdministrationView.getCurrent().reload();
            }
        });
    }

}
