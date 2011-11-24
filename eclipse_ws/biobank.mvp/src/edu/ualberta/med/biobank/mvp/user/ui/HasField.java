package edu.ualberta.med.biobank.mvp.user.ui;

import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.form.metadata.HasEnabled;
import com.pietschy.gwt.pectin.client.form.metadata.HasVisible;

public interface HasField<T> extends HasValue<T>, HasEnabled, HasVisible {
}
