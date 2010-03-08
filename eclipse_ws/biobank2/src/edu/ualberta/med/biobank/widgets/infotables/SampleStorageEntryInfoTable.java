package edu.ualberta.med.biobank.widgets.infotables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.SampleStorageDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class SampleStorageEntryInfoTable extends SampleStorageInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SampleStorageEntryInfoTable.class.getName());

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
    public SampleStorageEntryInfoTable(Composite parent, SiteWrapper site,
        List<SampleStorageWrapper> sampleStorageCollection) {
        super(parent, false, null);
        getSampleTypes(site);
        setSampleStorages(sampleStorageCollection);

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    public void addSampleStorage() {
        addOrEditSampleStorage(true, new SampleStorageWrapper(SessionManager
            .getAppService()), allSampleTypes);
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
            setCollection(selectedSampleStorages);
            addedOrModifiedSampleStorages.add(dlg.getSampleStorage());
            notifyListeners();
        }
    }

    private void addEditSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                addSampleStorage();
            }
        });
        addEditItemListener(new IInfoTableEditItemListener() {
            @Override
            public void editItem(InfoTableEvent event) {
                SampleStorageWrapper sampleStorage = getSelection();
                addOrEditSampleStorage(false, sampleStorage, allSampleTypes);
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                SampleStorageWrapper sampleStorage = getSelection();

                if (!MessageDialog.openConfirm(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(),
                    "Delete Aliquot Storage",
                    "Are you sure you want to delete sample storage \""
                        + sampleStorage.getSampleType().getName() + "\"?")) {
                    return;
                }

                selectedSampleStorages.remove(sampleStorage);
                setCollection(selectedSampleStorages);
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
            logger.error("getSampleTypes", e);
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
        setCollection(selectedSampleStorages);
        addedOrModifiedSampleStorages = new ArrayList<SampleStorageWrapper>();
        deletedSampleStorages = new ArrayList<SampleStorageWrapper>();
    }
}
