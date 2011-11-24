package edu.ualberta.med.biobank.common.action.comment;

import java.util.Date;

import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CommentUtil {
    public static Comment create(User user, String message) {
        return create(user, new Date(), message);
    }

    public static Comment create(User user, Date date, String message) {
        Comment comment = null;
        String trimmedMessage = message.trim();

        if (!trimmedMessage.isEmpty()) {
            comment = new Comment();
            comment.setUser(user);
            comment.setCreatedAt(date);
            comment.setMessage(trimmedMessage);
        }

        return comment;
    }
}
