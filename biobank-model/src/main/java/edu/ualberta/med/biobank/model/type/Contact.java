package edu.ualberta.med.biobank.model.type;

import javax.persistence.Embeddable;

@Embeddable
public class Contact {
    private String title;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String faxNumber;
    private String pagerNumber;
    private String officeNumber;
    private Location location;
}
