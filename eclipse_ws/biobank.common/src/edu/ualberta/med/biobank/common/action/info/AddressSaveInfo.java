package edu.ualberta.med.biobank.common.action.info;


import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.Address;


public class AddressSaveInfo implements ActionResult{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public Integer id;
    public String street1;
    public String street2;
    public String city;
    public String province;
    public String postalCode;
    public String email;
    public String phone;
    public String fax;
    public String country;

    public AddressSaveInfo(Integer id,
                           String street1,
                           String street2,
                           String city,
                           String province,
                           String postalCode,
                           String email,
                           String phone,
                           String fax,
                           String country) {
        this.id = id;
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.country = country;
    }

    public static AddressSaveInfo createFromAddress(Address address) {
        return new AddressSaveInfo(address.getId(),
                                   address.getStreet1(),
                                   address.getStreet2(),
                                   address.getCity(),
                                   address.getProvince(),
                                   address.getPostalCode(),
                                   address.getEmailAddress(),
                                   address.getPhoneNumber(),
                                   address.getFaxNumber(),
                                   address.getCountry());
    }

}
