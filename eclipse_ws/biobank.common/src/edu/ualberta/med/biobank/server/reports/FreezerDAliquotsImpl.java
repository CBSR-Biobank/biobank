package edu.ualberta.med.biobank.server.reports;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class FreezerDAliquotsImpl extends AbstractReport {

    protected static final String QUERY = "select aliquot.patientVisit.patient.study.nameShort,"
        + " aliquot.patientVisit.shipment.clinic.name , year(aliquot.linkDate),"
        + " {0}(aliquot.linkDate), count(aliquot.linkDate) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot.aliquotPosition not in (from "
        + AliquotPosition.class.getName()
        + " a where a.container.label like ?) and aliquot.aliquotPosition.container.id"
        + " in (select path1.container.id from "
        + ContainerPath.class.getName()
        + " as path1, "
        + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 and"
        + " path2.container.containerType.name like ?) and"
        + " aliquot.patientVisit.patient.study.site "
        + siteOperatorString
        + siteIdString
        + " group by aliquot.patientVisit.patient.study.nameShort,"
        + " aliquot.patientVisit.shipment.clinic.name,  year(aliquot.linkDate),"
        + " {0}(aliquot.linkDate)";

    private boolean groupByYear;

    public FreezerDAliquotsImpl(List<Object> parameters,
        List<ReportOption> options) {
        super(QUERY, parameters, options);
        for (int i = 0; i < options.size(); i++) {
            ReportOption option = options.get(i);
            if (parameters.get(i) == null)
                parameters.set(i, option.getDefaultValue());
            if (option.getType().equals(String.class))
                parameters.set(i, "%" + parameters.get(i) + "%");
        }
        // FIXME should do on client side
        // columnNames[2] = (String) params.get(0);
        String groupBy = (String) parameters.get(0);
        queryString = MessageFormat.format(queryString, groupBy);
        parameters.set(0, "SS%");
        parameters.add("%Freezer%");
        groupByYear = groupBy.equals("Year");
    }

    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {
        List<Object> compressedDates = new ArrayList<Object>();
        if (groupByYear) {
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0], castOb[1],
                    castOb[3], castOb[4] });
            }
        } else {
            // FIXME needs BiobankListProxy
            for (Object ob : results) {
                Object[] castOb = (Object[]) ob;
                compressedDates.add(new Object[] { castOb[0], castOb[1],
                    castOb[3] + "-" + castOb[2], castOb[4] });
            }
        }
        return compressedDates;
    }

}