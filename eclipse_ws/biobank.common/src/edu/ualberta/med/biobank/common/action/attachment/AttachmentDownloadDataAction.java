package edu.ualberta.med.biobank.common.action.attachment;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.SimpleResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Attachment;
import edu.ualberta.med.biobank.model.AttachmentData;
import edu.ualberta.med.biobank.model.type.Hash.SHA1Hash;

public class AttachmentDownloadDataAction
    implements Action<SimpleResult<AttachmentData>> {
    private static final long serialVersionUID = 1L;

    private final Integer attachmentId;
    private final SHA1Hash sha1Hash;

    public AttachmentDownloadDataAction(Attachment attachment) {
        this.attachmentId = attachment.getId();
        this.sha1Hash = attachment.getSha1Hash();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public SimpleResult<AttachmentData> run(ActionContext context)
        throws ActionException {

        AttachmentData data = (AttachmentData) context.getSession()
            .createCriteria(AttachmentData.class)
            .createCriteria("attachment")
            .add(Restrictions.idEq(attachmentId))
            .add(Restrictions.eq("sha1Hash", sha1Hash))
            .uniqueResult();

        return new SimpleResult<AttachmentData>(data);
    }
}
