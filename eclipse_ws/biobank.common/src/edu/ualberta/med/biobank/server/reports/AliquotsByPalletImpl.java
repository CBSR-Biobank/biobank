package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AliquotsByPalletImpl extends AbstractReport {

    // private static final String QUERY = "select s from "
    // + Aliquot.class.getName() + " s where s.aliquotPosition.container.id "
    // + "in (select path1.container.id from " + ContainerPath.class.getName()
    // + " as path1, " + ContainerPath.class.getName()
    // + " as path2 where path1.path like path2.path || '/%' and"
    // + " path2.container.id in (" + CONTAINER_LIST + ")) "
    // + "and s.aliquotPosition.container.label = ?";

    public AliquotsByPalletImpl(BiobankReport report) {
        // super(QUERY, report);
        super("", report); //$NON-NLS-1$
    }

    // Use Collections.sort, so can't use RowPostProcess
    @Override
    public List<Object> postProcess(WritableApplicationService appService,
        List<Object> results) {

        // ArrayList<Object> modifiedResults = new ArrayList<Object>();
        // // get the info
        // ContainerWrapper parent = null;
        // for (Object ob : results) {
        // // Aliquot a = (Aliquot) ob;
        // // String pnumber =
        // // a.getProcessingEvent().getPatient().getPnumber();
        // // String inventoryId = a.getInventoryId();
        // // String stName = a.getSpecimenType().getNameShort();
        // // SpecimenWrapper aliquotWrapper = new SpecimenWrapper(appService,
        // // a);
        // // String aliquotLabel = aliquotWrapper
        // // .getPositionString(false, false);
        // // parent = aliquotWrapper.getParent();
        // // String containerLabel = aliquotWrapper.getParent().getLabel();
        // // modifiedResults.add(new Object[] { aliquotLabel, containerLabel,
        // // inventoryId, pnumber, stName });
        // }
        // if (parent != null
        // && parent.getContainerType().getChildLabelingSchemeId() != 1) {
        //
        // } else {
        // // sort by location as an integer
        // Collections.sort(modifiedResults, new Comparator<Object>() {
        // @Override
        // public int compare(Object o1, Object o2) {
        // Object[] castOb1 = ((Object[]) o1);
        // Object[] castOb2 = ((Object[]) o2);
        // String s1 = (String) castOb1[0];
        // String s2 = (String) castOb2[0];
        // int compare = s1.substring(0, 1).compareTo(
        // s2.substring(0, 1));
        // if (compare == 0)
        // compare = ((Integer) Integer.parseInt(s1.substring(1)))
        // .compareTo(Integer.parseInt(s2.substring(1)));
        // return compare;
        // }
        // });
        // }
        // // recombine strings
        // ArrayList<Object> finalResults = new ArrayList<Object>();
        // for (Object ob : modifiedResults) {
        // Object[] castOb = ((Object[]) ob);
        // finalResults.add(new Object[] {
        // (String) castOb[1] + ((String) castOb[0]), castOb[2],
        // castOb[3], castOb[4] });
        // }
        // return finalResults;
        return null;
    }

}