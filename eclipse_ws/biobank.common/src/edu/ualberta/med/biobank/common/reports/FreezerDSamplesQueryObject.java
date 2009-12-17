package edu.ualberta.med.biobank.common.reports;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Study;

public class FreezerDSamplesQueryObject extends QueryObject {

    private static String PVCOUNT_STRING = "(select count(*) from "
        + Patient.class.getName()
        + " as p where (select count(*) from "
        + PatientVisit.class.getName()
        + " as pv where pv.patient = p and pv.shipment.clinic.id = c.clinic.id) {0} {1})";

    private static String QUERY_STRING = "select s.name, c.clinic.name, "
        + MessageFormat.format(PVCOUNT_STRING, "=", "1")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "2")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "3")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, "=", "4")
        + ", "
        + MessageFormat.format(PVCOUNT_STRING, ">=", "5")
        + ", "
        + "(select count(*) from "
        + PatientVisit.class.getName()
        + " as pvtotal where pvtotal.shipment.clinic.id=c.clinic.id and pvtotal.patient.study.id = s.id), "
        + "(select count(distinct patients.patient.id) from "
        + PatientVisit.class.getName()
        + " as patients where patients.shipment.clinic.id=c.clinic.id and patients.patient.study.id = s.id)"
        + " from " + Study.class.getName()
        + " as s inner join s.contactCollection as c";

    public FreezerDSamplesQueryObject(String name, Integer siteId) {
        super("Lists freezer samples by study.", name, QUERY_STRING,
            new String[] { "Study", "Clinic", "1 Visit", "2 Visit", "3 Visit",
                "4 Visit", "5+ Visits", "Total Visits", "Total Patients" });
        addOption("Start Date", Date.class, new Date(0));
        addOption("End Date", Date.class, new Date());
    }

    public List<Object> postProcess() {
        return null;

    }
}