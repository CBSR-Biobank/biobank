package edu.ualberta.med.biobank.common.action.comment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CommentUtil {
    public static Comment create(User user, String message) {
        return create(user, new Date(), message);
    }

    public static Comment copyComment(Comment comment) {
        Comment copiedComment = new Comment();
        copiedComment.setCreatedAt(comment.getCreatedAt());
        copiedComment.setMessage(comment.getMessage());
        copiedComment.setUser(comment.getUser());
        return copiedComment;
    }

    public static Set<Comment> copyCommentsFromCollection(Collection<Comment> comments) {
        Set<Comment> result = new HashSet<Comment>();
        for (Comment comment : comments) {
            result.add(copyComment(comment));
        }
        return result;
    }

    public static List<Comment> createCommentsFromList(User user, List<String> comments) {
        List<Comment> completedComments = new ArrayList<Comment>();
        for (String comment : comments) {
            completedComments.add(create(user, comment));
        }
        return completedComments;
    }

    /**
     *
     * @return a new comment object is message is not empty or just spaces.
     *         Returns null if message is empty or just spaces.
     */
    public static Comment create(User user, Date date, String message) {
        Comment comment = null;

        if ((message != null) && !message.isEmpty()) {
            String trimmedMessage = message.trim();

            if (!trimmedMessage.isEmpty()) {
                comment = new Comment();
                comment.setUser(user);
                comment.setCreatedAt(date);
                comment.setMessage(trimmedMessage);
            }
        }

        return comment;
    }
}
