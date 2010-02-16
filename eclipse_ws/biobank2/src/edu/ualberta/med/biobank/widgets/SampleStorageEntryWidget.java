package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.SampleStorageDialog;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class SampleStorageEntryWidget extends BiobankWidget {

    private static Logger LOGGER = Logger
        .getLogger(SampleStorageEntryWidget.class.getName());

    private SampleStorageInfoTable sampleStorageTable;

    private Button addSampleStorageButton;

    private List<SampleTypeWrapper> allSampleTypes;

    private List<SampleStorageWrapper> selectedSampleStorages;

    private List<SampleStorageWrapper> addedOrModifiedSampleStorages;

    private List<SampleStorageWrapper> deletedSampleStorages;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param sampleStorageCollection the sample storage already selected and to
     *            be displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public SampleStorageEntryWidget(Composite parent, int style,
        SiteWrapper site, List<SampleStorageWrapper> sampleStorageCollection,
        FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");

        getSampleTypes(site);
        setSampleStorages(sampleStorageCollection);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sampleStorageTable = new SampleStorageInfoTable(parent,
            selectedSampleStorages);
        sampleStorageTable.adaptToToolkit(toolkit, true);
        addEditSupport();
        sampleStorageTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    // SampleStorageEntryWidget.this.notifyListeners();
                }
            });

        addSampleStorageButton = toolkit.createButton(parent,
            "Add Sample Storage", SWT.PUSH);
        addSampleStorageButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addOrEditSampleStorage(true, new SampleStorageWrapper(
                    SessionManager.getAppService()), allSampleTypes);
            }
        });
    }

    private void addOrEditSampleStorage(boolean add,
        SampleStorageWrapper sampleStorage,
        List<SampleTypeWrapper> availSampleTypes) {
        SampleStorageDialog dlg = new SampleStorageDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            sampleStorage, availSampleTypes);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedSampleStorages.add(dlg.getSampleStorage());
            }
            sampleStorageTable.setCollection(selectedSampleStorages);
            addedOrModifiedSampleStorages.add(dlg.getSampleStorage());
            notifyListeners();
        }
    }

    private void addEditSupport() {
        sampleStorageTable
            .addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    SampleStorageWrapper sampleStorage = sampleStorageTable
                        .getSelection();
                    addOrEditSampleStorage(false, sampleStorage, allSampleTypes);
                }
            });

        sampleStorageTable
            .addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    SampleStorageWrapper sampleStorage = sampleStorageTable
                        .getSelection();

                    if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        "Delete Sample Storage",
                        "Are you sure you want to delete sample storage \""
                            + sampleStorage.getSampleType().getName() + "\"?")) {
                        return;
                    }

                    selectedSampleStorages.remove(sampleStorage);
                    sampleStorageTable.setCollection(selectedSampleStorages);
                    deletedSampleStorages.add(sampleStorage);
                    notifyListeners();
                }
            });
    }

    private void getSampleTypes(SiteWrapper site) {
        try {
            allSampleTypes = site.getAllSampleTypeCollection();
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (ApplicationException e) {
            LOGGER.error("getSampleTypes", e);
        }
    }

    public List<SampleStorageWrapper> getAddedOrModifiedSampleStorages() {
        return addedOrModifiedSampleStorages;
    }

    public List<SampleStorageWrapper> getDeletedSampleStorages() {
        return deletedSampleStorages;
    }

    public void setSampleStorages(List<SampleStorageWrapper> sampleStorages) {
        if (sampleStorages == null) {
            selectedSampleStorages = new ArrayList<SampleStorageWrapper>();
        } else {
            selectedSampleStorages = sampleStorages;
        }
        if (sampleStorageTable != null) {
            sampleStorageTable.setCollection(selectedSampleStorages);
        }
        addedOrModifiedSampleStorages = new ArrayList<SampleStorageWrapper>();
        deletedSampleStorages = new ArrayList<SampleStorageWrapper>();
    }

    @Override
    public boolean setFocus() {
        return addSampleStorageButton.setFocus();
    }
}
