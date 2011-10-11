package edu.ualberta.med.biobank.mvp.view;

public interface EditView extends CloseableView, ReloadableView, SaveableView {
    // TODO: have the presenter listen to modifications on the HasValue-s, which
    // maybe should have a default value, then should setDirty as appropriate
    void setDirty(boolean isDirty);

    // TODO: possibly set a map of source objects to error list?
    void setErrors();

    void setEditType(EditType editType);

    public enum EditType {
        CREATE, UPDATE;
    }
}
