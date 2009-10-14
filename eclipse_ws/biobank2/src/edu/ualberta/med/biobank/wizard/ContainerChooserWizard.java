package edu.ualberta.med.biobank.wizard;

import org.eclipse.jface.wizard.Wizard;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.ContainerCell;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerChooserWizard extends Wizard {
    private TopContainerChooserPage containerChooserPage;
    private PalletPositionChooserPage palletChooserPage;
    private SiteWrapper site;
    private WritableApplicationService appService;

    public ContainerChooserWizard(WritableApplicationService appService,
        SiteWrapper site) {
        super();
        this.appService = appService;
        this.site = site;
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        containerChooserPage = new TopContainerChooserPage();
        palletChooserPage = new PalletPositionChooserPage();
        addPage(containerChooserPage);
        addPage(palletChooserPage);
    }

    @Override
    public boolean performFinish() {
        return true;
    }

    public SiteWrapper getSite() {
        return site;
    }

    public WritableApplicationService getAppService() {
        return appService;
    }

    public ContainerCell getSelectedPosition() {
        return palletChooserPage.getSelectedPosition();
    }

    public ContainerTypeWrapper getContainerType() {
        return palletChooserPage.getContainerType();
    }
}
