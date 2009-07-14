package edu.ualberta.med.biobank.forms.listener;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.CancelConfirmForm;

/**
 * KeyListener for cancel/confirm field used with the barcode scanner
 */
public class CancelConfirmKeyListener implements KeyListener {

	public CancelConfirmForm form;

	public CancelConfirmKeyListener(CancelConfirmForm form) {
		this.form = form;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == 13) {
			String text = ((Text) e.widget).getText();
			try {
				if (BioBankPlugin.getDefault().isConfirmBarcode(text)
						&& form.isConfirmEnabled()) {
					form.confirm();
				} else if (BioBankPlugin.getDefault().isCancelBarcode(text)) {
					form.cancel();
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
