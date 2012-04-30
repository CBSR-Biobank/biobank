package edu.ualberta.med.biobank.common.action.labelPrinter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;

public class PrinterLabelTemplateGetAllAction implements
    Action<ListResult<PrinterLabelTemplate>> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(PrinterLabelTemplateGetAllAction.class.getName());

    @SuppressWarnings("nls")
    private static final String HQL_QRY =
        "FROM " + PrinterLabelTemplate.class.getName() + " plt"
            + " INNER JOIN FETCH plt.jasperTemplate";

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @Override
    public ListResult<PrinterLabelTemplate> run(ActionContext context)
        throws ActionException {
        log.debug("run");

        ArrayList<PrinterLabelTemplate> plTemplates =
            new ArrayList<PrinterLabelTemplate>(0);

        Query query = context.getSession().createQuery(HQL_QRY);

        @SuppressWarnings("unchecked")
        List<PrinterLabelTemplate> results = query.list();
        if (results != null) {
            plTemplates.addAll(results);
        }

        return new ListResult<PrinterLabelTemplate>(plTemplates);
    }

}
