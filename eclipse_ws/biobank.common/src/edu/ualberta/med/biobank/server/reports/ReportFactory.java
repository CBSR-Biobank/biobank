package edu.ualberta.med.biobank.server.reports;

import java.lang.reflect.Constructor;
import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ReportFactory {

    @SuppressWarnings("unchecked")
    public static AbstractReport createReport(String reportClassName,
        List<Object> parameters, List<ReportOption> options)
        throws ApplicationException {
        try {
            String reportClass = reportClassName + "Impl";
            reportClass = reportClass.replace(".client.", ".server.");
            Class<AbstractReport> clazz = (Class<AbstractReport>) Class
                .forName(reportClass);
            Constructor<AbstractReport> constructor = clazz.getConstructor(
                List.class, List.class);
            return constructor.newInstance(parameters, options);
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
