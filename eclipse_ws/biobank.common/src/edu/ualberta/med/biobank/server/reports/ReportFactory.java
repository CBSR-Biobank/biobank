package edu.ualberta.med.biobank.server.reports;

import java.lang.reflect.Constructor;
import java.util.List;

import edu.ualberta.med.biobank.client.reports.BiobankReport;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ReportFactory {

    @SuppressWarnings("unchecked")
    public static AbstractReport createReport(BiobankReport report)
        throws ApplicationException {
        try {
            String reportClass = report.getClass().getSimpleName() + "Impl";
            reportClass = reportClass.replace(".client.", ".server.");
            Class<AbstractReport> clazz = (Class<AbstractReport>) Class
                .forName(reportClass);
            Constructor<AbstractReport> constructor = clazz.getConstructor(
                List.class, List.class);
            return constructor.newInstance(report);
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
