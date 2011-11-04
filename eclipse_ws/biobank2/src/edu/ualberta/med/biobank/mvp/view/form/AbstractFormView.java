package edu.ualberta.med.biobank.mvp.view.form;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.pietschy.gwt.pectin.client.value.ValueHolder;
import com.pietschy.gwt.pectin.client.value.ValueTarget;

import edu.ualberta.med.biobank.mvp.view.AbstractView;
import edu.ualberta.med.biobank.mvp.view.IFormView;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;

public abstract class AbstractFormView extends AbstractView implements
    IFormView, IHasEditor {
    protected final ButtonItem reload = new ButtonItem();
    protected final ButtonItem close = new ButtonItem();
    protected FormViewEditorPart editor;
    private final ValueTarget<Object> identifier = new ValueHolder<Object>();
    private BaseForm baseForm;

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

    public BaseForm getBaseForm() {
        return baseForm;
    }

    @Override
    protected final void onCreate(Composite parent) {
        baseForm = new BaseForm(parent);

        // TODO: some initialisation for actions (close and reload button)

        onCreate(baseForm);

        baseForm.adapt();
        baseForm.reflow(true);
    }

    // TODO: not sure if this is the best way to do this?
    // use more and more specific onCreate methods?
    protected abstract void onCreate(BaseForm baseForm);
}
