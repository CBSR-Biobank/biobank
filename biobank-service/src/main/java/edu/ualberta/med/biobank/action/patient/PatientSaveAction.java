package edu.ualberta.med.biobank.action.patient;

import java.util.Date;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.comment.CommentUtil;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.permission.patient.PatientCreatePermission;
import edu.ualberta.med.biobank.permission.patient.PatientUpdatePermission;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;

public class PatientSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private Integer patientId;
    private Integer studyId;
    private String pnumber;
    private Date createdAt;

    private String commentText;

    public PatientSaveAction(Integer patientId, Integer studyId,
        String pnumber, Date createdAt, String commentText) {
        this.patientId = patientId;
        this.studyId = studyId;
        this.pnumber = pnumber;
        this.createdAt = createdAt;
        this.commentText = commentText;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Permission permission;
        if (patientId == null) {
            permission = new PatientCreatePermission(studyId);
        } else {
            permission = new PatientUpdatePermission(patientId);
        }
        return permission.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Patient patientToSave;

        if (patientId == null) {
            patientToSave = new Patient();
        } else {
            patientToSave = context.load(Patient.class, patientId);
        }

        patientToSave.setPnumber(pnumber);
        patientToSave.setCreatedAt(createdAt);
        patientToSave.setStudy(context.load(Study.class, studyId));
        saveComment(context, patientToSave);

        context.getSession().saveOrUpdate(patientToSave);

        return new IdResult(patientToSave.getId());
    }

    private void saveComment(ActionContext context, Patient p) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            p.getComments().add(comment);
        }
    }
}
