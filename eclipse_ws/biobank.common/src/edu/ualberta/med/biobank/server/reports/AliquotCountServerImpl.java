package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotCountServerImpl {

    private static String QUERY = "Select Alias.sampleType.name, count(*) from "
        + Aliquot.class.getName()
        + " as Alias where Alias.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like 'SS%') and Alias.patientVisit.patient.study.site "
        + "{0} {1} GROUP BY Alias.sampleType.name";

    public HQLCriteria getQuery(List<Object> params) {
        // TODO formatter
        // method getParams
        HQLCriteria criteria = new HQLCriteria(QUERY, params);
        return criteria;
    }
}
