package edu.ualberta.med.biobank.server.reports;

import java.lang.reflect.Constructor;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ReportFactory {

    @SuppressWarnings("unchecked")
    public static AbstractReport createReport(BiobankReport report)
        throws ApplicationException {
        try {
            String reportClass = BiobankReport.EDITOR_PATH.concat(report
                .getClassName().concat("Impl")); 
            reportClass = reportClass.replace(".editors", ".server.reports");  
            Class<AbstractReport> clazz = (Class<AbstractReport>) Class
                .forName(reportClass);
            Constructor<AbstractReport> constructor = clazz
                .getConstructor(BiobankReport.class);
            return constructor.newInstance(report);
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
