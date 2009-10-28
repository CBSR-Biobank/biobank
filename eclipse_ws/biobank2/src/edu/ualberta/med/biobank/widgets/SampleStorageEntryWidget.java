package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.SampleStorageDialog;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageInfoTable;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
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

    private Collection<SampleTypeWrapper> allSampleTypes;

    private Collection<SampleStorageWrapper> selectedSampleStorage;

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
        SiteWrapper site,
        Collection<SampleStorageWrapper> sampleStorageCollection,
        FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        getSampleTypes(site);
        selectedSampleStorage = sampleStorageCollection;

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sampleStorageTable = new SampleStorageInfoTable(parent,
            selectedSampleStorage);
        sampleStorageTable.adaptToToolkit(toolkit, true);
        addTableMenu();
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
                    SessionManager.getAppService(), new SampleStorage()),
                    getNonDuplicateSampleTypes());
            }
        });
    }

    private void addOrEditSampleStorage(boolean add,
        SampleStorageWrapper sampleStorage,
        Set<SampleTypeWrapper> availSampleTypes) {
        SampleStorageDialog dlg = new SampleStorageDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            sampleStorage, availSampleTypes);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedSampleStorage.add(dlg.getSampleStorage());
            }
            sampleStorageTable.setCollection(selectedSampleStorage);
            notifyListeners();
        }
    }

    // need sample types that have not yet been selected in sampleStorageTable
    private Set<SampleTypeWrapper> getNonDuplicateSampleTypes() {
        Set<SampleTypeWrapper> sampleTypes = new HashSet<SampleTypeWrapper>(
            allSampleTypes);
        Set<SampleTypeWrapper> dupSampleTypes = new HashSet<SampleTypeWrapper>();

        // get the IDs of the selected sample types
        List<Integer> sampleTypeIds = new ArrayList<Integer>();
        for (SampleStorageWrapper ss : sampleStorageTable.getCollection()) {
            sampleTypeIds.add(ss.getSampleType().getId());
        }

        for (SampleTypeWrapper stype : allSampleTypes) {
            if (sampleTypeIds.contains(stype.getId())) {
                dupSampleTypes.add(stype);
            }
        }
        sampleTypes.removeAll(dupSampleTypes);
        return sampleTypes;
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        sampleStorageTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                SampleStorageWrapper sampleStorage = sampleStorageTable
                    .getSelection();

                Set<SampleTypeWrapper> allowedSampleTypes = getNonDuplicateSampleTypes();
                allowedSampleTypes.add(sampleStorage.getSampleType());
                addOrEditSampleStorage(false, sampleStorage, allowedSampleTypes);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                SampleStorageWrapper sampleStorage = sampleStorageTable
                    .getSelection();

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Sample Storage",
                    "Are you sure you want to delete sample storage \""
                        + sampleStorage.getSampleType().getName() + "\"?");

                if (confirm) {
                    Collection<SampleStorageWrapper> ssToDelete = new HashSet<SampleStorageWrapper>();
                    for (SampleStorageWrapper ss : selectedSampleStorage) {
                        if (ss.getSampleType().getName().equals(
                            sampleStorage.getSampleType().getName()))
                            ssToDelete.add(ss);
                    }

                    for (SampleStorageWrapper ss : ssToDelete) {
                        selectedSampleStorage.remove(ss);
                    }

                    sampleStorageTable.setCollection(selectedSampleStorage);
                    notifyListeners();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
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

    public List<SampleStorageWrapper> getSampleStorage() {
        return sampleStorageTable.getCollection();
    }

    public void setSampleStorage(List<SampleStorageWrapper> sampleStorage) {
        selectedSampleStorage = sampleStorage;
        sampleStorageTable.setCollection(sampleStorage);
    }

    @Override
    public boolean setFocus() {
        return addSampleStorageButton.setFocus();
    }
}
