package edu.ualberta.med.biobank.widgets.specimenlink;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Callbacks made to user users of {@link LinkFormPatientManagement}.
 *
 * @author nelson
 *
 */
public interface ILinkFormPatientManagementParent {

    public Button createButton(Composite parent, String text, int style);

    public boolean isFinished();

    public void focusLost();

    public void textModified();

    public void collectionEventSelectionChanged();

}
