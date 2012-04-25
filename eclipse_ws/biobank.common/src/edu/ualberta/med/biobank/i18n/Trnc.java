package edu.ualberta.med.biobank.i18n;

public class Trnc extends Template {
    private static final long serialVersionUID = 1L;

    private final String context;
    private final String text;
    private final String pluralText;

    public Trnc(Bundle bundle, String context, String text, String pluralText) {
        super(bundle, context, text, pluralText);
        this.context = context;
        this.text = text;
        this.pluralText = pluralText;
    }

    public LString format(final int n, final Object... objects) {
        return new LString(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getString() {
                return bundle.i18n.trnc(context, text, pluralText, n, objects);
            }
        };
    }

    public LString singular(final Object... objects) {
        return new LString(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getString() {
                return bundle.i18n.trnc(context, text, pluralText, 1, objects);
            }
        };
    }

    public LString plural(final Object... objects) {
        return new LString(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getString() {
                return bundle.i18n.trnc(context, text, pluralText, 2, objects);
            }
        };
    }
}