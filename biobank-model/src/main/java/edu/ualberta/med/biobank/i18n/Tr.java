package edu.ualberta.med.biobank.i18n;

public class Tr extends Template {
    private static final long serialVersionUID = 1L;

    private final String text;

    public Tr(Bundle bundle, String text) {
        super(bundle, text);
        this.text = text;
    }

    public LString format(final Object... objects) {
        return new LString(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getString() {
                return bundle.i18n.tr(text, objects);
            }
        };
    }
}
