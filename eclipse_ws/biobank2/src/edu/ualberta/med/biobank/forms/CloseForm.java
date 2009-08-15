package edu.ualberta.med.biobank.forms;

public interface CloseForm {

    /**
     * Called from the BiobankPartListener
     * 
     * @return true if we can open a default page after this form is closed
     */
    public abstract boolean onClose();

}