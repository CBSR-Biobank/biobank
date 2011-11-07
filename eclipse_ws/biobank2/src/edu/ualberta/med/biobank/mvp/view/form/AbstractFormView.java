package edu.ualberta.med.biobank.mvp.view.form;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.pietschy.gwt.pectin.client.value.ValueHolder;
import com.pietschy.gwt.pectin.client.value.ValueTarget;

import edu.ualberta.med.biobank.mvp.user.ui.IButton;
import edu.ualberta.med.biobank.mvp.view.AbstractView;
import edu.ualberta.med.biobank.mvp.view.IFormView;
import edu.ualberta.med.biobank.mvp.view.form.ToolBarButtonManager.ButtonType;
import edu.ualberta.med.biobank.mvp.view.widget.DelegatingButton;

public abstract class AbstractFormView extends AbstractView implements
    IFormView, IHasEditor {
    protected final DelegatingButton reload = new DelegatingButton();
    protected final DelegatingButton close = new DelegatingButton();
    protected FormViewEditorPart editor;
    private final ValueTarget<Object> identifier = new ValueHolder<Object>();
    private BaseForm form;

    @Override
    public void setEditor(FormViewEditorPart editor) {
        this.editor = editor;
    }

    @Override
    public void close() {
        if (editor != null) {
            IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
            page.closeEditor(editor, true);
            editor = null;
        }
    }

    @Override
    public HasClickHandlers getClose() {
        return close;
    }

    @Override
    public HasClickHandlers getReload() {
        return reload;
    }

    @Override
    public ValueTarget<Object> getIdentifier() {
        return identifier;
    }

    public BaseForm getForm() {
        return form;
    }

    @Override
    protected final void onCreate(Composite parent) {
        form = new BaseForm(parent);

        initActions();

        onCreate(form);

        form.adapt();
        form.reflow(true);
    }

    protected abstract void onCreate(BaseForm baseForm);

    private void initActions() {
        IButton closeButton = form.getToolbar().get(ButtonType.CLOSE);
        IButton reloadButton = form.getToolbar().get(ButtonType.RELOAD);

        close.setDelegate(closeButton);
        reload.setDelegate(reloadButton);
    }
}
