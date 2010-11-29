package edu.ualberta.med.biobank.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

public class BindingContextHelper {

    // global context activation (workbench wide)
    public static Map<String, IContextActivation> contextActivations = new HashMap<String, IContextActivation>();

    public static void activateContextInWorkbench(String contextId) {
        IContextService contextService = (IContextService) PlatformUI
            .getWorkbench().getActiveWorkbenchWindow()
            .getService(IContextService.class);
        if (contextService.getDefinedContextIds().contains(contextId)) {
            IContextActivation activation = contextService
                .activateContext(contextId);
            contextActivations.put(contextId, activation);
        }
    }

    public static void deactivateContextInWorkbench(String contextId) {
        deactivateContextInWorkbench(contextActivations.get(contextId));
    }

    private static void deactivateContextInWorkbench(
        IContextActivation activation) {
        IContextService contextService = (IContextService) PlatformUI
            .getWorkbench().getActiveWorkbenchWindow()
            .getService(IContextService.class);
        if (activation != null) {
            contextService.deactivateContext(activation);
        }
    }

}
