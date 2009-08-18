package edu.ualberta.med.sampleProcessingRobot.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.WorkbenchException;

import edu.ualberta.med.sampleProcessingRobot.SampleProcessingRobotPlugin;
import edu.ualberta.med.sampleProcessingRobot.perspectives.MainPerspective;

public class ShowPerspectiveHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            IWorkbench workbench = SampleProcessingRobotPlugin.getDefault()
                .getWorkbench();
            workbench.showPerspective(MainPerspective.ID, workbench
                .getActiveWorkbenchWindow());
            IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
                .getActivePage();
            page.resetPerspective();

            for (IEditorReference ref : page.getEditorReferences()) {
                IEditorPart part = ref.getEditor(false);
                if (part != null) {
                    page.closeEditor(part, true);
                }
            }

        } catch (WorkbenchException e) {
            throw new ExecutionException(
                "Error while opening patients perpective", e);
        }
        return null;
    }

}
