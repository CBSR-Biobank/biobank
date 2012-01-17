package edu.ualberta.med.biobank.test.action;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.User;

public class TestSecurity extends TestAction {
    @Test
    public void testRoleSave() {
        session.beginTransaction();

        User user = (User) session.createQuery(
            "SELECT o FROM " + User.class.getName()
                + " o WHERE o.login = 'testuser'")
            .list().iterator().next();

        ActivityStatus active = (ActivityStatus) session.createQuery(
            "SELECT o FROM " + ActivityStatus.class.getName()
                + " o WHERE o.name = 'Active'")
            .list().iterator().next();

        Comment comment = new Comment();
        comment.setCreatedAt(new Date());
        comment.setMessage("asdf");
        comment.setUser(user);

        session.save(comment);

        Set<Comment> comments = new HashSet<Comment>();
        comments.add(comment);

        Site site = new Site();
        site.setAddress(new Address());
        site.setActivityStatus(active);
        site.setName("asdf");
        site.setNameShort("asdf");
        site.setCommentCollection(comments);

        session.save(site);

        session.getTransaction().commit();
        session.flush();

        session.beginTransaction();

        session.delete(site);

        session.getTransaction().commit();
        session.flush();
    }
}
