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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SourceSpecimenDialog;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SourceSpecimenInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SourceSpecimenEntryInfoTable extends SourceSpecimenInfoTable {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SourceSpecimenEntryInfoTable.class.getName());

    private List<SpecimenTypeWrapper> availableSpecimenTypes;

    private List<SourceSpecimenWrapper> selectedSourceSpecimen;

    private List<SourceSpecimenWrapper> addedOrModifiedSourceSpecimen;

    private List<SourceSpecimenWrapper> deletedSourceSpecimen;

    private StudyWrapper study;

    public SourceSpecimenEntryInfoTable(Composite parent,
        List<SourceSpecimenWrapper> collection) {
        super(parent, collection);
    }

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
    public SourceSpecimenEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
        this.study = study;
        initSpecimenTypes();
        selectedSourceSpecimen = study.getSourceSpecimenCollection(true);
        if (selectedSourceSpecimen == null) {
            selectedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        }
        setCollection(selectedSourceSpecimen);
        addedOrModifiedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        deletedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addEditSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addSourceSpecimen() {
        SourceSpecimenWrapper newSourceSpecimen = new SourceSpecimenWrapper(
            SessionManager.getAppService());
        newSourceSpecimen.setStudy(study);
        addOrEditStudySourceSpecimen(true, newSourceSpecimen);
    }

    private void addOrEditStudySourceSpecimen(boolean add,
        SourceSpecimenWrapper sourceSpecimen) {
        List<SpecimenTypeWrapper> dialogSpecimenTypes = availableSpecimenTypes;
        if (!add) {
            dialogSpecimenTypes.add(sourceSpecimen.getSpecimenType());
        }
        // FIXME: create new dialog type
        SourceSpecimenDialog dlg = new SourceSpecimenDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            sourceSpecimen, dialogSpecimenTypes);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedSourceSpecimen.add(sourceSpecimen);
            }
            availableSpecimenTypes.remove(sourceSpecimen.getSpecimenType());
            reloadCollection(selectedSourceSpecimen);
            addedOrModifiedSourceSpecimen.add(sourceSpecimen);
            notifyListeners();
        }
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(SourceSpecimenWrapper.class, null)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addSourceSpecimen();
                }
            });
        }
        if (SessionManager.canUpdate(SourceSpecimenWrapper.class, null)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    SourceSpecimenWrapper sourceSpecimen = getSelection();
                    if (sourceSpecimen != null)
                        addOrEditStudySourceSpecimen(false, sourceSpecimen);
                }
            });
        }
        if (SessionManager.canDelete(SourceSpecimenWrapper.class, null)) {
            addDeleteItemListener(new IInfoTableDeleteItemListener() {
                @Override
                public void deleteItem(InfoTableEvent event) {
                    SourceSpecimenWrapper sourceSpecimen = getSelection();
                    if (sourceSpecimen != null) {
                        if (!MessageDialog
                            .openConfirm(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getShell(),
                                Messages
                                    .getString("SourceSpecimenEntryInfoTable.delete.title"),
                                Messages
                                    .getString("SourceSpecimenEntryInfoTable.delete.question"))) {
                            return;
                        }

                        selectedSourceSpecimen.remove(sourceSpecimen);
                        setCollection(selectedSourceSpecimen);
                        deletedSourceSpecimen.add(sourceSpecimen);
                        availableSpecimenTypes.add(sourceSpecimen
                            .getSpecimenType());
                        notifyListeners();
                    }
                }
            });
        }
    }

    private void initSpecimenTypes() {
        try {
            availableSpecimenTypes = SpecimenTypeWrapper.getAllSpecimenTypes(
                SessionManager.getAppService(), false);
            List<SourceSpecimenWrapper> sourceSpecimen = study
                .getSourceSpecimenCollection(false);
            if (sourceSpecimen != null) {
                for (SourceSpecimenWrapper ssw : sourceSpecimen) {
                    availableSpecimenTypes.remove(ssw.getSpecimenType());
                }
            }
        } catch (final RemoteConnectFailureException exp) {
            BiobankPlugin.openRemoteConnectErrorMessage(exp);
        } catch (ApplicationException e) {
            logger.error("initSpecimenTypes", e);
        }
    }

    public List<SourceSpecimenWrapper> getAddedOrModifiedSourceSpecimens() {
        return addedOrModifiedSourceSpecimen;
    }

    public List<SourceSpecimenWrapper> getDeletedSourceSpecimens() {
        return deletedSourceSpecimen;
    }

    public void reload() {
        selectedSourceSpecimen = study.getSourceSpecimenCollection(true);
        if (selectedSourceSpecimen == null) {
            selectedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        }
        reloadCollection(selectedSourceSpecimen);
        addedOrModifiedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        deletedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = getCollectionModelObject((SourceSpecimenWrapper) e1);
                    TableRowData i2 = getCollectionModelObject((SourceSpecimenWrapper) e2);
                    return super.compare(i1.name, i2.name);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

}
