package edu.ualberta.med.biobank.forms.linkassign;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;

public abstract class AbstractLinkAssignEntryForm2 extends
    AbstractSpecimenAdminForm {

    // composite containing common fields to single and multiple
    private Composite commonFieldsComposite;

    private Button radioSingle;
    private Button radioMultiple;

    // Single
    private Composite singleFieldsComposite;
    // when work only with one specimen
    protected SpecimenWrapper singleSpecimen;

    // Multiple
    private Composite multipleFieldsComposite;
    private ScrolledComposite visualisationScroll;
    private Composite visualisationComposite;

    protected CancelConfirmWidget cancelConfirmWidget;

    @Override
    protected void init() throws Exception {
        super.init();
        singleSpecimen = new SpecimenWrapper(appService);
    }

    protected abstract String getFormTitle();

    protected abstract boolean isSingleMode();

    protected abstract void setSingleMode(boolean single);

    @Override
    protected void createFormContent() throws Exception {
        form.setText(getFormTitle());
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        page.setLayout(gl);

        Composite mainComposite = new Composite(page, SWT.NONE);
        gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        mainComposite.setLayout(gl);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.TOP;
        mainComposite.setLayoutData(gd);

        createLeftSection(mainComposite);

        createVisualisationSection(mainComposite);

        radioSingle.setSelection(isSingleMode());
        radioMultiple.setSelection(!isSingleMode());
        showSingleComposite(isSingleMode());

        toolkit.adapt(mainComposite);
    }

    private void createLeftSection(Composite parent) throws Exception {
        Composite leftComposite = toolkit.createComposite(parent);
        GridLayout gl = new GridLayout(1, false);
        gl.marginWidth = 0;
        leftComposite.setLayout(gl);
        toolkit.paintBordersFor(leftComposite);
        GridData gd = new GridData();
        gd.widthHint = getLeftSectionWidth();
        gd.verticalAlignment = SWT.TOP;
        leftComposite.setLayoutData(gd);

        commonFieldsComposite = toolkit.createComposite(leftComposite);
        gl = new GridLayout(3, false);
        gl.horizontalSpacing = 10;
        commonFieldsComposite.setLayout(gl);
        gd = new GridData();
        gd.widthHint = getLeftSectionWidth();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commonFieldsComposite.setLayoutData(gd);
        toolkit.paintBordersFor(commonFieldsComposite);

        createCommonFields(commonFieldsComposite);

        createSingleMultipleSection(leftComposite);

        cancelConfirmWidget = new CancelConfirmWidget(leftComposite, this, true);
    }

    protected int getLeftSectionWidth() {
        return 600;
    }

    protected abstract void createCommonFields(Composite commonFieldsComposite);

    private void createSingleMultipleSection(Composite leftComposite)
        throws Exception {
        Composite singleMultipleComposite = toolkit
            .createComposite(leftComposite);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        singleMultipleComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleMultipleComposite.setLayoutData(gd);
        toolkit.paintBordersFor(singleMultipleComposite);

        // radio button to choose single or multiple
        radioSingle = toolkit.createButton(singleMultipleComposite,
            Messages.getString("GenericLinkEntryForm.choice.radio.single"), //$NON-NLS-1$
            SWT.RADIO);
        radioMultiple = toolkit.createButton(singleMultipleComposite,
            Messages.getString("GenericLinkEntryForm.choice.radio.multiple"), //$NON-NLS-1$
            SWT.RADIO);

        singleFieldsComposite = toolkit.createComposite(leftComposite);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        singleFieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(singleFieldsComposite);
        gd = new GridData();
        gd.widthHint = getLeftSectionWidth();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleFieldsComposite.setLayoutData(gd);
        createSingleFields(singleFieldsComposite);

        multipleFieldsComposite = toolkit.createComposite(leftComposite);
        layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        multipleFieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(multipleFieldsComposite);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleFieldsComposite.setLayoutData(gd);
        createMultipleFields(multipleFieldsComposite);

        radioSingle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioSingle.getSelection()) {
                    showSingleComposite(true);
                }
            }

        });
        radioMultiple.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioMultiple.getSelection()) {
                    showSingleComposite(false);
                }
            }
        });
    }

    /**
     * Show either single or multiple selection fields
     */
    protected void showSingleComposite(boolean single) {
        setSingleMode(single);
        widgetCreator.showWidget(singleFieldsComposite, single);
        widgetCreator.showWidget(multipleFieldsComposite, !single);
        setBindings(single);
        showVisualisation(!single);

        page.layout(true, true);
    }

    protected abstract void setBindings(boolean isSingleMode);

    protected abstract void createSingleFields(Composite parent);

    protected abstract void createMultipleFields(Composite parent)
        throws Exception;

    protected abstract void createContainersVisualisation(Composite parent);

    /**
     * Containers visualisation
     */
    private void createVisualisationSection(Composite parent) {
        visualisationScroll = new ScrolledComposite(parent, SWT.H_SCROLL);
        visualisationScroll.setExpandHorizontal(true);
        visualisationScroll.setExpandVertical(true);
        visualisationScroll.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        visualisationScroll.setLayoutData(scrollData);
        visualisationComposite = toolkit.createComposite(visualisationScroll);
        GridLayout layout = new GridLayout(1, false);
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginLeft = 20;
        visualisationComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        visualisationComposite.setLayoutData(gd);
        visualisationScroll.setContent(visualisationComposite);

        createContainersVisualisation(visualisationComposite);
    }

    protected void showVisualisation(boolean show) {
        if (visualisationScroll != null) {
            widgetCreator.showWidget(visualisationScroll, show);
            visualisationScroll.setMinSize(visualisationComposite.computeSize(
                SWT.DEFAULT, SWT.DEFAULT));
        }
    }

    // @Override
    // protected void disableFields() {
    // enableFields(false);
    // }

    private void enableFields(boolean enable) {
        commonFieldsComposite.setEnabled(enable);
        radioSingle.setEnabled(enable);
        radioMultiple.setEnabled(enable);
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        enableFields(true);
        singleSpecimen.reset(); // reset internal values
        setDirty(false);
        setFocus();
        reset(true);
    }

    public void reset(boolean resetAll) {
        cancelConfirmWidget.reset();
        // removeRescanMode();
        // setScanHasBeenLauched(isSingleMode());
        // if (resetAll) {
        // resetPlateToScan();
        // }
        setFocus();
    }

    // @Override
    // /**
    // * Multiple linking: do this before multiple scan is made
    // */
    // protected void beforeScanThreadStart() {
    // isFakeScanRandom = fakeScanRandom != null
    // && fakeScanRandom.getSelection();
    // }

    // @Override
    // /**
    // * Multiple linking: do this before scan of one tube is really made
    // */
    // protected void beforeScanTubeAlone() {
    // isFakeScanRandom = fakeScanRandom != null
    // && fakeScanRandom.getSelection();
    // }

}
