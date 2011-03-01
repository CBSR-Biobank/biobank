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
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.AliquotedSpecimenDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.AliquotedSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current aliquoted specimen collection and allows the user to add
 * additional aliquoted specimen to the collection.
 */
public class AliquotedSpecimenEntryInfoTable extends AliquotedSpecimenInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AliquotedSpecimenEntryInfoTable.class.getName());

    private List<SpecimenTypeWrapper> allSpecimenTypes;

    private List<AliquotedSpecimenWrapper> selectedAliquotedSpecimen;

    private List<AliquotedSpecimenWrapper> addedOrModifiedAliquotedSpecimen;

    private List<AliquotedSpecimenWrapper> deletedAliquotedSpecimen;

    private StudyWrapper study;

    public AliquotedSpecimenEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
        getSpecimenTypes();
        this.study = study;
        selectedAliquotedSpecimen = study.getAliquotedSpecimenCollection();
        if (selectedAliquotedSpecimen == null) {
            selectedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();
        }
        setCollection(selectedAliquotedSpecimen);
        addedOrModifiedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();
        deletedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addAliquotedSpecimen() {
        addOrEditAliquotedSpecimen(true, new AliquotedSpecimenWrapper(
            SessionManager.getAppService()));
    }

    private void addOrEditAliquotedSpecimen(boolean add,
        AliquotedSpecimenWrapper aliquotedSpecimen) {
        List<SpecimenTypeWrapper> availableSpecimenTypes = new ArrayList<SpecimenTypeWrapper>();
        availableSpecimenTypes.addAll(allSpecimenTypes);
        for (AliquotedSpecimenWrapper ssw : selectedAliquotedSpecimen) {
            if (add || !ssw.equals(aliquotedSpecimen)) {
                availableSpecimenTypes.remove(ssw.getSpecimenType());
            }
        }
        AliquotedSpecimenDialog dlg = new AliquotedSpecimenDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            aliquotedSpecimen, availableSpecimenTypes);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedAliquotedSpecimen.add(aliquotedSpecimen);
            }
            reloadCollection(selectedAliquotedSpecimen);
            addedOrModifiedAliquotedSpecimen.add(aliquotedSpecimen);
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
                    AliquotedSpecimenWrapper aliquotedSpecimen = getSelection();
                    if (aliquotedSpecimen != null)
                        addOrEditAliquotedSpecimen(false, aliquotedSpecimen);
                }
            });
        }
        if (SessionManager.canDelete(AliquotedSpecimenWrapper.class, null)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    AliquotedSpecimenWrapper aliquotedSpecimen = getSelection();
                    if (aliquotedSpecimen != null) {
                        if (!MessageDialog
                            .openConfirm(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getShell(),
                                Messages
                                    .getString("AliquotedSpecimenEntryInfoTable.delete.title"),
                                Messages
                                    .getString(
                                        "AliquotedSpecimenEntryInfoTable.delete.question",
                                        aliquotedSpecimen.getSpecimenType()
                                            .getName()))) {
                            return;
                        }

                        selectedAliquotedSpecimen.remove(aliquotedSpecimen);
                        setCollection(selectedAliquotedSpecimen);
                        deletedAliquotedSpecimen.add(aliquotedSpecimen);
                        notifyListeners();
                    }
                }
            });
        }
    }

    private void getSpecimenTypes() {
        try {
            allSpecimenTypes = SpecimenTypeWrapper.getAllSpecimenTypes(
                SessionManager.getAppService(), true);
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (ApplicationException e) {
            logger.error("getSpecimenTypes", e);
        }
    }

    public List<AliquotedSpecimenWrapper> getAddedOrModifiedAliquotedSpecimens() {
        return addedOrModifiedAliquotedSpecimen;
    }

    public List<AliquotedSpecimenWrapper> getDeletedAliquotedSpecimens() {
        return deletedAliquotedSpecimen;
    }

    public void reload() {
        selectedAliquotedSpecimen = study.getAliquotedSpecimenCollection();
        if (selectedAliquotedSpecimen == null) {
            selectedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();
        }
        reloadCollection(selectedAliquotedSpecimen);
        addedOrModifiedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();
        deletedAliquotedSpecimen = new ArrayList<AliquotedSpecimenWrapper>();
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
