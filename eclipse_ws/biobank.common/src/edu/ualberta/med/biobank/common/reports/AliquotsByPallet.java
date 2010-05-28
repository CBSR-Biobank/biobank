package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.ContainerPath;

public class AliquotsByPallet extends QueryObject {

    protected static final String NAME = "Aliquots by Pallet";

    public AliquotsByPallet(String op, Integer siteId) {
        super(
            "Given a pallet label, generate a list of aliquots.",
            "select s from "
                + Aliquot.class.getName()
                + " s where s.patientVisit.patient.study.site "
                + op
                + siteId
                + " and s.aliquotPosition.container.id "
                + "in (select path1.container.id from "
                + ContainerPath.class.getName()
                + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 and path2.container.containerType.nameShort like ?) and s.aliquotPosition.container.label = ?",
            new String[] { "Patient", "Inventory ID", "Location", "Type" });
        addOption("Top Container Type", String.class, "");
        addOption("Pallet Label", String.class, "");
    }

    @Override
    protected List<Object> postProcess(List<Object> results) {
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        for (Object ob : results) {
            Aliquot a = (Aliquot) ob;
            String pnumber = a.getPatientVisit().getPatient().getPnumber();
            String inventoryId = a.getInventoryId();
            String stName = a.getSampleType().getNameShort();
            String aliquotLabel = new AliquotWrapper(null, a)
                .getPositionString(true, false);
            modifiedResults.add(new Object[] { pnumber, inventoryId,
                aliquotLabel, stName });
        }
        return modifiedResults;
    }

    @Override
    public String getName() {
        return NAME;
    }
}