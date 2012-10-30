package edu.ualberta.med.biobank.model.provider;

import edu.ualberta.med.biobank.model.study.Study;

public class StudyProvider
    extends AbstractProvider<Study> {

    private int name = 1;

    protected StudyProvider(Mother mother) {
        super(mother);
    }

    @Override
    public Study onCreate() {
        Study study = new Study();
        study.setName(mother.getName() + "_" + name++);
        study.setDescription("no description.");
        study.setEnabled(Boolean.TRUE);
        return study;
    }
}
