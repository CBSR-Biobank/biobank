package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.OriginInfoBaseWrapper;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Clinic;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class OriginInfoWrapper extends OriginInfoBaseWrapper {

    public OriginInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public OriginInfoWrapper(WritableApplicationService appService,
        OriginInfo originInfo) {
        super(appService, originInfo);
    }

    public List<SpecimenWrapper> getSpecimenCollection() {
        return getSpecimenCollection(false);
    }

    public List<PatientWrapper> getPatientCollection() {
        List<SpecimenWrapper> specimens = getSpecimenCollection();
        List<PatientWrapper> patients = new ArrayList<PatientWrapper>();

        for (SpecimenWrapper specimen : specimens) {
            PatientWrapper patient = specimen.getCollectionEvent().getPatient();

            if (!patients.contains(patient)) {
                patients.add(patient);
            }
        }

        return patients;
    }

    public void checkAtLeastOneSpecimen() throws BiobankCheckException {
        List<SpecimenWrapper> spc = getSpecimenCollection(false);
        if (spc == null || spc.isEmpty()) {
            throw new BiobankCheckException(
                "At least one specimen should be added to this Collection Event.");
        }
    }

    private static final String WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY = "from "
        + Clinic.class.getName() + " as clinic join clinic."
        + ClinicPeer.ORIGIN_INFO_COLLECTION.getName() + " as oi join oi."
        + OriginInfoPeer.SHIPMENT_INFO.getName() + " as si where clinic."
        + ClinicPeer.ID.getName() + "=? and si."
        + ShipmentInfoPeer.WAYBILL.getName() + "=?";

    private boolean checkWaybillUniqueForClinic(ClinicWrapper clinic)
        throws ApplicationException {
        String isSameShipment = "";
        List<Object> params = new ArrayList<Object>();
        params.add(clinic.getId());
        params.add(getShipmentInfo().getWaybill());

        StringBuilder qry = new StringBuilder(
            WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY);
        if (!isNew()) {
            qry.append(" and ce.").append(CollectionEventPeer.ID.getName())
                .append(" <> ?");
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria(WAYBILL_UNIQUE_FOR_CLINIC_BASE_QRY
            + isSameShipment, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        CenterWrapper<?> center = getCenter();
        if (center == null) {
            throw new BiobankCheckException("A Center should be set.");
        }
        checkAtLeastOneSpecimen();

        if (center instanceof ClinicWrapper) {
            ClinicWrapper clinic = (ClinicWrapper) center;
            String waybill = getShipmentInfo().getWaybill();

            if (Boolean.TRUE.equals(clinic.getSendsShipments())) {
                if (waybill == null || waybill.isEmpty()) {
                    throw new BiobankCheckException(
                        "A waybill should be set on this shipment");
                }
                if (!checkWaybillUniqueForClinic(clinic)) {
                    throw new BiobankCheckException(
                        "A collection event with waybill " + waybill
                            + " already exist in clinic "
                            + clinic.getNameShort());
                }
            } else {
                if (waybill != null) {
                    throw new BiobankCheckException(
                        "This clinic doesn't send shipments: waybill should not be set");
                }
            }
        }

    }
}
