package edu.ualberta.med.biobank.mvp.view;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.form.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.form.validation.message.ValidationMessage;

import edu.ualberta.med.biobank.common.action.site.SiteGetStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;

public class SiteEntryView implements SiteEntryPresenter.View {
    private SiteEntryForm widget;

    private final ButtonItem save = new ButtonItem();
    private final ButtonItem reload = new ButtonItem();
    private final ButtonItem close = new ButtonItem();
    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private final TextItem comment = new TextItem();
    private final TableItem<List<StudyInfo>> studies = new TableItem<List<StudyInfo>>();

    private IView addressEntryView;
    private IView activityStatusComboView;

    @Override
    public HasButton getSave() {
        return save;
    }

    @Override
    public HasValue<String> getName() {
        return name;
    }

    @Override
    public HasValue<String> getNameShort() {
        return nameShort;
    }

    @Override
    public HasValue<String> getComment() {
        return comment;
    }

    @Override
    public HasValue<Collection<StudyInfo>> getStudies() {
        return null;
    }

    @Override
    public void close() {
        widget.getParent().dispose();
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
    public void create(Composite parent) {
        widget = new SiteEntryForm(parent, SWT.NONE);

        // TODO: make a BaseForm, get an attach point to make the text stuff in
        // and then we don't need an inner class. that's pointless.

        name.setValidationControl(widget.nameLabel);
        name.setText(widget.name);
        nameShort.setValidationControl(widget.nameShortLabel);
        nameShort.setText(widget.nameShort);
        comment.setText(widget.comment);
        save.setButton(widget.save);
        reload.setButton(widget.reload);
    }

    @Override
    public void setActivityStatusComboView(IView view) {
        this.activityStatusComboView = view;
    }

    // TODO: move out
    public static class BaseForm extends Composite {
        private static final String PAGE_KEY = "page"; //$NON-NLS-1$
        protected final ManagedForm managedForm;
        protected final ScrolledForm form;
        protected final FormToolkit toolkit;
        protected final ScrolledPageBook book;
        protected final Composite page;

        public BaseForm(Composite parent, int style) {
            super(parent, style);

            managedForm = new ManagedForm(parent);
            toolkit = managedForm.getToolkit();
            form = managedForm.getForm();
            toolkit.decorateFormHeading(form.getForm());

            form.getBody().setLayout(new GridLayout());
            GridData gd = new GridData();
            gd.grabExcessHorizontalSpace = true;
            gd.grabExcessVerticalSpace = true;
            gd.horizontalAlignment = SWT.FILL;
            gd.verticalAlignment = SWT.FILL;
            form.getBody().setLayoutData(gd);

            book = toolkit.createPageBook(form.getBody(), SWT.V_SCROLL);
            book.setLayout(new GridLayout());
            book.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
                true));
            page = book.createPage(PAGE_KEY);
            book.showPage(PAGE_KEY);
            //
            // // start a new runnable so that database objects are populated in
            // a
            // // separate thread.
            // BusyIndicator.showWhile(parent.getDisplay(), new Runnable() {
            // @Override
            // public void run() {
            // try {
            // form.setImage(getFormImage());
            // createFormContent();
            // form.reflow(true);
            // } catch (final RemoteConnectFailureException exp) {
            // BgcPlugin.openRemoteConnectErrorMessage(exp);
            // } catch (Exception e) {
            // BgcPlugin.openError(
            //                            "BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$
            // }
            // }
            // });
        }

        protected Section createSection(String title, Composite parent,
            int style) {
            Section section = toolkit.createSection(parent, style);
            if (title != null) {
                section.setText(title);
            }
            section.setLayout(new GridLayout(1, false));
            section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            section.addExpansionListener(new ExpansionAdapter() {
                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    form.reflow(false);
                }
            });
            return section;
        }

        protected Section createSection(String title, Composite parent) {
            return createSection(title, parent, Section.TWISTIE
                | Section.TITLE_BAR | Section.EXPANDED);
        }

        protected Section createSection(String title) {
            return createSection(title, page);
        }

        protected Composite sectionAddClient(Section section) {
            Composite client = toolkit.createComposite(section);
            section.setClient(client);
            client.setLayout(new GridLayout(2, false));
            toolkit.paintBordersFor(client);
            return client;
        }

        protected Composite createSectionWithClient(String title) {
            return sectionAddClient(createSection(title, page));
        }
    }

    private class SiteEntryForm extends BaseForm {
        public final Text name;
        public final Label nameLabel;
        public final Text nameShort;
        public final Label nameShortLabel;
        public final Text comment;
        public final Button save;
        public final Button reload;

        public SiteEntryForm(Composite parent, int style) {
            super(parent, style);

            form.setText(Messages.StudyEntryForm_main_title);
            form.setMessage("asdfadsf", IMessageProvider.NONE);
            page.setLayout(new GridLayout(1, false));

            Composite client = toolkit.createComposite(page);
            GridLayout layout = new GridLayout(2, false);
            layout.horizontalSpacing = 10;
            client.setLayout(layout);
            client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            toolkit.paintBordersFor(client);

            nameLabel = new Label(client, SWT.NONE);
            nameLabel.setText("name");
            name = new Text(client, SWT.BORDER);
            nameShortLabel = new Label(client, SWT.NONE);
            nameShortLabel.setText("nameShort");
            nameShort = new Text(client, SWT.BORDER);
            new Label(client, SWT.NONE).setText("comment");
            comment = new Text(client, SWT.BORDER);

            Composite addressSection = createSectionWithClient("Address");
            addressEntryView.create(addressSection);

            new Label(client, SWT.NONE).setText("activityStatus");
            activityStatusComboView.create(client);

            save = new Button(client, SWT.NONE);
            save.setText("save");
            reload = new Button(client, SWT.NONE);
            reload.setText("reload");

            form.reflow(true);
        }
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        System.out.println(new Date());
        for (ValidationMessage message : result.getMessages()) {
            System.out.println(message.getMessage());
        }
    }

    @Override
    public void setAddressEditView(IView view) {
        this.addressEntryView = view;
    }
}
