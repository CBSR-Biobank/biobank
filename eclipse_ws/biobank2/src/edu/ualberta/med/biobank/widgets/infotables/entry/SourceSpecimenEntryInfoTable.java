package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Arrays;
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
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.PagedDialog.NewListener;
import edu.ualberta.med.biobank.dialogs.StudySourceSpecimenDialog;
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
        final SourceSpecimenWrapper sourceSpecimen) {
        List<SpecimenTypeWrapper> dialogSpecimenTypes = availableSpecimenTypes;
        if (!add) {
            dialogSpecimenTypes.add(sourceSpecimen.getSpecimenType());
        }
        NewListener newListener = null;
        if (add) {
            // only add to the collection when adding and not editing
            newListener = new NewListener() {
                @Override
                public void newAdded(ModelWrapper<?> spec) {
                    ((SourceSpecimenWrapper) spec).setStudy(study);
                    availableSpecimenTypes.remove(sourceSpecimen
                        .getSpecimenType());
                    selectedSourceSpecimen.add((SourceSpecimenWrapper) spec);
                    study.addToSourceSpecimenCollection((Arrays
                        .asList((SourceSpecimenWrapper) spec)));
                    reloadCollection(selectedSourceSpecimen);
                    notifyListeners();
                }
            };
        }
        StudySourceSpecimenDialog dlg = new StudySourceSpecimenDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            sourceSpecimen, newListener, dialogSpecimenTypes);

        int res = dlg.open();
        if (!add && res == Dialog.OK) {
            reloadCollection(selectedSourceSpecimen);
            notifyListeners();
        }
    }

    private void addEditSupport() {
        if (SessionManager.canCreate(SourceSpecimenWrapper.class)) {
            addAddItemListener(new IInfoTableAddItemListener() {
                @Override
                public void addItem(InfoTableEvent event) {
                    addSourceSpecimen();
                }
            });
        }
        if (SessionManager.canUpdate(SourceSpecimenWrapper.class)) {
            addEditItemListener(new IInfoTableEditItemListener() {
                @Override
                public void editItem(InfoTableEvent event) {
                    SourceSpecimenWrapper sourceSpecimen = getSelection();
                    if (sourceSpecimen != null)
                        addOrEditStudySourceSpecimen(false, sourceSpecimen);
                }
            });
        }
        if (SessionManager.canDelete(SourceSpecimenWrapper.class)) {
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
                        study.removeFromSourceSpecimenCollection(Arrays
                            .asList(sourceSpecimen));
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

    public void reload() {
        selectedSourceSpecimen = study.getSourceSpecimenCollection(true);
        if (selectedSourceSpecimen == null) {
            selectedSourceSpecimen = new ArrayList<SourceSpecimenWrapper>();
        }
        reloadCollection(selectedSourceSpecimen);
    }

    @SuppressWarnings("serial")
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
