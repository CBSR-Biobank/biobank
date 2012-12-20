package edu.ualberta.med.biobank.action.labelPrinter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.JasperTemplate;

public class JasperTemplateGetAllAction implements
    Action<ListResult<JasperTemplate>> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(JasperTemplateGetAllAction.class.getName());

    @SuppressWarnings("nls")
    private static final String HQL_QRY =
        "FROM " + JasperTemplate.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public ListResult<JasperTemplate> run(ActionContext context)
        throws ActionException {
        log.debug("run");

        ArrayList<JasperTemplate> plTemplates =
            new ArrayList<JasperTemplate>(0);

        Query query = context.getSession().createQuery(HQL_QRY);

        @SuppressWarnings("unchecked")
        List<JasperTemplate> results = query.list();
        if (results != null) {
            plTemplates.addAll(results);
        }

        return new ListResult<JasperTemplate>(plTemplates);
    }

}
