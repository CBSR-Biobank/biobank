package edu.ualberta.med.biobank.wizard;

import org.eclipse.jface.wizard.Wizard;

import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ContainerChooserWizard extends Wizard {
	private TopContainerChooserPage containerChooserPage;
	private PalletPositionChooserPage palletChooserPage;
	private Site site;
	private WritableApplicationService appService;

	public ContainerChooserWizard(WritableApplicationService appService,
			Site site) {
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

	public Site getSite() {
		return site;
	}

	public WritableApplicationService getAppService() {
		return appService;
	}

	public ContainerPosition getSelectedPosition() {
		return palletChooserPage.getSelectedPosition();
	}

	public ContainerType getContainerType() {
		return palletChooserPage.getContainerType();
	}
}
