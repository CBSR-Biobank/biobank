package edu.ualberta.med.biobank.common.action.attachment;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.SimpleResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.FileMetaData;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.type.Hash.SHA1Hash;

public class AttachmentDownloadDataAction
    implements Action<SimpleResult<FileData>> {
    private static final long serialVersionUID = 1L;

    private final Integer attachmentId;
    private final SHA1Hash sha1Hash;

    public AttachmentDownloadDataAction(FileMetaData attachment) {
        this.attachmentId = attachment.getId();
        this.sha1Hash = attachment.getSha1Hash();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public SimpleResult<FileData> run(ActionContext context)
        throws ActionException {

        FileData data = (FileData) context.getSession()
            .createCriteria(FileData.class)
            .createCriteria("attachment")
            .add(Restrictions.idEq(attachmentId))
            .add(Restrictions.eq("sha1Hash", sha1Hash))
            .uniqueResult();

        return new SimpleResult<FileData>(data);
    }
}
