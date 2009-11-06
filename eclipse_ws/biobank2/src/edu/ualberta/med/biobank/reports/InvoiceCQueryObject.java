package edu.ualberta.med.biobank.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class InvoiceCQueryObject extends QueryObject {
    public InvoiceCQueryObject(String name) {
        super(
            "Lists all samples for a particular date range, grouped by clinic.",
            name, "Select " + name + "Alias.patientVisit.clinic.name, " + name
                + "Alias.patientVisit.patient.id, " + name
                + "Alias.patientVisit.patient.number, " + name
                + "Alias.linkDate, " + name + "Alias.sampleType.name"
                + " from " + Sample.class.getName() + " as " + name
                + "Alias where " + name + "Alias.linkDate > ? and " + name
                + "Alias.linkDate < ? and " + name
                + "Alias.patientVisit.patient.study.site = "
                + SessionManager.getInstance().getCurrentSiteWrapper().getId()
                + " ORDER BY " + name + "Alias.patientVisit.clinic.id, " + name
                + "Alias.patientVisit.patient.id", new ArrayList<Class<?>>(),
            new ArrayList<Object>(), new ArrayList<String>(), new String[] {
                "Clinic", "Patient Id", "Patient Number", "Link Date",
                "Sample Type" });
        fieldTypes.add(DateTimeWidget.class);
        fieldTypes.add(DateTimeWidget.class);
        defaults.add(new Date(0));
        defaults.add(new Date());
        fieldNames.add("Start Date");
        fieldNames.add("End Date");
    }

    @Override
    public List<Object> postProcess(List<Object> results) {
        return results;
    }
}
