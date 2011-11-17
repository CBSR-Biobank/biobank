package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;
import java.util.List;

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

    public static String commentListToString(List<CommentWrapper> comments) {
        StringBuilder sb = new StringBuilder();
        for (CommentWrapper comment : comments) {
            sb.append(comment.getMessage());
            sb.append("; ");
        }
        if (sb.length()>0) sb.delete(sb.length()-2, sb.length() -1);
        return sb.toString();
    }
    
}
