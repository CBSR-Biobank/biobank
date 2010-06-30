package edu.ualberta.med.biobank.client.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
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

}
