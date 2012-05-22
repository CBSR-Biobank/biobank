package edu.ualberta.med.biobank.common.action.labelPrinter;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.labelPrinter.PrinterLabelTemplateGetInfoAction.PrinterLabelTemplateInfo;
import edu.ualberta.med.biobank.common.permission.labelPrinting.LabelPrintingPermission;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;

public class PrinterLabelTemplateGetInfoAction implements
    Action<PrinterLabelTemplateInfo> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(PrinterLabelTemplateGetInfoAction.class.getName());

    @SuppressWarnings("nls")
    private static final String HQL_QRY =
        "FROM " + PrinterLabelTemplate.class.getName() + " plt"
            + " INNER JOIN FETCH plt.jasperTemplate"
            + " WHERE name=?";

    public static class PrinterLabelTemplateInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public PrinterLabelTemplate printerLabelTemplate;
    }

    public final String name;

    public PrinterLabelTemplateGetInfoAction(String name) {
        this.name = name;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new LabelPrintingPermission().isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public PrinterLabelTemplateInfo run(ActionContext context)
        throws ActionException {
        log.debug("run: name={}", name);

        Query query = context.getSession().createQuery(HQL_QRY);
        query.setParameter(0, name);

        PrinterLabelTemplateInfo info = new PrinterLabelTemplateInfo();
        info.printerLabelTemplate = (PrinterLabelTemplate) query.uniqueResult();
        if (info.printerLabelTemplate == null) {
            throw new IllegalStateException("printer label template is null");
        }

        return info;
    }
}
