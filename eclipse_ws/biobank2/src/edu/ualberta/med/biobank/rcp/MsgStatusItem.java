package edu.ualberta.med.biobank.rcp;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MsgStatusItem extends ContributionItem {

    private CLabel label;

    private String text = ""; //$NON-NLS-1$

    private int widthHint = -1;

    private int heightHint = -1;

    private Image icon;

    public MsgStatusItem(String id) {
        super(id);
    }

    @Override
    public void fill(Composite parent) {
        Label sep = new Label(parent, SWT.SEPARATOR);
        label = new CLabel(parent, SWT.BORDER | SWT.SHADOW_NONE);
        setLabelText(text);
        setLabelIcon(icon);

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

    public void setText(String text) {
        Assert.isNotNull(text);

        this.text = LegacyActionTools.escapeMnemonics(text);
        setLabelText(this.text);

        if (this.text.length() == 0) {
            if (isVisible()) {
                setVisible(false);
            }
        } else if (!isVisible()) {
            setVisible(true);
        }
    }

    public void setIcon(Image icon) {
        this.icon = icon;
        setLabelIcon(icon);
    }

    public void setLabelIcon(Image image) {
        if ((label == null) || label.isDisposed()) {
            return;
        }
        label.setImage(image);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        IContributionManager contributionManager = getParent();
        if (contributionManager != null) {
            contributionManager.update(true);
        }
    }

    private void setLabelText(String text) {
        if ((label == null) || label.isDisposed()) {
            return;
        }

        label.setText(this.text);

        if (text != null) {
            Color color = getBackgroundColor(text);
            if (color != null)
                label.setBackground(color);
        }
    }

    @SuppressWarnings("unused")
    public Color getBackgroundColor(String text) {
        return null;
    }

}
