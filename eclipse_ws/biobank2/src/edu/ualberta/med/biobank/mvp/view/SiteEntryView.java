package edu.ualberta.med.biobank.mvp.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.site.SiteGetStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.IButton;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem;
import edu.ualberta.med.biobank.mvp.view.item.TranslatedItem.Translator;
import edu.ualberta.med.biobank.widgets.infotables.entry.StudyAddInfoTable;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * 
 * @author jferland
 * 
 */
public class SiteEntryView implements SiteEntryPresenter.View {
    private SiteEntryForm widget;

    private final ButtonItem save = new ButtonItem();
    private final ButtonItem reload = new ButtonItem();
    private final ButtonItem close = new ButtonItem();
    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private final TextItem comment = new TextItem();
    private final HasValue<Collection<Comment>> comments =
        new TableItem<Comment>();
    private final TableItem<StudyWrapper> studyWrappers =
        new TableItem<StudyWrapper>();
    private final TranslatedItem<Collection<StudyInfo>, Collection<StudyWrapper>> studies =
        TranslatedItem.from(studyWrappers, STUDY_TRANSLATOR);

    private IView addressEntryView;
    private IView activityStatusComboView;

    private static final StudyTranslator STUDY_TRANSLATOR =
        new StudyTranslator();

    private static class StudyTranslator implements
        Translator<Collection<StudyInfo>, Collection<StudyWrapper>> {
        @Override
        public Collection<StudyInfo> fromDelegate(
            Collection<StudyWrapper> delegate) {
            Collection<StudyInfo> studies = new ArrayList<StudyInfo>();
            for (StudyWrapper study : delegate) {
                StudyInfo studyInfo =
                    new StudyInfo(study.getWrappedObject(), -1l, -1l);
                studies.add(studyInfo);
            }
            return studies;
        }

        @Override
        public Collection<StudyWrapper> toDelegate(Collection<StudyInfo> foreign) {
            Collection<StudyWrapper> studies = new ArrayList<StudyWrapper>();
            for (StudyInfo study : foreign) {
                WritableApplicationService appService =
                    SessionManager.getAppService();
                StudyWrapper wrapper =
                    new StudyWrapper(appService, study.getStudy());
                studies.add(wrapper);
            }
            return studies;
        }
    }

    @Override
    public void setActivityStatusComboView(IView view) {
        this.activityStatusComboView = view;
    }

    @Override
    public void setAddressEditView(IView view) {
        this.addressEntryView = view;
    }

    @Override
    public IButton getSave() {
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
    public HasValue<Collection<Comment>> getCommentCollection() {
        return comments;
    }

    @Override
    public HasValue<Collection<StudyInfo>> getStudies() {
        return studies;
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

        // TODO: fix comment section
        // comment.setText(widget.comment);

        save.setButton(widget.save);
        reload.setButton(widget.reload);
        studyWrappers.setTable(widget.studiesTable);
    }

    // TODO: move out
    public static class BaseForm extends Composite {
        private static final String PAGE_KEY = "page"; //$NON-NLS-1$
        protected final ManagedForm managedForm;
        protected final ScrolledForm form;
        protected final FormToolkit toolkit;
        protected final ScrolledPageBook book;
        protected final Composite page;
        private final SectionExpansionAdapter sectionExpansionAdapter =
            new SectionExpansionAdapter();

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

        private Section createSection(String title, Composite parent, int style) {
            Section section = toolkit.createSection(parent, style);

            if (title != null) section.setText(title);

            section.setLayout(new GridLayout(1, false));
            section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            section.addExpansionListener(sectionExpansionAdapter);

            return section;
        }

        protected Composite createSectionWithClient(String title) {
            Section section = createSection(title);

            Composite client = toolkit.createComposite(section);
            client.setLayout(new GridLayout(1, false));
            section.setClient(client);
            toolkit.paintBordersFor(client);

            return client;
        }

        protected Section createSection(String title, Composite parent) {
            return createSection(title, parent, Section.TWISTIE
                | Section.TITLE_BAR | Section.EXPANDED);
        }

        protected Section createSection(String title) {
            return createSection(title, page);
        }

        public void adaptToToolkit(FormToolkit toolkit, boolean paintBorder) {
            toolkit.adapt(this, true, true);
            adaptAllChildren(this, toolkit);
            if (paintBorder) {
                toolkit.paintBordersFor(this);
            }
        }

        private void adaptAllChildren(Composite container, FormToolkit toolkit) {
            Control[] children = container.getChildren();
            for (Control child : children) {
                toolkit.adapt(child, true, true);
                if (child instanceof Composite) {
                    adaptAllChildren((Composite) child, toolkit);
                }
            }
        }

        protected static void addSectionToolbar(Section section,
            String tooltip, SelectionListener listener,
            Class<?> wrapperTypeToAdd, String imageKey) {
            if (wrapperTypeToAdd == null
                || SessionManager.canCreate(wrapperTypeToAdd)) {
                ToolBar tbar = (ToolBar) section.getTextClient();
                if (tbar == null) {
                    tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
                    section.setTextClient(tbar);
                }

                ToolItem titem = new ToolItem(tbar, SWT.NULL);
                if (imageKey == null) {
                    imageKey = BgcPlugin.IMG_ADD;
                }
                titem.setImage(BgcPlugin.getDefault().getImageRegistry()
                    .get(imageKey));
                titem.setToolTipText(tooltip);
                titem.addSelectionListener(listener);
            }
        }

        private class SectionExpansionAdapter extends ExpansionAdapter {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(false);
            }
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
        public final StudyAddInfoTable studiesTable;

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

            Section studySection = createSection("Studies");
            boolean superAdmin = SessionManager.getUser().isSuperAdmin();
            if (superAdmin) {
                BaseForm.addSectionToolbar(studySection,
                    Messages.SiteEntryForm_studies_add, new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            studiesTable.createStudyDlg();
                        }
                    }, ContactWrapper.class, null);
            }
            WritableApplicationService appService =
                SessionManager.getAppService();
            SiteWrapper siteWrapper = new SiteWrapper(appService);
            studiesTable =
                new StudyAddInfoTable(studySection, siteWrapper, superAdmin);
            studySection.setClient(studiesTable);

            save = new Button(client, SWT.NONE);
            save.setText("save");
            reload = new Button(client, SWT.NONE);
            reload.setText("reload");

            form.reflow(true);

            adaptToToolkit(toolkit, true);
        }
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        System.out.println(new Date());
        for (ValidationMessage message : result.getMessages()) {
            System.out.println(message.getMessage());
        }
    }
}
