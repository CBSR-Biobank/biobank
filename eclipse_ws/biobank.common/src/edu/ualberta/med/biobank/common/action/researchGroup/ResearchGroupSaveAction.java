package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupSavePermission;
import edu.ualberta.med.biobank.common.permission.shipment.OriginInfoSavePermission;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class ResearchGroupSaveAction implements Action<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ResearchGroupSaveInfo rgInfo;

    public ResearchGroupSaveAction(ResearchGroupSaveInfo rgInfo) {
        this.rgInfo = rgInfo;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new ResearchGroupSavePermission(rgInfo.id).isAllowed(user,
            session);
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        ResearchGroup rg =
            sessionUtil.get(ResearchGroup.class, rgInfo.id, new ResearchGroup());

        rg.setName(rgInfo.name);
        rg.setNameShort(rgInfo.nameShort);
        
        rg.setStudy(sessionUtil.get(Study.class, rgInfo.studyId));
        rg.setActivityStatus(sessionUtil.get(ActivityStatus.class, rgInfo.activityStatusId));
        
        
        Address address = new Address();
        address.setId(rgInfo.address.id);
        address.setName(rgInfo.address.name);
        address.setCity(rgInfo.address.city);
        address.setProvince(rgInfo.address.province);
        address.setCountry(rgInfo.address.country);
        address.setEmailAddress(rgInfo.address.email);
        address.setFaxNumber(rgInfo.address.fax);
        address.setPhoneNumber(rgInfo.address.phone);
        address.setStreet1(rgInfo.address.street1);
        address.setStreet2(rgInfo.address.street2);
        address.setPostalCode(rgInfo.address.postalCode);
        
        rg.setAddress(address);
        
        // This stuff could be extracted to a util method. need to think about
        // how
        if (!rgInfo.comment.trim().equals("")) {
            Collection<Comment> comments = rg.getCommentCollection();
            if (comments == null) comments = new HashSet<Comment>();
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(rgInfo.comment);
            newComment.setUser(user);
            session.saveOrUpdate(newComment);
            
            comments.add(newComment);
            rg.setCommentCollection(comments);
        }

        session.saveOrUpdate(rg);
        session.flush();

        return rg.getId();
    }
}
