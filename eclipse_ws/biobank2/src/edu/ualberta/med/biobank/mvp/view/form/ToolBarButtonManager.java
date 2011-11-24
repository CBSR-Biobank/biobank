package edu.ualberta.med.biobank.mvp.view.form;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.mvp.event.EmptyClickEvent;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;

public class ToolBarButtonManager {
    private final Map<ButtonType, ButtonImpl> actions =
        new HashMap<ButtonType, ButtonImpl>();

    public enum ButtonType {
        // TODO: internationalize text
        EDIT("Edit", BgcPlugin.IMG_EDIT_FORM),
        PRINT("Print", BgcPlugin.IMG_PRINTER),
        RELOAD("Reload", BgcPlugin.IMG_RESET_FORM),
        CLOSE("Close", BgcPlugin.IMG_CANCEL_FORM),
        SAVE("Save", BgcPlugin.IMG_CONFIRM_FORM);

        private final String label;
        private final ImageDescriptor image;

        private ButtonType(String label, String imageKey) {
            this.label = label;
            this.image = getImageDescriptor(imageKey);
        }

        private ImageDescriptor getImageDescriptor(String imageKey) {
            ImageRegistry reg = BgcPlugin.getDefault().getImageRegistry();
            return ImageDescriptor.createFromImage(reg.get(imageKey));
        }
    }

    private final IToolBarManager toolBarManager;

    public ToolBarButtonManager(IToolBarManager toolbarManager) {
        this.toolBarManager = toolbarManager;
    }

    /**
     * Returns an {@link HasButton} based on the given {@link ButtonType},
     * creating it if necessary.
     * 
     * @param buttonType
     * @return
     */
    public HasButton get(ButtonType buttonType) {
        ButtonImpl button = actions.get(buttonType);
        if (button == null) {
            button = new ButtonImpl(buttonType);
            actions.put(buttonType, button);
            toolBarManager.add(button.getContributionItem());
            toolBarManager.update(true);
        }
        return button;
    }

    private class ButtonImpl implements HasButton {
        private final HandlerManager handlerManager = new HandlerManager(this);
        private final ActionContributionItem contributionItem;

        private ButtonImpl(ButtonType buttonType) {
            Action action = new Action() {
                @Override
                public void run() {
                    fireEvent(new EmptyClickEvent());
                }
            };

            action.setText(buttonType.label);
            action.setToolTipText(buttonType.label);
            action.setImageDescriptor(buttonType.image);

            contributionItem = new ActionContributionItem(action);
            contributionItem.setVisible(true);
        }

        public IContributionItem getContributionItem() {
            return contributionItem;
        }

        @Override
        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return handlerManager.addHandler(ClickEvent.getType(), handler);
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            handlerManager.fireEvent(event);
        }

        @Override
        public void setEnabled(boolean enabled) {
            contributionItem.getAction().setEnabled(enabled);
        }

        @Override
        public boolean isEnabled() {
            return contributionItem.getAction().isEnabled();
        }
    }
}
