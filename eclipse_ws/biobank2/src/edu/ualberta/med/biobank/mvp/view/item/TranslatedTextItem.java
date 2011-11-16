package edu.ualberta.med.biobank.mvp.view.item;

import org.eclipse.swt.widgets.Text;

public class TranslatedTextItem<T> extends TranslatedItem<T, String> {
    private final TextItem textItem;

    public TranslatedTextItem(Translator<T, String> translator) {
        this(new TextItem(), translator);
    }

    private TranslatedTextItem(TextItem textItem,
        Translator<T, String> translator) {
        super(textItem, translator);

        this.textItem = textItem;
    }

    public void setText(Text text) {
        textItem.setText(text);
    }
}
