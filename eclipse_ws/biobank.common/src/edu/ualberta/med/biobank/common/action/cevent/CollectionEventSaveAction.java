package edu.ualberta.med.biobank.common.action.cevent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;

public class CollectionEventSaveAction implements Action<Integer> {

    private static final long serialVersionUID = 1L;

    private Integer ceventId;
    private Integer patientId;
    private Integer visitNumber;
    private Integer statusId;
    private String comments;

    public static class CESpecimenInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        public Integer id;
        public String inventoryId;
        public Date timeDrawn;
        public Integer statusId;
        public Integer specimenTypeId;
        public String comment;
        public Double quantity;
    }

    private List<CESpecimenInfo> sourceSpecimens;

    private Integer centerId;

    // FIXME pvdata

    public CollectionEventSaveAction(Integer ceventId, Integer patientId,
        Integer visitNumber, Integer statusId, String comments,
        Integer centerId, List<CESpecimenInfo> sourceSpecs) {
        this.ceventId = ceventId;
        this.patientId = patientId;
        this.visitNumber = visitNumber;
        this.statusId = statusId;
        this.comments = comments;
        this.centerId = centerId;
        this.sourceSpecimens = sourceSpecs;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Integer doAction(Session session) throws ActionException {
        CollectionEvent ceventToSave;
        if (ceventId == null) {
            ceventToSave = new CollectionEvent();
        } else {
            ceventToSave = (CollectionEvent) session.get(CollectionEvent.class,
                ceventId);
        }
        // FIXME checks?
        // FIXME permission ?

        ceventToSave
            .setPatient((Patient) session.get(Patient.class, patientId));
        ceventToSave.setVisitNumber(visitNumber);
        ceventToSave.setActivityStatus((ActivityStatus) session.get(
            ActivityStatus.class, statusId));
        ceventToSave.setComment(comments);

        setSourceSpecimens(session, ceventToSave);

        // FIXME set pvdata

        session.saveOrUpdate(ceventToSave);

        return ceventToSave.getId();
    }

    private void setSourceSpecimens(Session session,
        CollectionEvent ceventToSave) {
        OriginInfo oi = new OriginInfo();
        oi.setCenter((Center) session.get(Center.class, centerId));
        session.saveOrUpdate(oi);
        Set<Specimen> newSourceSpecList = new HashSet<Specimen>();
        for (CESpecimenInfo specInfo : sourceSpecimens) {
            Specimen specimen;
            if (specInfo.id == null) {
                specimen = new Specimen();
                specimen.setCurrentCenter(oi.getCenter());
                specimen.setOriginInfo(oi);
            } else {
                specimen = (Specimen) session.get(Specimen.class, specInfo.id);
            }
            specimen.setActivityStatus((ActivityStatus) session.get(
                ActivityStatus.class, specInfo.statusId));
            specimen.setCollectionEvent(ceventToSave);
            // cascade will save-update the specimens from this list:
            getAllSpecimenCollection(ceventToSave).add(specimen);
            specimen.setOriginalCollectionEvent(ceventToSave);
            getOriginalSpecimenCollection(ceventToSave).add(specimen);
            specimen.setComment(specInfo.comment);
            specimen.setCreatedAt(specInfo.timeDrawn);
            specimen.setInventoryId(specInfo.inventoryId);
            specimen.setQuantity(specInfo.quantity);
            specimen.setSpecimenType((SpecimenType) session.get(
                SpecimenType.class, specInfo.specimenTypeId));
            newSourceSpecList.add(specimen);
        }
        List<Specimen> oldList = new ArrayList<Specimen>(
            getOriginalSpecimenCollection(ceventToSave));
        oldList.removeAll(newSourceSpecList);
        // need to remove from collections otherwise will be re-saved by
        // cascade.
        getAllSpecimenCollection(ceventToSave).removeAll(oldList);
        getOriginalSpecimenCollection(ceventToSave).removeAll(oldList);
        for (Specimen spec : oldList) {
            if (spec.getId() != null)
                // those left need to be deleted
                // FIXME try delete-orphan cascade?
                session.delete(spec);
        }
    }

    private Collection<Specimen> getOriginalSpecimenCollection(
        CollectionEvent ceventToSave) {
        Collection<Specimen> specs = ceventToSave
            .getOriginalSpecimenCollection();
        if (specs == null) {
            specs = new HashSet<Specimen>();
            ceventToSave.setOriginalSpecimenCollection(specs);
        }
        return specs;
    }

    private Collection<Specimen> getAllSpecimenCollection(
        CollectionEvent ceventToSave) {
        Collection<Specimen> specs = ceventToSave.getAllSpecimenCollection();
        if (specs == null) {
            specs = new HashSet<Specimen>();
            ceventToSave.setAllSpecimenCollection(specs);
        }
        return specs;
    }
}
