package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.dialogs.SampleTypeDialog;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.SampleTypeInfoTable;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class SampleTypeEntryWidget extends BiobankWidget {

    private SampleTypeInfoTable sampleTypeTable;

    private Button addSampleTypeButton;

    private List<SampleTypeWrapper> selectedSampleTypes;

    private Collection<SampleTypeWrapper> conflictTypes;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param SampleTypeCollection the sample storage already selected and to be
     *            displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public SampleTypeEntryWidget(Composite parent, int style,
        Collection<SampleTypeWrapper> sampleTypeCollection,
        Collection<SampleTypeWrapper> conflictTypes, String buttonLabel,
        FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");

        this.conflictTypes = conflictTypes;

        if (sampleTypeCollection == null) {
            selectedSampleTypes = new ArrayList<SampleTypeWrapper>();
        } else {
            selectedSampleTypes = new ArrayList<SampleTypeWrapper>(
                sampleTypeCollection);
        }
        Collections.sort(selectedSampleTypes);
        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sampleTypeTable = new SampleTypeInfoTable(parent, selectedSampleTypes);
        sampleTypeTable.adaptToToolkit(toolkit, true);
        GridData gd = (GridData) sampleTypeTable.getLayoutData();
        gd.heightHint = 230;

        addTableMenu();
        sampleTypeTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    SampleTypeEntryWidget.this.notifyListeners();
                }
            });

        addSampleTypeButton = toolkit.createButton(parent, buttonLabel,
            SWT.PUSH);
        addSampleTypeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addOrEditSampleType(true, new SampleTypeWrapper(SessionManager
                    .getAppService(), new SampleType()), getRestrictedTypes());
            }
        });
    }

    private boolean addOrEditSampleType(boolean add,
        SampleTypeWrapper sampleType, Set<SampleTypeWrapper> restrictedTypes) {
        SampleTypeDialog dlg = new SampleTypeDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), sampleType);
        if (dlg.open() == Dialog.OK) {
            if (addEditOk(sampleType, restrictedTypes)) {
                if (add) {
                    // only add to the collection when adding and not editing
                    selectedSampleTypes.add(dlg.getSampleType());
                }
                sampleTypeTable.setCollection(selectedSampleTypes);
                notifyListeners();
                return true;
            }
            BioBankPlugin.openAsyncError("Name Problem",
                "A type with the same name or short name already exists.");
        }
        return false;
    }

    // need sample types that have not yet been selected in sampleStorageTable
    private Set<SampleTypeWrapper> getRestrictedTypes() {
        Set<SampleTypeWrapper> restrictedTypes = new HashSet<SampleTypeWrapper>(
            conflictTypes);
        Collection<SampleTypeWrapper> currentSampleTypes = sampleTypeTable
            .getCollection();

        for (SampleTypeWrapper ss : currentSampleTypes) {
            restrictedTypes.add(ss);
        }
        return restrictedTypes;
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        sampleTypeTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {

                IStructuredSelection stSelection = (IStructuredSelection) sampleTypeTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                SampleTypeWrapper pvss = ((SampleTypeWrapper) item.o);
                Set<SampleTypeWrapper> restrictedTypes = getRestrictedTypes();
                restrictedTypes.remove(pvss);
                addOrEditSampleType(false, pvss, restrictedTypes);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection stSelection = (IStructuredSelection) sampleTypeTable
                    .getTableViewer().getSelection();

                BiobankCollectionModel item = (BiobankCollectionModel) stSelection
                    .getFirstElement();
                SampleTypeWrapper sampleType = (SampleTypeWrapper) item.o;

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Sample Type",
                    "Are you sure you want to delete sample type \""
                        + sampleType.getName() + "\"?");

                if (confirm) {
                    Collection<SampleTypeWrapper> stToDelete = new HashSet<SampleTypeWrapper>();
                    for (SampleTypeWrapper st : selectedSampleTypes) {
                        if (st.getName().equals(sampleType.getName()))
                            stToDelete.add(st);
                    }

                    for (SampleTypeWrapper st : stToDelete) {
                        selectedSampleTypes.remove(st);
                    }

                    sampleTypeTable.setCollection(selectedSampleTypes);
                    notifyListeners();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private boolean addEditOk(SampleTypeWrapper type,
        Set<SampleTypeWrapper> restrictedTypes) {
        for (SampleTypeWrapper st : restrictedTypes) {
            if (st.getName().equals(type.getName())
                || st.getNameShort().equals(type.getNameShort())) {
                return false;
            }
        }
        return true;
    }

    public List<SampleTypeWrapper> getTableSampleTypes() {
        return sampleTypeTable.getCollection();
    }

    @Override
    public boolean setFocus() {
        return addSampleTypeButton.setFocus();
    }
}
