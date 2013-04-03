package edu.ualberta.med.biobank.common.action.batchoperation.patient;

import java.util.Date;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpPojoHelper;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class PatientBatchOpPojoData implements IBatchOpPojoHelper {

    @SuppressWarnings("nls")
    private static Date DEFAULT_ENROLLMENT_DATE = DateFormatter.parseToDate("1970-01-01");

    private final PatientBatchOpInputPojo pojo;
    private Study study;
    private User user;

    PatientBatchOpPojoData(PatientBatchOpInputPojo pojo) {
        this.pojo = pojo;
    }

    @Override
    public int getCsvLineNumber() {
        return pojo.getLineNumber();
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SuppressWarnings("nls")
    public Patient getNewPatient() {
        Patient patient = new Patient();
        patient.setPnumber(pojo.getPatientNumber());

        Date enrollmentData = pojo.getEnrollmentDate();
        if (enrollmentData == null) {
            patient.setCreatedAt(DEFAULT_ENROLLMENT_DATE);
        } else {
            patient.setCreatedAt(enrollmentData);
        }
        patient.setStudy(study);

        if ((pojo.getComment() != null) && !pojo.getComment().isEmpty()) {
            if (user == null) {
                throw new IllegalStateException("user is null, cannot add comment");
            }

            Comment comment = new Comment();
            comment.setMessage(pojo.getComment());
            comment.setUser(user);
            comment.setCreatedAt(new Date());
            patient.getComments().add(comment);
        }
        return patient;
    }
}
