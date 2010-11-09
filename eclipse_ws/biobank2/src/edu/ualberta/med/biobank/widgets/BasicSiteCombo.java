package edu.ualberta.med.biobank.widgets;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class BasicSiteCombo extends ComboViewer {

    protected SiteWrapper selectedSite;

    public BasicSiteCombo(Composite parent,
        WritableApplicationService appService) {
        super(parent, SWT.READ_ONLY);
        setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object e) {
                return ((SiteWrapper) e).getNameShort();
            }
        });
        setContentProvider(new ArrayContentProvider());
        try {
            List<SiteWrapper> sites = SiteWrapper.getSites(appService);
            setInput(sites);
        } catch (Exception e1) {
            BioBankPlugin.openAsyncError("Failed to load sites", e1);
        }
        getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedSite = (SiteWrapper) ((StructuredSelection) getSelection())
                    .getFirstElement();
            }
        });
    }

    public SiteWrapper getSite() {
        return selectedSite;
    }

}
