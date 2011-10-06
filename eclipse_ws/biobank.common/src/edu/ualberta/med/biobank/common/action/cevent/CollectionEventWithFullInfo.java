package edu.ualberta.med.biobank.common.action.cevent;

import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;

public class CollectionEventWithFullInfo extends CollectionEventInfo {

    private static final long serialVersionUID = 1L;

    public List<SpecimenInfo> sourceSpecimenInfos;
    public List<SpecimenInfo> aliquotedSpecimenInfos;
    public Map<String, EventAttrInfo> eventAttrs;

}
