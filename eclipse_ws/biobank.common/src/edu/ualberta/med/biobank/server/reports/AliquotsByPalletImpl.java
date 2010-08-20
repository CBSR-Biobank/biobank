package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AliquotsByPalletImpl extends AbstractReport {

    private static final String QUERY = "select s from "
        + Aliquot.class.getName() + " s where s.patientVisit.shipment.site "
        + SITE_OPERATOR + SITE_ID + " and s.aliquotPosition.container.id "
        + "in (select path1.container.id from " + ContainerPath.class.getName()
        + " as path1, " + ContainerPath.class.getName()
        + " as path2 where locate(path2.path, path1.path) > 0 "
        + "and path2.container.containerType.nameShort like ?) "
        + "and s.aliquotPosition.container.label = ?";

    public AliquotsByPalletImpl(BiobankReport report) {
        super(QUERY, report);
        List<Object> params = report.getParams();
        params.add(params.remove(0));
        this.report.setParams(params);
    }

    // Use Collections.sort, so can't use RowPostProcess
    @Override
    protected List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {

        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        // get the info
        for (Object ob : results) {
            Aliquot a = (Aliquot) ob;
            String pnumber = a.getPatientVisit().getPatient().getPnumber();
            String inventoryId = a.getInventoryId();
            String stName = a.getSampleType().getNameShort();
            AliquotWrapper aliquotWrapper = new AliquotWrapper(appService, a);
            String aliquotLabel = aliquotWrapper
                .getPositionString(false, false);
            String containerLabel = aliquotWrapper.getParent().getLabel();
            modifiedResults.add(new Object[] { aliquotLabel, containerLabel,
                inventoryId, pnumber, stName });
        }
        // sort by location as an integer
        Collections.sort(modifiedResults, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Object[] castOb1 = ((Object[]) o1);
                Object[] castOb2 = ((Object[]) o2);
                String s1 = (String) castOb1[0];
                String s2 = (String) castOb2[0];
                int compare = s1.substring(0, 1).compareTo(s2.substring(0, 1));
                if (compare == 0)
                    compare = ((Integer) Integer.parseInt(s1.substring(1)))
                        .compareTo(Integer.parseInt(s2.substring(1)));
                return compare;
            }
        });
        // recombine strings
        ArrayList<Object> finalResults = new ArrayList<Object>();
        for (Object ob : modifiedResults) {
            Object[] castOb = ((Object[]) ob);
            finalResults.add(new Object[] {
                (String) castOb[1] + ((String) castOb[0]), castOb[2],
                castOb[3], castOb[4] });
        }
        return finalResults;
    }

}