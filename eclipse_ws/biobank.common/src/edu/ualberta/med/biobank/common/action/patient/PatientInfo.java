package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.util.List;

import edu.ualberta.med.biobank.common.action.cevent.CollectionEventInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Patient;

public class PatientInfo implements Serializable, NotAProxy {
    private static final long serialVersionUID = 1L;

    public Patient patient;
    public List<CollectionEventInfo> cevents;
    public Long sourceSpecimenCount;
    public Long aliquotedSpecimenCount;
}
