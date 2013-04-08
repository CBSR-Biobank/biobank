package edu.ualberta.med.biobank.dao.hibernate;

import org.springframework.stereotype.Repository;

import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.model.security.User;

@Repository("UserDao")
public class UserDaoHibernate
    extends GenericDaoHibernate<User>
    implements UserDao {

    public UserDaoHibernate() {
        super(User.class);
    }

}