package edu.ualberta.med.biobank.common.permission;

import java.util.ArrayList;
import java.util.Collection;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class SiteReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Site site;

    public SiteReadPermission(Site site) {
        this.site = site;
    }

    @Override
    public boolean isAllowed(User user) {
        return false;
    }
}
