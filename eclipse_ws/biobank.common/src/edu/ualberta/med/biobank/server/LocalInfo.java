package edu.ualberta.med.biobank.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * local information keep on the local thread.
 */
public class LocalInfo implements Serializable {
    private String userName;
    private Map<Integer, SiteInfo> sitesInfos;

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    private static final long serialVersionUID = 7526471155622776147L;

    public void addNewSiteInfo(Integer siteId, String nameShort, Type type) {
        if (sitesInfos == null)
            sitesInfos = new HashMap<Integer, SiteInfo>();
        sitesInfos.put(siteId, new SiteInfo(siteId, nameShort, type));
    }

    public boolean hasSiteInfos() {
        return (sitesInfos != null && sitesInfos.size() > 0);
    }

    public void clearSiteInfos() {
        if (sitesInfos != null)
            sitesInfos.clear();
    }

    public Set<Entry<Integer, SiteInfo>> getSitesInfosEntrySet() {
        if (sitesInfos == null)
            return null;
        return sitesInfos.entrySet();
    }

    public static class SiteInfo {
        public Integer id;
        public String nameShort;
        public Type type;

        public SiteInfo(Integer id, String nameShort, Type type) {
            super();
            this.id = id;
            this.nameShort = nameShort;
            this.type = type;
        }
    }

    public enum Type {
        INSERT, DELETE
    };

}
