package edu.ualberta.med.biobank.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.dialogs.SampleStorageDialog;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.SampleType;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleStorageEntryWidget extends BiobankWidget {

    private SampleStorageInfoTable sampleStorageTable;

    private Button addSampleStorageButton;

    private Collection<SampleType> allSampleTypes;

    private Set<SampleType> selectedSampleTypes;

    private Collection<SampleStorage> selectedSampleStorage;

    public SampleStorageEntryWidget(Composite parent, int style,
        Collection<SampleStorage> selectedSampleStorage, FormToolkit toolkit) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        getSampleTypes();

        setLayout(new GridLayout(1, false));
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        this.selectedSampleStorage = selectedSampleStorage;
        addSampleStorageButton = toolkit.createButton(parent,
            "Add Sample Storage", SWT.PUSH);
        addSampleStorageButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Set<SampleType> sampleTypes = new HashSet<SampleType>(
                    allSampleTypes);
                for (SampleStorage ss : sampleStorageTable.getSampleStorage()) {
                    sampleTypes.remove(ss.getSampleType());
                }
                SampleStorageDialog dlg = new SampleStorageDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    new SampleStorage(), sampleTypes);
                if (dlg.open() == Dialog.OK) {
                    SampleStorage ss = dlg.getSampleStorage();
                    Collection<SampleStorage> collection = SampleStorageEntryWidget.this.selectedSampleStorage;
                    if (collection == null) {
                        collection = new HashSet<SampleStorage>();
                    }
                    collection.add(ss);
                    sampleStorageTable.setSampleStorage(collection);
                }
            }
        });

        sampleStorageTable = new SampleStorageInfoTable(parent,
            this.selectedSampleStorage);
        sampleStorageTable.adaptToToolkit(toolkit);
    }

    private void getSampleTypes() {
        try {
            allSampleTypes = SessionManager.getInstance().getSessionAdapter()
                .getAppService().search(SampleType.class, new SampleType());
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }
}
