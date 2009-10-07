package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.dialogs.PvSampleSourceDialog;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.widgets.infotables.PvSampleSourceInfoTable;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
public class PvSampleSourceEntryWidget extends BiobankWidget {

    private PvSampleSourceInfoTable pvSampleSourceTable;

    private Button addPvSampleSourceButton;

    private List<SampleSourceWrapper> allSampleSources;

    private List<PvSampleSourceWrapper> selectedPvSampleSources;

    private PatientVisitWrapper patientVisit;

    /**
     * 
     * @param parent a composite control which will be the parent of the new
     *            instance (cannot be null)
     * @param style the style of control to construct
     * @param pvSampleSourceCollection the sample storage already selected and
     *            to be displayed in the table viewer (can be null).
     * @param toolkit The toolkit is responsible for creating SWT controls
     *            adapted to work in Eclipse forms. If widget is not used in a
     *            form this parameter should be null.
     */
    public PvSampleSourceEntryWidget(Composite parent, int style,
        List<PvSampleSourceWrapper> pvSampleSourceCollection,
        PatientVisitWrapper patientVisit, FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        getSampleSources();
        this.patientVisit = patientVisit;
        if (pvSampleSourceCollection == null) {
            selectedPvSampleSources = new ArrayList<PvSampleSourceWrapper>();
        } else {
            selectedPvSampleSources = pvSampleSourceCollection;
        }

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        pvSampleSourceTable = new PvSampleSourceInfoTable(parent,
            selectedPvSampleSources);
        pvSampleSourceTable.adaptToToolkit(toolkit, true);
        addTableMenu();
        pvSampleSourceTable
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    PvSampleSourceEntryWidget.this.notifyListeners();
                }
            });

        addPvSampleSourceButton = toolkit.createButton(parent,
            "Add Sample Source", SWT.PUSH);
        addPvSampleSourceButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PvSampleSourceWrapper sampleSource = new PvSampleSourceWrapper(
                    SessionManager.getAppService());
                sampleSource
                    .setPatientVisit(PvSampleSourceEntryWidget.this.patientVisit);
                addOrEditPvSampleSource(true, sampleSource,
                    getNonDuplicateSampleSources());
            }
        });
    }

    private void addOrEditPvSampleSource(boolean add,
        PvSampleSourceWrapper pvSampleSource,
        Set<SampleSourceWrapper> availSampleSources) {
        PvSampleSourceDialog dlg = new PvSampleSourceDialog(PlatformUI
            .getWorkbench().getActiveWorkbenchWindow().getShell(),
            pvSampleSource, availSampleSources);
        if (dlg.open() == Dialog.OK) {
            if (add) {
                // only add to the collection when adding and not editing
                selectedPvSampleSources.add(dlg.getPvSampleSource());
            }
            pvSampleSourceTable.setCollection(selectedPvSampleSources);
            notifyListeners();
        }
    }

    // need sample types that have not yet been selected in sampleStorageTable
    private Set<SampleSourceWrapper> getNonDuplicateSampleSources() {
        Set<SampleSourceWrapper> nonDupSampleSources = new HashSet<SampleSourceWrapper>();
        Collection<PvSampleSourceWrapper> currentSampleSources = pvSampleSourceTable
            .getCollection();
        for (SampleSourceWrapper ss : allSampleSources) {
            if (!currentSampleSources.contains(ss)) {
                nonDupSampleSources.add(ss);
            }
        }
        return nonDupSampleSources;
    }

    private void addTableMenu() {
        Menu menu = new Menu(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.NONE);
        pvSampleSourceTable.getTableViewer().getTable().setMenu(menu);

        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Edit");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                PvSampleSourceWrapper pvss = pvSampleSourceTable.getSelection();
                Set<SampleSourceWrapper> allowedSampleSources = getNonDuplicateSampleSources();
                allowedSampleSources.add(new SampleSourceWrapper(SessionManager
                    .getAppService(), pvss.getSampleSource()));
                addOrEditPvSampleSource(false, pvss, allowedSampleSources);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Delete");
        item.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                PvSampleSourceWrapper pvss = pvSampleSourceTable.getSelection();

                boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Delete Sample Storage",
                    "Are you sure you want to delete sample source \""
                        + pvss.getSampleSource().getName() + "\"");

                if (confirm) {
                    Collection<PvSampleSourceWrapper> ssToDelete = new HashSet<PvSampleSourceWrapper>();
                    for (PvSampleSourceWrapper ss : selectedPvSampleSources) {
                        if (ss.getSampleSource().getId().equals(
                            pvss.getSampleSource().getId()))
                            ssToDelete.add(ss);
                    }

                    for (PvSampleSourceWrapper pvssDel : ssToDelete) {
                        selectedPvSampleSources.remove(pvssDel);
                    }

                    pvSampleSourceTable.setCollection(selectedPvSampleSources);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void getSampleSources() {
        try {
            List<SampleSource> sampleSources = SessionManager.getAppService()
                .search(SampleSource.class, new SampleSource());
            allSampleSources = new ArrayList<SampleSourceWrapper>();
            for (SampleSource ss : sampleSources) {
                allSampleSources.add(new SampleSourceWrapper(SessionManager
                    .getAppService(), ss));
            }
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Error retrieving sample sources", e);
        }
    }

    public Collection<PvSampleSourceWrapper> getPvSampleSources() {
        return pvSampleSourceTable.getCollection();
    }

    @Override
    public boolean setFocus() {
        return addPvSampleSourceButton.setFocus();
    }
}
