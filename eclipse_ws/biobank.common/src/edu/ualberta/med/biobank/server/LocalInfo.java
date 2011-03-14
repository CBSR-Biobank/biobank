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
    private Map<Integer, CenterInfo> centerInfos;

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    private static final long serialVersionUID = 7526471155622776147L;

    public void addNewCenterInfo(Integer centerId, String nameShort,
        Class<?> centerClass, ActionType type) {
        if (centerInfos == null)
            centerInfos = new HashMap<Integer, CenterInfo>();
        centerInfos.put(centerId, new CenterInfo(centerId, nameShort,
            centerClass, type));
    }

    public boolean hasCenterInfos() {
        return (centerInfos != null && centerInfos.size() > 0);
    }

    public void clearCenterInfos() {
        if (centerInfos != null)
            centerInfos.clear();
    }

    public Set<Entry<Integer, CenterInfo>> getCenterInfosEntrySet() {
        if (centerInfos == null)
            return null;
        return centerInfos.entrySet();
    }

    public static class CenterInfo {
        public Integer id;
        public String nameShort;
        public Class<?> centerClass;
        public ActionType type;

        public CenterInfo(Integer id, String nameShort, Class<?> centerClass,
            ActionType type) {
            super();
            this.id = id;
            this.nameShort = nameShort;
            this.centerClass = centerClass;
            this.type = type;
        }
    }

    public enum ActionType {
        INSERT, DELETE
    };

}
