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
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SampleStorageDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.AliquotedSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class AliquotedSpecimenEntryInfoTable extends AliquotedSpecimenInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AliquotedSpecimenEntryInfoTable.class.getName());

    private List<SpecimenTypeWrapper> allSampleTypes;

    private List<AliquotedSpecimenWrapper> selectedSampleStorages;

    private List<AliquotedSpecimenWrapper> addedOrModifiedSampleStorages;

    private List<AliquotedSpecimenWrapper> deletedSampleStorages;

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
    public AliquotedSpecimenEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
        getSpecimenTypes();
        this.study = study;
        selectedSampleStorages = study.getAliquotedSpecimenCollection();
        if (selectedSampleStorages == null) {
            selectedSampleStorages = new ArrayList<AliquotedSpecimenWrapper>();
        }
        setCollection(selectedSampleStorages);
        addedOrModifiedSampleStorages = new ArrayList<AliquotedSpecimenWrapper>();
        deletedSampleStorages = new ArrayList<AliquotedSpecimenWrapper>();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addAliquotedSpecimen() {
        addOrEditSampleStorage(true, new AliquotedSpecimenWrapper(
            SessionManager.getAppService()));
    }

    private void addOrEditSampleStorage(boolean add,
        AliquotedSpecimenWrapper sampleStorage) {
        List<SpecimenTypeWrapper> availableSampleTypes = new ArrayList<SpecimenTypeWrapper>();
        availableSampleTypes.addAll(allSampleTypes);
        for (AliquotedSpecimenWrapper ssw : selectedSampleStorages) {
            if (add || !ssw.equals(sampleStorage)) {
                availableSampleTypes.remove(ssw.getSpecimenType());
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
        if (SessionManager.canCreate(AliquotedSpecimenWrapper.class, null)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addAliquotedSpecimen();
                }
            });
        }
        if (SessionManager.canUpdate(AliquotedSpecimenWrapper.class, null)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    AliquotedSpecimenWrapper sampleStorage = getSelection();
                    if (sampleStorage != null)
                        addOrEditSampleStorage(false, sampleStorage);
                }
            });
        }
        if (SessionManager.canDelete(AliquotedSpecimenWrapper.class, null)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    AliquotedSpecimenWrapper sampleStorage = getSelection();
                    if (sampleStorage != null) {
                        if (!MessageDialog.openConfirm(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow()
                            .getShell(), "Delete Aliquot Storage",
                            "Are you sure you want to delete sample storage \""
                                + sampleStorage.getSpecimenType().getName()
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

    private void getSpecimenTypes() {
        try {
            allSampleTypes = SpecimenTypeWrapper.getAllSpecimenTypes(
                SessionManager.getAppService(), true);
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (ApplicationException e) {
            logger.error("getSpecimenTypes", e);
        }
    }

    public List<AliquotedSpecimenWrapper> getAddedOrModifiedAliquotedSpecimens() {
        return addedOrModifiedSampleStorages;
    }

    public List<AliquotedSpecimenWrapper> getDeletedAliquotedSpecimens() {
        return deletedSampleStorages;
    }

    public void reload() {
        selectedSampleStorages = study.getAliquotedSpecimenCollection();
        if (selectedSampleStorages == null) {
            selectedSampleStorages = new ArrayList<AliquotedSpecimenWrapper>();
        }
        reloadCollection(selectedSampleStorages);
        addedOrModifiedSampleStorages = new ArrayList<AliquotedSpecimenWrapper>();
        deletedSampleStorages = new ArrayList<AliquotedSpecimenWrapper>();
    }

    @Override
    public BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((AliquotedSpecimenWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((AliquotedSpecimenWrapper) e2);
                    return super.compare(i1.typeName, i2.typeName);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
