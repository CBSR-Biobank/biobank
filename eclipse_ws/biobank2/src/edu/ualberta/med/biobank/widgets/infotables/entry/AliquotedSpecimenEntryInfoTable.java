package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.PagedDialog.NewListener;
import edu.ualberta.med.biobank.dialogs.StudyAliquotedSpecimenDialog;
import edu.ualberta.med.biobank.widgets.infotables.AliquotedSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;

/**
 * Displays the current aliquoted specimen collection and allows the user to add
 * additional aliquoted specimen to the collection.
 */
public class AliquotedSpecimenEntryInfoTable extends AliquotedSpecimenInfoTable {

    private List<AliquotedSpecimenWrapper> selectedAliquotedSpecimen;

    private List<AliquotedSpecimenWrapper> addedOrModifiedAliquotedSpecimen;

    private List<AliquotedSpecimenWrapper> deletedAliquotedSpecimen;

    private StudyWrapper study;

    public AliquotedSpecimenEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
        this.study = study;
        selectedAliquotedSpecimen = study.getAliquotedSpecimenCollection(true);
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
        AliquotedSpecimenWrapper asw = new AliquotedSpecimenWrapper(
            SessionManager.getAppService());
        asw.setStudy(study);
        addOrEditAliquotedSpecimen(true, asw);
    }

    private void addOrEditAliquotedSpecimen(boolean add,
        final AliquotedSpecimenWrapper aliquotedSpecimen) {
        final Collection<SpecimenTypeWrapper> availableSpecimenTypes = getAvailableSpecimenTypes();
        if (!add) {
            availableSpecimenTypes.add(aliquotedSpecimen.getSpecimenType());
        }
        NewListener newListener = null;
        if (add) {
            // only add to the collection when adding and not editing
            newListener = new NewListener() {
                @Override
                public void newAdded(ModelWrapper<?> spec) {
                    ((AliquotedSpecimenWrapper) spec).setStudy(study);
                    availableSpecimenTypes.remove(aliquotedSpecimen
                        .getSpecimenType());
                    selectedAliquotedSpecimen
                        .add((AliquotedSpecimenWrapper) spec);
                    addedOrModifiedAliquotedSpecimen
                        .add((AliquotedSpecimenWrapper) spec);
                    reloadCollection(selectedAliquotedSpecimen);
                    notifyListeners();
                }
            };
        }
        StudyAliquotedSpecimenDialog dlg = new StudyAliquotedSpecimenDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            aliquotedSpecimen, newListener, availableSpecimenTypes);

        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(selectedAliquotedSpecimen);
            notifyListeners();
        }
    }

    private Collection<SpecimenTypeWrapper> getAvailableSpecimenTypes() {
        Set<SpecimenTypeWrapper> availableSpecimenTypes = new HashSet<SpecimenTypeWrapper>();
        for (SourceSpecimenWrapper ssw : study
            .getSourceSpecimenCollection(false)) {
            availableSpecimenTypes.addAll(ssw.getSpecimenType()
                .getChildSpecimenTypeCollection(false));
        }
        return availableSpecimenTypes;
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(AliquotedSpecimenWrapper.class)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addAliquotedSpecimen();
                }
            });
        }
        if (SessionManager.canUpdate(AliquotedSpecimenWrapper.class)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    AliquotedSpecimenWrapper aliquotedSpecimen = getSelection();
                    if (aliquotedSpecimen != null)
                        addOrEditAliquotedSpecimen(false, aliquotedSpecimen);
                }
            });
        }
        if (SessionManager.canDelete(AliquotedSpecimenWrapper.class)) {
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

    public List<AliquotedSpecimenWrapper> getAddedOrModifiedAliquotedSpecimens() {
        return addedOrModifiedAliquotedSpecimen;
    }

    public List<AliquotedSpecimenWrapper> getDeletedAliquotedSpecimens() {
        return deletedAliquotedSpecimen;
    }

    public void reload() {
        selectedAliquotedSpecimen = study.getAliquotedSpecimenCollection(true);
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
            private static final long serialVersionUID = 1L;

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
