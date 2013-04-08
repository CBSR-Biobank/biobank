package edu.ualberta.med.biobank.dao.hibernate;

import edu.ualberta.med.biobank.dao.StudyDao;
import edu.ualberta.med.biobank.model.study.Study;

public class StudyDaoHibernate
    extends GenericDaoHibernate<Study>
    implements StudyDao {

    public StudyDaoHibernate() {
        super(Study.class);
    }

}
