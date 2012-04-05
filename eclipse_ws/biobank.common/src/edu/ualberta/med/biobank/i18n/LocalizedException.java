package edu.ualberta.med.biobank.i18n;

public class LocalizedException extends Exception
    implements HasLocalizedMessage {
    private static final long serialVersionUID = 1L;

    private final TI18n msg;

    public LocalizedException(TI18n msg) {
        super(msg.getMsg());

        this.msg = msg;
    }

    @Override
    public String getLocalizedMessage() {
        return msg.getMsg();
    }
}
