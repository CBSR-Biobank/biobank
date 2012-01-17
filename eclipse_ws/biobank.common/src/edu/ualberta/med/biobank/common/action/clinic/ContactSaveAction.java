package edu.ualberta.med.biobank.common.action.clinic;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;

public class ContactSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private String name;
    private String title;
    private String mobileNumber;
    private String officeNumber;
    private String pagerNumber;
    private String faxNumber;
    private String emailAddress;
    private Integer clinicId;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public void setPagerNumber(String pagerNumber) {
        this.pagerNumber = pagerNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // FIXME get correct permissions
        return true;
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        if (clinicId == null) {
            throw new NullPointerException("clinic id cannot be null");
        }

        Contact contact = context.get(Contact.class, id, new Contact());

        contact.setName(name);
        contact.setTitle(title);
        contact.setMobileNumber(mobileNumber);
        contact.setOfficeNumber(officeNumber);
        contact.setPagerNumber(pagerNumber);
        contact.setFaxNumber(faxNumber);
        contact.setEmailAddress(emailAddress);

        Clinic clinic = context.get(Clinic.class, clinicId);
        contact.setClinic(clinic);

        context.getSession().saveOrUpdate(contact);
        context.getSession().flush();

        return new IdResult(contact.getId());
    }

}
