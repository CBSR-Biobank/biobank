package edu.ualberta.med.biobank.common.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.ContainerPath;

public class AliquotsByPallet extends QueryObject {

    protected static final String NAME = "Aliquots by Pallet";

    public AliquotsByPallet(String op, Integer siteId) {
        super("Given a pallet label, generate a list of aliquots.",
            "select s from " + Aliquot.class.getName()
                + " s where s.patientVisit.patient.study.site " + op + siteId
                + " and s.aliquotPosition.container.id "
                + "in (select path1.container.id from "
                + ContainerPath.class.getName() + " as path1, "
                + ContainerPath.class.getName()
                + " as path2 where locate(path2.path, path1.path) > 0 "
                + "and path2.container.containerType.nameShort like ?) "
                + "and s.aliquotPosition.container.label = ?", new String[] {
                "Location", "Inventory ID", "Patient", "Type" });
        addOption("Top Container Type", String.class, "");
        addOption("Pallet Label", String.class, "");
    }

    @Override
    protected List<Object> postProcess(List<Object> results) {
        AliquotPosition aliquotPosition;
        ArrayList<Object> modifiedResults = new ArrayList<Object>();
        for (Object ob : results) {
            Aliquot a = (Aliquot) ob;
            aliquotPosition = a.getAliquotPosition();
            String pnumber = a.getPatientVisit().getPatient().getPnumber();
            String inventoryId = a.getInventoryId();
            String stName = a.getSampleType().getNameShort();
            String containerLabel = aliquotPosition.getContainer().getLabel();
            String aliquotLabel = LabelingScheme.getPositionString(
                new RowColPos(aliquotPosition.getRow(), aliquotPosition
                    .getCol()), aliquotPosition.getContainer()
                    .getContainerType());
            modifiedResults.add(new Object[] { containerLabel + aliquotLabel,
                inventoryId, pnumber, stName });
        }
        Collections.sort(modifiedResults, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Object[] castOb1 = ((Object[]) o1);
                Object[] castOb2 = ((Object[]) o2);
                return castOb1[0].toString().compareTo(castOb2[0].toString());
            }
        });
        return modifiedResults;
    }

    @Override
    public String getName() {
        return NAME;
    }
}