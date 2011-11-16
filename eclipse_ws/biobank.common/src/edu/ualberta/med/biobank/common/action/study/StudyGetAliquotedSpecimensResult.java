package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;

public class StudyGetAliquotedSpecimensResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final ArrayList<AliquotedSpecimen> aliquotedSpecimens;

    public StudyGetAliquotedSpecimensResult(
        ArrayList<AliquotedSpecimen> aliquotedSpecimens) {
        this.aliquotedSpecimens = aliquotedSpecimens;
    }

    public ArrayList<AliquotedSpecimen> getAliquotedSpecimens() {
        return aliquotedSpecimens;
    }
}
