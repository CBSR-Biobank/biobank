package edu.ualberta.med.biobank.mvp;


public abstract class AbstractApplicationContext implements ApplicationContext {
    @Override
    public Integer getUserId() {
        return getUser() != null ? getUser().getId() : null;
    }

    @Override
    public Integer getWorkingCenterId() {
        return getWorkingCenter() != null ? getWorkingCenter().getId() : null;
    }
}
