package edu.ualberta.med.biobank.common.action.site;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class SaveSiteAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    private final Integer siteId;

    // Specific properties force the programmer only to modify the intended
    // data. A little faster. But disregards version checks. Version checks
    // might apply, but they might not, up to the individual action (e.g.
    // "incrementCountAction" shouldn't care).

    private String name;
    private String nameShort;
    private String comment;
    private Address address;
    private ActivityStatus activityStatus;
    private Set<Integer> studyIds;

    public SaveSiteAction(Integer siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Collection<Integer> getStudyIds() {
        return studyIds;
    }

    public void setStudyIds(Set<Integer> studyIds) {
        this.studyIds = studyIds;
    }

    public Integer getSiteId() {
        return siteId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Site site = sessionUtil.get(Site.class, siteId, new Site());

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        site.setName(name);
        site.setNameShort(nameShort);
        site.setComment(comment);
        site.setActivityStatus(activityStatus);

        // TODO: remember to check the address
        site.setAddress(address);

        // TODO: check that the user has access to at least the studies they are
        // removing or adding?
        Map<Integer, Study> studies = sessionUtil.get(Study.class, studyIds);
        site.setStudyCollection(new HashSet<Study>(studies.values()));

        session.saveOrUpdate(site);
        session.flush();

        // TODO: SHOULD NOT require a flush so that we can get the inserted id
        // if this was an insert, try using a callback that sets the response
        // value instead?

        return null;
    }
}
