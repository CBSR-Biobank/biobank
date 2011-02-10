package edu.ualberta.med.biobank.client.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class ConfigContainers {

    protected Map<String, ContainerTypeWrapper> containerTypeMap;

    protected ConfigContainers(SiteWrapper site) throws Exception {
        if (site == null) {
            throw new Exception("site is null");
        }
        site.reload();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        List<ContainerTypeWrapper> siteContainerTypes = site
            .getContainerTypeCollection();
        if (siteContainerTypes == null) {
            throw new Exception("site " + site.getNameShort()
                + " does not have any container types");
        }
        for (ContainerTypeWrapper ct : siteContainerTypes) {
            containerTypeMap.put(ct.getName(), ct);
        }
    }

    protected ContainerTypeWrapper getContainerType(String name)
        throws Exception {
        ContainerTypeWrapper type = containerTypeMap.get(name);
        if (type == null) {
            throw new Exception("container type " + name + " not in database");
        }
        return type;
    }

    protected static ContainerWrapper addTopLevelContainer(SiteWrapper site,
        String label, ContainerTypeWrapper type) throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setLabel(label);
        container.setSite(site);
        container.setContainerType(type);
        container.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(site.getAppService()));
        container.setTemperature(type.getDefaultTemperature());
        container.persist();
        container.reload();
        return container;
    }

    protected static ContainerWrapper addContainer(SiteWrapper site,
        ContainerTypeWrapper type, ContainerWrapper parent, int row, int col)
        throws Exception {
        ContainerWrapper container = new ContainerWrapper(site.getAppService());
        container.setSite(site);
        container.setContainerType(type);
        container.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(site.getAppService()));
        container.setPositionAsRowCol(new RowColPos(row, col));
        container.setParent(parent);
        container.setTemperature(type.getDefaultTemperature());
        container.persist();
        container.reload();
        return container;
    }

}
