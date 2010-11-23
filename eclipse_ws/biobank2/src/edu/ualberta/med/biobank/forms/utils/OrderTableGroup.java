package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public enum OrderTableGroup {
    ADDED("Added") {
        @Override
        public List<AliquotWrapper> getChildren(DispatchWrapper shipment) {
            try {
                return PatientWrapper
                    .getPatient(SessionManager.getAppService(), "1923")
                    .getPatientVisitCollection().get(0).getAliquotCollection()
                    .subList(0, 5);
            } catch (ApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    },
    NON_PROCESSED("Non Processed") {
        @Override
        public List<AliquotWrapper> getChildren(DispatchWrapper shipment) {
            try {
                return PatientWrapper
                    .getPatient(SessionManager.getAppService(), "1923")
                    .getPatientVisitCollection().get(0).getAliquotCollection()
                    .subList(3, 52);
            } catch (ApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    },
    CLAIMED("Claimed") {
        @Override
        public List<AliquotWrapper> getChildren(DispatchWrapper shipment) {
            try {
                return PatientWrapper
                    .getPatient(SessionManager.getAppService(), "1923")
                    .getPatientVisitCollection().get(0).getAliquotCollection()
                    .subList(5, 15);
            } catch (ApplicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    };

    private String label;

    private OrderTableGroup(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getTitle(DispatchWrapper ship) {
        return label + " (" + getChildren(ship).size() + ")";
    }

    public abstract List<AliquotWrapper> getChildren(DispatchWrapper shipment);

    public static Object findParent(AliquotWrapper dsa) {
        for (OrderTableGroup tg : values()) {
            if (tg.getChildren(null).contains(dsa)) {
                return tg;
            }
        }
        return null;
    }

    public static List<OrderTableGroup> getGroupsForShipment(
        DispatchWrapper ship) {
        List<OrderTableGroup> groups = new ArrayList<OrderTableGroup>();
        groups.add(CLAIMED);
        groups.add(ADDED);
        groups.add(NON_PROCESSED);
        return groups;
    }
}
