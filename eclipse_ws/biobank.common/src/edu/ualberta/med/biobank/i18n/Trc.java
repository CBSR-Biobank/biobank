package edu.ualberta.med.biobank.i18n;

public class Trc extends Template {
    private static final long serialVersionUID = 1L;

    private final String context;
    private final String text;

    public Trc(Bundle bundle, String context, String text) {
        super(bundle, context, text);
        this.context = context;
        this.text = text;
    }

    public LString format() {
        return new LString(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getString() {
                return bundle.i18n.trc(context, text);
            }
        };
    }
}
