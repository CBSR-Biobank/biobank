
package edu.ualberta.med.biobank.importer;

import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SiteClinics {

    private static Map<String, ClinicWrapper> clinicsMap = null;

    private static Map<String, ContactWrapper> contactsMap = null;

    public static void createClinics(SiteWrapper site) throws Exception {
        clinicsMap = new HashMap<String, ClinicWrapper>();
        contactsMap = new HashMap<String, ContactWrapper>();

        addClinic(site, "CL1", null, "Foothills Medical Centre",
            "1403 29 Street", "Calgary", "Alberta", "t2n2t9");
        addContact(clinicsMap.get("CL1"), "Morna Brown", null, "403-944-4057",
            "403-944-1745", "morna.brown@calgaryhealthregion.ca");

        addClinic(site, "CL1-Charlynn", "Charlynn Ursu",
            "715 Fort Alice Cresecent SE", null, "Calgary", "Alberta", "T2A2C8");
        addContact(clinicsMap.get("CL1-Charlynn"), "Charlynn Ursu", null,
            "403-944-9883", null, "charlynn.ursu@calgaryhealthservices.ca");

        addClinic(site, "CL1-NHS", "Shirley Cole",
            "Heritage Medical Research Clinic",
            "Suite 1140, 3350 Hospital Drive", "Calgary", "Alberta", "T2N4N1");
        addContact(clinicsMap.get("CL1-NHS"), "Shirley Cole", null,
            "403-220-4988", null, "coles@ualberta.ca");

        addClinic(site, "CL1-Sharon", "Sharon Gulewich",
            "3820 Marlborough Drive NE", null, "Calgary", "Alberta", "T2A4L1");
        addContact(clinicsMap.get("CL1-Sharon"), "Sharon Gulewich", null,
            "403-944-9882", "403-944-9905",
            "sharon.gulewich@calgaryhealthregion.ca");

        addClinic(site, "CL2", null, "Alberta Children's Hospital",
            "2888 Shaganappi Trail NW", "Calgary", "Alberta", "t3b6a8");

        addClinic(site, "ED1", null, "University of Alberta Hospital", null,
            "Edmonton", "Alberta", "T6G2B7");
        addContact(clinicsMap.get("ED1"), "Melanie Peters", null,
            "780-407-6588", null, "melaniepeters@cha.ab.ca");
        addContact(clinicsMap.get("ED1"), "Candace Dando", "Research Nurse",
            "780-721-8013", "780-445-7324 (pager)",
            "candace.dando@capitalhealth.ca");
        addContact(clinicsMap.get("ED1"), "Dawn Opgenorth",
            "STudy Coordinator", "780-407-1543", "780-445-7621 (pager)",
            "dawno@ualberta.ca");
        addContact(clinicsMap.get("ED1"), "Dr. Andrew Mason",
            "Principle Investigator", "780-492-8172", null,
            "andrew.mason@ualberta.ca");

        addClinic(site, "FM1", "Philo King", "124 Beardsley Crescent", null,
            "Fort McMurray", "Alberta", "T9H2S2");

        addClinic(site, "GP1", "Sharon Mollins", "Renal Dialysis 2W",
            "10409-98 Street", "Grande Prairie", "Alberta", "T8V0E2");

        addClinic(site, "HL1", null, "QE11 Health Sciences Centre",
            "5788 University Avenue", "Halifax", "Nova Scotia", "B3H1V8");

        addClinic(site, "HL2", null, "IWK Health Centre",
            "5850 University Ave PO Box 9700", "Halifax", "Nova Scotia",
            "B3K6R8");
        addContact(clinicsMap.get("HL2"), "Aleasha Warner",
            "Research Coordinator", "902-470-7414", "902-470-7456",
            "aleasha.warner@iwk.nshealth.ca");

        addClinic(site, "HM1", null, "McMaster University Medical Centre",
            "1200 Main street West", "Hamilton", "Ontario", "L8N3Z5");

        addClinic(site, "KN1", null, "Kingston Cancer Centre",
            "25 King Street", "Kingston", "Ontario", "K7L5P9");

        addClinic(site, "LM1", "Janilee Dow", "3820 43 Avenue", "Room 307",
            "Lloydminister", "Saskatchewan", "S9V1Y5");

        addClinic(site, "LN1", null, "St Joseph's Health Centre",
            "800 Comissioners Road East", "London", "Ontario", "N6A4V2");

        addClinic(site, "MC1", null, "Moncton Hospital", "135 MacBeath",
            "Moncton", "New Brunswick", "E1C6Z8");
        addContact(clinicsMap.get("MC1"), "Dorine Belliveau", null,
            "506-857-5465", null, "dobelliv2@serha.ca");

        addClinic(site, "MN1", null, "Hopital Ste-Justine",
            "3175 Cote Ste-Catherine", "Montreal", "Quebec", "H3T1C5");
        addContact(clinicsMap.get("MN1"), "Elaine Gloutnez", null,
            "514-345-4931 x6483", null, "elaine.gloutnez.hsj@ssss.gouv.qc.ca");

        addClinic(site, "MN2", null, "Montreal Children's Hospital",
            "2300 rue Tupper", "Montreal", "Quebec", "H3H1P3");

        addClinic(site, "OL1", "Jodie Hingst", "5123 42 Street", null, "Olds",
            "Alberta", "T4H1X1");

        addClinic(site, "OT1", null, "Ottawa Hospital", "501 Smyth Road",
            "Ottawa", "Ontario", "K1H8L6");

        addClinic(site, "OT2", null, "Children's Hospital of Eastern Ontario",
            "401 Smyth Road", "Ottawa", "Ontario", "K1G4X3");

        addClinic(site, "QB1", null, "CHA Hopital Enfant-Jesus",
            "1401 18e Rue", "Quebec City", "Quebec", "G1J1Z4");
        addContact(clinicsMap.get("QB1"), "Chantal Gagne", "Research Nurse",
            "418-649-0252 x3115", "418-649-5956",
            "chantal.gagne.recherche.cha@ssss.gouv.qc.ca");

        addClinic(site, "RD1", "Gwen Winter", "Red Deer Regional Hospital",
            "Room 120, 3942 50A Avenue", "Red Deer", "Alberta", "T4N6R2");

        addClinic(site, "SB1", null, "Saint Johns Regional Hospital",
            "400 University Avenue", "St John", "New Brunswick", "E2L4L2");

        addClinic(site, "SD1", null, "Sudbury Regional Hospital",
            "41 Ramsey Lake Road", "Sudbury", "Ontario", "P3E5J1");
        addContact(clinicsMap.get("SD1"), "Elizabeth-Ann Paradis", null,
            "705-522-2200 x3264", null, "eparadis@hrsrh.on.ca");

        addClinic(site, "SF1", "", "Health Science Centre, Eastern Health",
            "300 Prince Philip Drive, Room 4304D", "St John's",
            "Newfoundland and Labrador", "A1B3V6");
        addContact(clinicsMap.get("SF1"), "Daisy Gibbons", "Research Nurse",
            "709-777-6508", "709-777-7622", "daisy.gibbons@easternhealth.ca");

        addClinic(site, "SP1", "Stacey Culp", "St Therese Hospital",
            "4713 48 Avenue, PO Box 880", "St Paul", "Alberta", "T0A3A3");

        addClinic(site, "SS1", null, "Royal University Hospital",
            "103 Hospital Drive", "Saskatoon", "Alberta", "S7N0W8");
        addContact(clinicsMap.get("SS1"), "Dianne Dufour", null,
            "306-966-7962", null, "diannedufour@saskatoonhealthregion.ca");

        addClinic(site, "TH1", null, "Thunder Bay Regional Hospital",
            "980 Oliver Road", "Thunder Bay", "Ontario", "P7B6V4");

        addClinic(site, "VN1", "", "St Paul's Hospital",
            "1081 Burrard Street, Room 318 Comox Building", "Vancouver",
            "British Columbia", "V6Z1Y6");
        addContact(clinicsMap.get("VN1"), "Ann Chala", "Research Coordinator",
            "604-682-2344 x63135", "604-806-8856",
            "achala@providencehealth.bc.ca");

        addClinic(site, "VN2", null, "Women's and Children's Health Centre",
            "4480 Oak Street", "Vancouver", "British Columbia", "V6H3V4");
        addContact(clinicsMap.get("VN2"), "Colleen Fitzgerald", null,
            "604-875-2000 x7277", null, "cfitzgerald@cw.bc.ca");

        addClinic(site, "WL1", "Cathy Lent", "Westlock Health Care Centre",
            "Clinical Laboratory, 10020 93 Street", "Westlock", "Alberta",
            "T7P2G4");
        addContact(clinicsMap.get("WL1"), "Cathy Lent", "Clinic Director",
            "780-350-2025", "780-349-5922", "cathy.lent@aspenha.ab.ca");

        addClinic(site, "WN1", null, "Cancer Care Manitoba",
            "675 McDermot Avenue", "Winnipeg", "Manitoba", "R3E0V9");
    }

    private static ClinicWrapper addClinic(SiteWrapper site, String name,
        String comment, String street1, String street2, String city,
        String province, String postalCode) throws Exception {
        ClinicWrapper clinic = new ClinicWrapper(site.getAppService());
        clinic.setSite(site);
        clinic.setName(name);
        clinic.setComment(comment);
        clinic.setStreet1(street1);
        clinic.setStreet2(street2);
        clinic.setCity(city);
        clinic.setProvince(province);
        clinic.setPostalCode(postalCode);
        clinic.persist();
        clinic.reload();
        clinicsMap.put(name, clinic);
        System.out.println("Added clinic " + name);
        return clinic;
    }

    public static ClinicWrapper getClinic(String name) throws Exception {
        ClinicWrapper clinic = clinicsMap.get(name);
        if (clinic == null) {
            throw new Exception("clinic with name \"" + name
                + "\" does not exist");
        }
        return clinic;
    }

    private static ContactWrapper addContact(ClinicWrapper clinic, String name,
        String title, String phoneNumber, String faxNumber, String emailAddress)
        throws Exception {
        ContactWrapper contact = new ContactWrapper(clinic.getAppService());
        contact.setClinic(clinic);
        contact.setName(name);
        contact.setTitle(title);
        contact.setPhoneNumber(phoneNumber);
        contact.setFaxNumber(faxNumber);
        contact.setEmailAddress(emailAddress);
        contact.persist();
        contact.reload();
        contactsMap.put(name, contact);
        System.out.println("Added contact " + name);
        return contact;
    }

    public static ContactWrapper getContact(String name) throws Exception {
        ContactWrapper contact = contactsMap.get(name);
        if (contact == null) {
            throw new Exception("contact with name \"" + name
                + "\" does not exist");
        }
        return contact;
    }

}
