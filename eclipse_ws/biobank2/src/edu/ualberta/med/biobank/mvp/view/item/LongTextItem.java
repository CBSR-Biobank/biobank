package edu.ualberta.med.biobank.mvp.view.item;

public class LongTextItem extends TranslatedTextItem<Long> {
    private static final Translator<Long, String> TRANSLATOR =
        new Translator<Long, String>() {
            @Override
            public Long fromDelegate(String delegateValue) {
                return Long.parseLong(delegateValue);
            }

            @Override
            public String toDelegate(Long value) {
                return value.toString();
            }
        };

    public LongTextItem() {
        super(TRANSLATOR);
    }
}
