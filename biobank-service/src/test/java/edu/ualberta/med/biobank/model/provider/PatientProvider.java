package edu.ualberta.med.biobank.model.provider;

import java.util.Date;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ualberta.med.biobank.dao.PatientDao;
import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.Patient;
import edu.ualberta.med.biobank.model.study.Study;

public class PatientProvider
    extends AbstractProvider<Patient> {

    @Autowired
    UserDao userDao;

    @Autowired
    private PatientDao patientDao;

    private int pnumber = 1;

    @Autowired
    public PatientProvider(Mother mother) {
        super(mother);
        mother.bind(Patient.class, this);
    }

    @Override
    public Patient onCreate() {
        Date date = new Date();

        User superadmin = userDao.get(1L);
        Assert.assertEquals("superadmin", superadmin.getLogin());

        Patient patient = new Patient();
        patient.setStudy(mother.getProvider(Study.class).get());
        patient.setPnumber(mother.getName() + "_" + pnumber++);
        patient.setInsertedAndUpdated(superadmin, date.getTime());
        return patient;
    }

    @Override
    public Patient save(Patient patient) {
        patientDao.save(patient);
        return patient;
    }
}
