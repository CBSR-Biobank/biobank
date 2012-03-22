package edu.ualberta.med.biobank.i18n;

public enum CommonMessages implements L10nedMessage {
    LIST_ITEM_DELIMITER,
    MEMBERSHIP_SAVE_NOT_ALLOWED,
    MEMBERSHIP_SAVE_ILLEGAL_PERMS_MODIFIED,
    MEMBERSHIP_SAVE_ILLEGAL_ROLES_MODIFIED;

    private final BundleL10nedMessage message;

    private CommonMessages() {
        message = new BundleL10nedMessage(getClass().getName(), name());
    }

    @Override
    public String getMessage() {
        return message.getMessage();
    }
}
