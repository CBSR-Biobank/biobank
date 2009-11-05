package edu.ualberta.med.biobank.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class InvoiceCQueryObject extends QueryObject {
    public InvoiceCQueryObject(String name) {
        super(
            SampleWrapper.class,
            "Lists all samples for a particular date range, grouped by clinic.",
            name, "Select " + name + "Alias, " + name
                + "Alias.patientVisit.clinic.id from " + Sample.class.getName()
                + " as " + name + "Alias where " + name
                + "Alias.linkDate > ? and " + name
                + "Alias.linkDate < ? GROUP BY " + name
                + "Alias.patientVisit.clinic.id", new ArrayList<Class<?>>(),
            new ArrayList<String>());
        fieldTypes.add(DateTimeWidget.class);
        fieldTypes.add(DateTimeWidget.class);
        fieldNames.add("Start Date");
        fieldNames.add("End Date");
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return results;
    }
}
