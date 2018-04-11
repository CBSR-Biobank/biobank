package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupSaveInfo;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupUpdatePermission;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;

/**
 *
 * Action object that saves a Research Group along with it's associated studies to the database
 *
 * Code Changes -
 * 		1> Add a setter method to accept a list of Study IDs
 * 		2> Change the run method and make it similar to SiteSaveAction
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;
    private ResearchGroupSaveInfo rgInfo;
    private Set<Integer> studyIds = new HashSet<Integer>(0);

    public ResearchGroupSaveAction(ResearchGroupSaveInfo rgInfo) {
        this.rgInfo = rgInfo;
    }

    //OHSDEV
    public void setStudyIds(Set<Integer> studyIds) {
        if (studyIds == null) {
            throw new IllegalArgumentException();
        }

        this.studyIds = studyIds;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ResearchGroupUpdatePermission(rgInfo.id).isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        ResearchGroup rg =
            context
                .get(ResearchGroup.class, rgInfo.id, new ResearchGroup());

        rg.setName(rgInfo.name);
        rg.setNameShort(rgInfo.nameShort);
        rg.setActivityStatus(rgInfo.activityStatus);

        //OHSDEV
        Set<Study> studies = context.load(Study.class, studyIds);
        rg.getStudies().clear();
        rg.getStudies().addAll(studies);

        Address address = new Address();
        address.setId(rgInfo.address.id);
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
        if (!rgInfo.comment.trim().isEmpty()) {
            Set<Comment> comments = rg.getComments();
            if (comments == null) comments = new HashSet<Comment>();
            Comment newComment = new Comment();
            newComment.setCreatedAt(new Date());
            newComment.setMessage(rgInfo.comment);
            newComment.setUser(context.getUser());
            context.getSession().saveOrUpdate(newComment);

            comments.add(newComment);
            rg.setComments(comments);
        }

        context.getSession().saveOrUpdate(rg);
        context.getSession().flush();

        return new IdResult(rg.getId());
    }
}