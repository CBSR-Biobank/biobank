package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
    }

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        if (perspective.getId().equals(PatientsAdministrationPerspective.ID)) {
            for (IEditorReference ref : page.getEditorReferences()) {
                IEditorPart part = ref.getEditor(false);
                if (part != null) {
                    page.closeEditor(part, true);
                }
            }
        }
    }
}
