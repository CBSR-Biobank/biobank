package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.PrinterLabelTemplateBaseWrapper;
import edu.ualberta.med.biobank.model.PrinterLabelTemplate;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PrinterLabelTemplateWrapper extends
    PrinterLabelTemplateBaseWrapper {

    public PrinterLabelTemplateWrapper(WritableApplicationService appService,
        PrinterLabelTemplate wrappedObject) {
        super(appService, wrappedObject);
    }

    public PrinterLabelTemplateWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<PrinterLabelTemplate> o) {
        return 0;
    }

    private static final String TEMPLATES_QRY = "from "
        + PrinterLabelTemplate.class.getName();

    public static List<PrinterLabelTemplateWrapper> getAllTemplates(
        WritableApplicationService appService) throws ApplicationException {
        StringBuilder qry = new StringBuilder(TEMPLATES_QRY);

        HQLCriteria criteria = new HQLCriteria(qry.toString());
        List<PrinterLabelTemplate> templates = appService.query(criteria);
        List<PrinterLabelTemplateWrapper> wrappers = new ArrayList<PrinterLabelTemplateWrapper>();
        for (PrinterLabelTemplate t : templates) {
            wrappers.add(new PrinterLabelTemplateWrapper(appService, t));
        }
        return wrappers;
    }

    private static final String TEMPLATE_NAMES_QRY = "select name from "
        + PrinterLabelTemplate.class.getName();

    public static List<String> getTemplateNames(
        WritableApplicationService appService) throws ApplicationException {
        StringBuilder qry = new StringBuilder(TEMPLATE_NAMES_QRY);

        HQLCriteria criteria = new HQLCriteria(qry.toString());
        return appService.query(criteria);
    }

    private static final String TEMPLATE_BY_NAME_QRY = "from "
        + PrinterLabelTemplate.class.getName() + " where name=?";

    public static PrinterLabelTemplateWrapper getTemplateByName(
        WritableApplicationService appService, String name)
        throws ApplicationException {
        StringBuilder qry = new StringBuilder(TEMPLATE_BY_NAME_QRY);
        List<Object> qryParms = new ArrayList<Object>();
        qryParms.add(name);

        HQLCriteria criteria = new HQLCriteria(qry.toString(), qryParms);
        List<PrinterLabelTemplate> templates = appService.query(criteria);
        return new PrinterLabelTemplateWrapper(appService, templates.get(0));
    }
}
