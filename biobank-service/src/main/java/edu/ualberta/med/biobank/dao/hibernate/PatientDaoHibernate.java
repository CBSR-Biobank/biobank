package edu.ualberta.med.biobank.dao.hibernate;

import org.springframework.stereotype.Repository;

import edu.ualberta.med.biobank.dao.PatientDao;
import edu.ualberta.med.biobank.model.study.Patient;

@Repository("PatientDao")
public class PatientDaoHibernate
    extends GenericDaoHibernate<Patient>
    implements PatientDao {

    public PatientDaoHibernate() {
        super(Patient.class);
    }

}
