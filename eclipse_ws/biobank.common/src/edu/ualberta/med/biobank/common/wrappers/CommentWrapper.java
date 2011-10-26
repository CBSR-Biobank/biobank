package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.CommentBaseWrapper;
import edu.ualberta.med.biobank.model.Comment;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CommentWrapper extends CommentBaseWrapper {

    public CommentWrapper(WritableApplicationService appService) {
        super(appService);
        // TODO Auto-generated constructor stub
    }

    public CommentWrapper(WritableApplicationService appService, Comment comment) {
        super(appService, comment);
    }

}
