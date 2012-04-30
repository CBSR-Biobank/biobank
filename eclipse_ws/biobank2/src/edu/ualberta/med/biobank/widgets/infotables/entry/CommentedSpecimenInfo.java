package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;

public class CommentedSpecimenInfo extends SpecimenInfo {
    public CommentedSpecimenInfo(SpecimenInfo info) {
        super(info);
    }

    private static final long serialVersionUID = -5057145081839375616L;
    public List<String> comments = new ArrayList<String>();
}
