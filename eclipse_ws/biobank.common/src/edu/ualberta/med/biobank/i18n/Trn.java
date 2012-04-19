package edu.ualberta.med.biobank.i18n;

public class Trn extends Template {
    private static final long serialVersionUID = 1L;

    private final String text;
    private final String pluralText;

    public Trn(Bundle bundle, String text, String pluralText) {
        super(bundle, text, pluralText);
        this.text = text;
        this.pluralText = pluralText;
    }

    public LString format(final int n, final Object... objects) {
        return new LString(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getString() {
                return bundle.i18n.trn(text, pluralText, n, objects);
            }
        };
    }
}