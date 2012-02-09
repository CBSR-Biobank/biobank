package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction.StudiesInfo;
import edu.ualberta.med.biobank.common.permission.study.StudyReadPermission;
import edu.ualberta.med.biobank.model.Study;

public class StudyGetAllAction implements Action<StudiesInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =
        " FROM " + Study.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new StudyReadPermission().isAllowed(context);
    }

    public static class StudiesInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final ArrayList<Study> studies;

        public StudiesInfo(ArrayList<Study> studys) {
            this.studies = studys;
        }

        public ArrayList<Study> getStudies() {
            return studies;
        }

    }

    @Override
    public StudiesInfo run(ActionContext context) throws ActionException {
        ArrayList<Study> studys = new ArrayList<Study>(0);
        Query query = context.getSession().createQuery(STUDY_INFO_HQL);
        @SuppressWarnings("unchecked")
        List<Study> results = query.list();
        if (results != null) {
            studys.addAll(results);
        }
        return new StudiesInfo(studys);
    }

}
