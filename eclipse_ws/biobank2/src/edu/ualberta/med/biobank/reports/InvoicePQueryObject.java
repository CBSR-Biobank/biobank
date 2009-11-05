package edu.ualberta.med.biobank.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.Sample;

public class InvoicePQueryObject extends QueryObject {
    public InvoicePQueryObject(String name) {
        super(
            SampleWrapper.class,
            "Lists all samples for a particular date range, grouped by clinic.",
            name, "Select " + name + "Alias from " + Sample.class.getName()
                + " as " + name
                + "Alias where "
                // + name + "Alias.linkDate > ? and "
                // + name + "Alias.linkDate < ? and "
                + name + "Alias.patientVisit.patient.study.site = "
                + SessionManager.getInstance().getCurrentSiteWrapper().getId(),
            // + " GROUP BY " + name + "Alias.patientVisit.patient",
            new ArrayList<Class<?>>(), new ArrayList<String>());
        // fieldTypes.add(DateTimeWidget.class);
        // fieldTypes.add(DateTimeWidget.class);
        // fieldNames.add("Start Date");
        // fieldNames.add("End Date");
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return results;
    }
}
