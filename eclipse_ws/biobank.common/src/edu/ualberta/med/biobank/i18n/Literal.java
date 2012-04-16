package edu.ualberta.med.biobank.i18n;

class Literal extends AbstractLocalizable {
    private static final long serialVersionUID = 1L;

    private final String literal;

    protected Literal(String literal) {
        super(literal);

        this.literal = literal;
    }

    @Override
    public String getString() {
        return literal;
    }
}
