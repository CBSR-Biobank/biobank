package edu.ualberta.med.biobank.rcp;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class ServerMsgStatusItem extends ContributionItem {

    private static ServerMsgStatusItem instance = null;

    /**
     * The composite into which this contribution item has been placed. This
     * will be <code>null</code> if this instance has not yet been initialized.
     */
    private Composite statusLine = null;

    private CLabel label;

    private String text = ""; //$NON-NLS-1$

    private int widthHint = -1;

    private int heightHint = -1;

    private ServerMsgStatusItem() {
        super("biobank.serverMsg"); //$NON-NLS-1$
    }

    public static ServerMsgStatusItem getInstance() {
        if (instance == null) {
            instance = new ServerMsgStatusItem();
        }

        return instance;
    }

    @Override
    public void fill(Composite parent) {
        statusLine = parent;

        Label sep = new Label(parent, SWT.SEPARATOR);
        label = new CLabel(statusLine, SWT.BORDER | SWT.SHADOW_NONE);
        setLabelText(this.text);

        Point preferredSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        widthHint = preferredSize.x;
        heightHint = preferredSize.y;

        StatusLineLayoutData data = new StatusLineLayoutData();
        data.widthHint = widthHint;
        label.setLayoutData(data);

        data = new StatusLineLayoutData();
        data.heightHint = heightHint;
        sep.setLayoutData(data);
    }

    public void setServerName(String text) {
        Assert.isNotNull(text);

        this.text = LegacyActionTools.escapeMnemonics(text);
        setLabelText(this.text);

        if (this.text.length() == 0) {
            if (isVisible()) {
                setVisible(false);
                IContributionManager contributionManager = getParent();

                if (contributionManager != null) {
                    contributionManager.update(true);
                }
            }
        } else if (!isVisible()) {
            setVisible(true);
            IContributionManager contributionManager = getParent();

            if (contributionManager != null) {
                contributionManager.update(true);
            }
        }
    }

    private void setLabelText(String text) {
        if ((label == null) || label.isDisposed()) {
            return;
        }

        label.setText(this.text);

        if ((text != null) && !text.endsWith("@cbsr.med.ualberta.ca")) { //$NON-NLS-1$
            label.setBackground(PlatformUI.getWorkbench().getDisplay()
                .getSystemColor(SWT.COLOR_YELLOW));
        }

    }

}
