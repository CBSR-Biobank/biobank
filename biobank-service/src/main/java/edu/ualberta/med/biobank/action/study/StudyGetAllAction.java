package edu.ualberta.med.biobank.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.study.StudyReadPermission;
import edu.ualberta.med.biobank.model.study.Study;

public class StudyGetAllAction implements Action<ListResult<Study>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =
        " FROM " + Study.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new StudyReadPermission().isAllowed(context);
    }

    @Override
    public ListResult<Study> run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(STUDY_INFO_HQL);
        @SuppressWarnings("unchecked")
        List<Study> results = query.list();
        ArrayList<Study> readableStudies = new ArrayList<Study>();
        if (results != null) {
            for (Study s : results)
                if (new StudyReadPermission(s.getId()).isAllowed(context))
                    readableStudies.add(s);
        }
        return new ListResult<Study>(readableStudies);
    }

}
