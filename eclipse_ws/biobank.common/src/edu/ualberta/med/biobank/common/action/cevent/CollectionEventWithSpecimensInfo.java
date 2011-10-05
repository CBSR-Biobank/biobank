package edu.ualberta.med.biobank.common.action.cevent;

import java.util.List;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;

public class CollectionEventWithSpecimensInfo extends CollectionEventInfo {

    private static final long serialVersionUID = 1L;

    public List<SpecimenInfo> sourceSpecimenInfos;
    public List<SpecimenInfo> aliquotedSpecimenInfos;

}
