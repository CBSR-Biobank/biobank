package test.ualberta.med.biobank;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Container;

public class TestContainer extends TestDatabase {
	private SiteWrapper site;
	private List<ContainerTypeWrapper> containerTypes;
	private List<ContainerTypeWrapper> topTypes;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		List<SiteWrapper> sites = SiteWrapper.getAllSites(appService);
		if (sites.size() > 0) {
			site = sites.get(0);
			containerTypes = ContainerTypeWrapper.getContainerTypesInSite(
					appService, site, "", false);
			if (containerTypes.size() == 0) {
				throw new Exception(
						"Can't test with no container type in this site");
			}
			topTypes = ContainerTypeWrapper.getTopContainerTypesInSite(
					appService, site);
			if (topTypes.size() == 0) {
				throw new Exception(
						"Can't test with no Top container type in this site");
			}
		} else {
			throw new Exception("Can't test with no site");
		}
	}

	@Test
	public void createOk() throws Exception {
		ContainerWrapper container = new ContainerWrapper(appService);
		container.setLabel("05");
		container.setContainerType(topTypes.get(0));
		container.setSite(site);
		container.persist();

		Integer id = container.getId();
		Assert.assertNotNull(id);
		Container containerInDB = ModelUtils.getObjectWithId(appService,
				Container.class, id);
		Assert.assertNotNull(containerInDB);
	}

	@Test(expected = BiobankCheckException.class)
	public void createNoSite() throws Exception {
		ContainerWrapper container = new ContainerWrapper(appService);
		container.setLabel("05");
		container.setContainerType(topTypes.get(0));
		container.persist();
	}
}
