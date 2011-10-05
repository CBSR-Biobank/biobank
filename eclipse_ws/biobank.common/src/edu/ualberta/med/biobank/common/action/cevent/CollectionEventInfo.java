package edu.ualberta.med.biobank.common.action.cevent;

import java.io.Serializable;
import java.util.Date;

import edu.ualberta.med.biobank.common.action.util.InfoUtil;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.CollectionEvent;

public class CollectionEventInfo implements Serializable, NotAProxy,
    Comparable<CollectionEventInfo> {

    private static final long serialVersionUID = 1L;

    public CollectionEvent cevent;
    public Long sourceSpecimenCount;
    public Long aliquotedSpecimenCount;
    public Date minSourceSpecimenDate;

    @Override
    public int compareTo(CollectionEventInfo info) {
        Integer nber1 = cevent.getVisitNumber();
        Integer nber2 = info.cevent.getVisitNumber();
        if (nber1 != null && nber2 != null) {
            return nber1.compareTo(nber2);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CollectionEventInfo) {
            CollectionEventInfo ceventInfo = (CollectionEventInfo) o;
            if (this == ceventInfo)
                return true;
            return InfoUtil.equals(cevent, ceventInfo.cevent);
        }
        return false;
    }

}
