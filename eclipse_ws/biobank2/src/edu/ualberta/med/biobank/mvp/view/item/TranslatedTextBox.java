package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.widgets.Text;

public class TranslatedTextBox<T> extends AdaptedValueField<T, String> {
    private final TextBox textItem;

    public TranslatedTextBox(Adapter<T, String> adapter) {
        this(new TextBox(), adapter);
    }

    private TranslatedTextBox(TextBox textItem, Adapter<T, String> adapter) {
        super(textItem, adapter);

        this.textItem = textItem;
    }

    public void setText(Text text) {
        textItem.setText(text);
    }
}
