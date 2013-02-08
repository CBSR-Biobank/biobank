package edu.ualberta.med.biobank.widgets.specimentypeselection;


public interface ISpecimenTypeSelectionChangedListener {
    /**
     * Notifies that the selection has changed.
     *
     * @param event event object describing the change
     */
    public void selectionChanged(SpecimenTypeSelectionEvent event);

}
