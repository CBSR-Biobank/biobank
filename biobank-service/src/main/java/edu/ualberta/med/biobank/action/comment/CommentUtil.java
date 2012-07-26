package edu.ualberta.med.biobank.action.comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CommentUtil {
    public static Comment create(User user, String message) {
        return create(user, new Date(), message);
    }

    public static List<Comment> createCommentsFromList(User user,
        List<String> comments) {
        List<Comment> completedComments = new ArrayList<Comment>();
        for (String comment : comments)
            completedComments.add(create(user, comment));
        return completedComments;
    }

    public static Comment create(User user, Date date, String message) {
        Comment comment = null;

        if ((message != null) && !message.isEmpty()) {
            String trimmedMessage = message.trim();

            if (!trimmedMessage.isEmpty()) {
                comment = new Comment();
                comment.setUser(user);
                comment.setTimeCreated(date);
                comment.setMessage(trimmedMessage);
            }
        }

        return comment;
    }
}
