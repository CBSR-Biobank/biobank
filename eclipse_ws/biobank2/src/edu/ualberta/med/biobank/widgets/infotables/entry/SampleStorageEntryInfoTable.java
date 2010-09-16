package edu.ualberta.med.biobank.widgets.infotables.entry;

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
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SampleStorageDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageInfoTable;
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

    private StudyWrapper study;

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
    public SampleStorageEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
        getSampleTypes();
        this.study = study;
        selectedSampleStorages = study.getSampleStorageCollection();
        if (selectedSampleStorages == null) {
            selectedSampleStorages = new ArrayList<SampleStorageWrapper>();
        }
        setCollection(selectedSampleStorages);
        addedOrModifiedSampleStorages = new ArrayList<SampleStorageWrapper>();
        deletedSampleStorages = new ArrayList<SampleStorageWrapper>();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addSampleStorage() {
        addOrEditSampleStorage(true,
            new SampleStorageWrapper(SessionManager.getAppService()));
    }

    private void addOrEditSampleStorage(boolean add,
        SampleStorageWrapper sampleStorage) {
        List<SampleTypeWrapper> availableSampleTypes = new ArrayList<SampleTypeWrapper>();
        availableSampleTypes.addAll(allSampleTypes);
        for (SampleStorageWrapper ssw : selectedSampleStorages) {
            if (add || !ssw.equals(sampleStorage)) {
                availableSampleTypes.remove(ssw.getSampleType());
            }
        }
        SampleStorageDialog dlg = new SampleStorageDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            sampleStorage, availableSampleTypes);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedSampleStorages.add(sampleStorage);
            }
            reloadCollection(selectedSampleStorages);
            addedOrModifiedSampleStorages.add(sampleStorage);
            notifyListeners();
        }
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(SampleStorageWrapper.class)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addSampleStorage();
                }
            });
        }
        if (SessionManager.canUpdate(SampleStorageWrapper.class)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    SampleStorageWrapper sampleStorage = getSelection();
                    if (sampleStorage != null)
                        addOrEditSampleStorage(false, sampleStorage);
                }
            });
        }
        if (SessionManager.canDelete(SampleStorageWrapper.class)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    SampleStorageWrapper sampleStorage = getSelection();
                    if (sampleStorage != null) {
                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Aliquot Storage",
                            "Are you sure you want to delete sample storage \""
                                + sampleStorage.getSampleType().getName()
                                + "\"?")) {
                            return;
                        }

                        selectedSampleStorages.remove(sampleStorage);
                        setCollection(selectedSampleStorages);
                        deletedSampleStorages.add(sampleStorage);
                        notifyListeners();
                    }
                }
            });
        }
    }

    private void getSampleTypes() {
        try {
            allSampleTypes = SampleTypeWrapper.getAllSampleTypes(
                SessionManager.getAppService(), true);
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage(exp);
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

    public void reload() {
        selectedSampleStorages = study.getSampleStorageCollection();
        if (selectedSampleStorages == null) {
            selectedSampleStorages = new ArrayList<SampleStorageWrapper>();
        }
        reloadCollection(selectedSampleStorages);
        addedOrModifiedSampleStorages = new ArrayList<SampleStorageWrapper>();
        deletedSampleStorages = new ArrayList<SampleStorageWrapper>();
    }

    @Override
    public BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((SampleStorageWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((SampleStorageWrapper) e2);
                    return super.compare(i1.typeName, i2.typeName);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
