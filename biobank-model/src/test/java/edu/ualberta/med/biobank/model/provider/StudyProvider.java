package edu.ualberta.med.biobank.model.provider;

import edu.ualberta.med.biobank.model.study.Study;

public class StudyProvider
    extends AbstractProvider<Study> {

    protected StudyProvider(Mother mother) {
        super(mother);
    }

    @Override
    public Study onCreate() {
        return null;
    }
}
