package edu.ualberta.med.biobank.common.cbsr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class CbsrClinics {

    private static Map<String, ClinicWrapper> clinicsMap = null;

    private static Map<String, ContactWrapper> contactsMap = null;

    public static void createClinics(SiteWrapper site) throws Exception {
        clinicsMap = new HashMap<String, ClinicWrapper>();
        contactsMap = new HashMap<String, ContactWrapper>();

        addClinic(site, "CL1", "CL1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Foothills Medical Centre", "1403 29 Street", "Calgary", "Alberta",
            "t2n2t9");
        addClinic(site, "CL1-KDCS", "CL1-KDCS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING,
            "Charlynn Ursu, Sharon Gulewich and Coralea Bignell",
            "Sunridge Medical Gallery, Alberta Health Services Building",
            "#200, 2580- 32 street NE Room 3001", "Calgary", "Alberta",
            "T1Y7M8");
        addClinic(site, "CL1-NHS", "CL1-NHS",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "Shirley Cole",
            "Heritage Medical Research Clinic",
            "Suite 1140, 3350 Hospital Drive", "Calgary", "Alberta", "T2N4N1");
        addClinic(site, "CL2", "CL2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Alberta Children's Hospital", "2888 Shaganappi Trail NW",
            "Calgary", "Alberta", "t3b6a8");
        addClinic(site, "ED1", "ED1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "University of Alberta Hospital", null, "Edmonton", "Alberta",
            "T6G2B7");
        addClinic(site, "FM1", "FM1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "Philo King",
            "124 Beardsley Crescent", null, "Fort McMurray", "Alberta",
            "T9H2S2");
        addClinic(site, "GP1", "GP1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "Sharon Mollins",
            "Renal Dialysis 2W", "10409-98 Street", "Grande Prairie",
            "Alberta", "T8V0E2");
        addClinic(site, "HL1", "HL1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "QE11 Health Sciences Centre", "5788 University Avenue", "Halifax",
            "Nova Scotia", "B3H1V8");
        addClinic(site, "HL2", "HL2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "IWK Health Centre", "5850 University Ave PO Box 9700", "Halifax",
            "Nova Scotia", "B3K6R8");
        addClinic(site, "HM1", "HM1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "McMaster University Medical Centre", "1200 Main street West",
            "Hamilton", "Ontario", "L8N3Z5");
        addClinic(site, "KN1", "KN1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Kingston Cancer Centre", "25 King Street", "Kingston", "Ontario",
            "K7L5P9");
        addClinic(site, "LM1", "LM1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "", "3820 43 Avenue",
            "Room 307", "Lloydminister", "Saskatchewan", "S9V1Y5");
        addClinic(site, "LN1", "LN1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "St Joseph's Health Centre", "800 Comissioners Road East",
            "London", "Ontario", "N6A4V2");
        addClinic(site, "MC1", "MC1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Moncton Hospital", "135 MacBeath", "Moncton", "New Brunswick",
            "E1C6Z8");
        addClinic(site, "MN1", "MN1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Hopital Ste-Justine", "3175 Cote Ste-Catherine", "Montreal",
            "Quebec", "H3T1C5");
        addClinic(site, "MN2", "MN2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Montreal Children's Hospital", "2300 rue Tupper", "Montreal",
            "Quebec", "H3H1P3");
        addClinic(site, "OL1", "OL1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "", "5123 42 Street",
            null, "Olds", "Alberta", "T4H1X1");
        addClinic(site, "OT1", "OT1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Ottawa Hospital", "501 Smyth Road", "Ottawa", "Ontario", "K1H8L6");
        addClinic(site, "OT2", "OT2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Children's Hospital of Eastern Ontario", "401 Smyth Road",
            "Ottawa", "Ontario", "K1G4X3");
        addClinic(site, "QB1", "QB1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "CHA Hopital Enfant-Jesus", "1401 18e Rue", "Quebec City",
            "Quebec", "G1J1Z4");
        addClinic(site, "RD1", "RD1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "",
            "Red Deer Regional Hospital", "Room 120, 3942 50A Avenue",
            "Red Deer", "Alberta", "T4N6R2");
        addClinic(site, "SB1", "SB1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Saint Johns Regional Hospital", "400 University Avenue",
            "St John", "New Brunswick", "E2L4L2");
        addClinic(site, "SD1", "SD1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Sudbury Regional Hospital", "41 Ramsey Lake Road", "Sudbury",
            "Ontario", "P3E5J1");
        addClinic(site, "SF1", "SF1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "",
            "Health Science Centre, Eastern Health",
            "300 Prince Philip Drive, Room 4304D", "St John's",
            "Newfoundland and Labrador", "A1B3V6");
        addClinic(site, "SP1", "SP1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "",
            "St Therese Hospital", "4713 48 Avenue, PO Box 880", "St Paul",
            "Alberta", "T0A3A3");
        addClinic(site, "SS1", "SS1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Royal University Hospital", "103 Hospital Drive", "Saskatoon",
            "Alberta", "S7N0W8");
        addClinic(site, "TH1", "TH1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Thunder Bay Regional Hospital", "980 Oliver Road", "Thunder Bay",
            "Ontario", "P7B6V4");
        addClinic(site, "VN1", "VN1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "",
            "St Paul's Hospital",
            "1081 Burrard Street, Room 318 Comox Building", "Vancouver",
            "British Columbia", "V6Z1Y6");
        addClinic(site, "VN2", "VN2",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Women's and Children's Health Centre", "4480 Oak Street",
            "Vancouver", "British Columbia", "V6H3V4");
        addClinic(site, "WL1", "WL1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, "",
            "Westlock Health Care Centre",
            "Clinical Laboratory, 10020 93 Street", "Westlock", "Alberta",
            "T7P2G4");
        addClinic(site, "WN1", "WN1",
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Cancer Care Manitoba", "675 McDermot Avenue", "Winnipeg",
            "Manitoba", "R3E0V9");
        addClinic(site, "TR1", "TR1", "Closed", null, null, null, "Toronto",
            "Ontario", null);

        addContact("CL1", "Morna Brown", null, "403-944-4057", "403-944-1745",
            "morna.brown@calgaryhealthregion.ca");
        addContact("CL1-KDCS", "Coralea Bignell", null, "403-944-9885",
            "403-620-8075 cell", "coralea.bignell@albertahealthservices.ca");
        addContact("CL1-KDCS", "Sharon Gulewich", null, "403-944-9882",
            "403-816-1501 work cell",
            "sharon.gulewich@albertahealthservices.ca");
        addContact("CL1-KDCS", "Charlynn Ursu", null, "403-944-9883", null,
            "charlynn.ursu@calgaryhealthservices.ca");
        addContact("CL1-NHS", "Shirley Cole", null, "403-220-4988", null,
            "coles@ualberta.ca");
        addContact("CL2", "unknown", null, null, null, null);
        addContact("ED1", "Dellanee Kahlke", null, "780-407-8064",
            "780-932-2259 cell", "dellanee.kahlke@capitalhealth.ca");
        addContact("ED1", "Dr. Andrew Mason", "Principle Investigator",
            "780-492-8172", null, "andrew.mason@ualberta.ca");
        addContact("ED1", "Dr. Neesh Pannu", null, "780-401-0682 pager", null,
            null);
        addContact("ED1", "Wanda MacDonald", "Research Coordinator",
            "780-248-1037", "780-445-7769 pager", "wmacdona@ualberta.ca");
        addContact("ED1", "Erin Rezanoff", "Study Coordinator", "780-407-7448",
            "780-407-3324", "erin.rezanoff@albertahealthservices.ca");
        addContact("ED1", "Elizabeth Taylor", "Laboratory Technician",
            "780-903-7093", null, "e.taylor@ualberta.ca");
        addContact("ED1", "Bonny Granfield", null, "780-719-6279", null,
            "bgranfield@biosample.ca");
        addContact("ED1", "Melanie Peters", null, "780-407-6588", null,
            "melaniepeters@cha.ab.ca");
        addContact("ED1", "Dr. Justin Ezekowitz", "Principle Investigator",
            "780-407-8719", "780-407-6452", "jae2@ualberta.ca");
        addContact("ED1", "Sue Szigety", null, "780-407-7868", null,
            "sszigety@ualberta.ca");
        addContact("ED1", "Candace Dando", "Research Nurse", "780-721-8013",
            "780-445-7324 (pager)", "candace.dando@capitalhealth.ca");
        addContact("ED1", "Dawn Opgenorth", "Study Coordinator",
            "780-407-1543", "780-445-7621 (pager)", "dawno@ualberta.ca");
        addContact("FM1", "Phillo King", null, "780-799-4382", null,
            "pmking@nlhr.ca");
        addContact("GP1", "Sharon Mollins", null, "780-538-7576", null,
            "sharon.mollins@pchr.ca");
        addContact("HL1", "Niki Davis", "Data Management Coordinator",
            "902-473-4611", "902-473-4667", "nicki.davis@cdha.nshealth.ca");
        addContact("HL2", "Aleasha Warner", "Research Coordinator",
            "902-470-7414", "902-470-7456", "aleasha.warner@iwk.nshealth.ca");
        addContact("HM1", "Theresa Almonte", null, "905-521-2348", null,
            "almontet@hhsc.ca");
        addContact("KN1", "Bonny Granfield", null, null, null, null);
        addContact("KN1", "Maryanne Gibson", null, "613-544-2631 x6625", null,
            "maryanne.gibson@krcc.on.ca");
        addContact("LM1", "Janilee Dow", null, "306-825-3058 Home", null,
            "jdow15@hotmail.com");
        addContact("LN1", "Sheila Schembri", null, "519-685-8500 x53582", null,
            "sheila.schembri@lhsc.on.ca");
        addContact("MC1", "Dorine Belliveau", null, "506-857-5465", null,
            "dobelliv2@serha.ca");
        addContact("MN1", "Elaine Gloutnez", null, "514-345-4931 x6483", null,
            "elaine.gloutnez.hsj@ssss.gouv.qc.ca");
        addContact("MN2", "Nathalie Aubin", null, "514-412-4420", null,
            "nathalie.aubin@muhc.mcgill.ca");
        addContact("OL1", "Jodie Hingst", null, "403-507-8520", null, null);
        addContact("OT1", "Lucie Lacasse", null, "613-737-8252", null,
            "llacasse@ottawahospital.on.ca");
        addContact("OT2", "Tammy Burtenshaw", null, "613-373-7600 x2368", null,
            "tburtenshaw@cheo.on.ca");
        addContact("QB1", "Chantal Gagne", "Research Nurse",
            "418-649-0252 x3115", "418-649-5956",
            "chantal.gagne.recherche.cha@ssss.gouv.qc.ca");
        addContact("RD1", "Gwen Winter", null, "403-357-5357", null,
            "blueraven1@live.com");
        addContact("SB1", "Louise Bedard", null, null, null,
            "bedlo@reg2.health.nb.ca");
        addContact("SD1", "Elizabeth-Ann Paradis", null, "705-522-2200 x3264",
            null, "eparadis@hrsrh.on.ca");
        addContact("SF1", "Daisy Gibbons", "Research Nurse", "709-777-6508",
            "709-777-7622", "daisy.gibbons@easternhealth.ca");
        addContact("SP1", "Stacey Culp", null, "780-724-4325 home", null,
            "stacey.culp@capitalhealth.ca");
        addContact("SP1", null, null, null, null, null);
        addContact("SS1", "Dianne Dufour", null, "306-966-7962", null,
            "diannedufour@saskatoonhealthregion.ca");
        addContact("TH1", "Janet D Sharun", null, "807-684-6601", null,
            "sharunj@tbh.net");
        addContact("TR1", "Tony", null, null, null, null);
        addContact("VN1", "unknown", null, null, null, null);
        addContact("VN1", "Ann Chala", "Research Coordinator",
            "604-682-2344 x63135", "604-806-8856",
            "achala@providencehealth.bc.ca");
        addContact("VN2", "Colleen Fitzgerald", null, "604-875-2000 x7277",
            null, "cfitzgerald@cw.bc.ca");
        addContact("WL1", "Cathy Lent", "Clinic Director", "780-350-2025",
            "780-349-5922", "cathy.lent@aspenha.ab.ca");
        addContact("WN1", "Kathy Hjalmarsson", null, "204-787-4254", null,
            "kathy.hjalmarsson@cancercare.mb.ca");

    }

    private static ClinicWrapper addClinic(SiteWrapper site, String name,
        String nameShort, String activityStatusName, String comment,
        String street1, String street2, String city, String province,
        String postalCode) throws Exception {
        ClinicWrapper clinic = new ClinicWrapper(site.getAppService());
        clinic.setSite(site);
        clinic.setName(name);
        clinic.setNameShort(nameShort);
        clinic
            .setActivityStatus(CbsrSite.getActivityStatus(activityStatusName));
        clinic.setComment(comment);
        clinic.setStreet1(street1);
        clinic.setStreet2(street2);
        clinic.setCity(city);
        clinic.setProvince(province);
        clinic.setPostalCode(postalCode);
        clinic.persist();
        clinic.reload();
        clinicsMap.put(name, clinic);
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

    public static List<String> getClinicNames() throws Exception {
        if (clinicsMap == null) {
            throw new Exception("clinics have not been added");
        }
        return Collections.unmodifiableList(new ArrayList<String>(clinicsMap
            .keySet()));
    }

    private static ContactWrapper addContact(String clinicName, String name,
        String title, String officeNumber, String faxNumber, String emailAddress)
        throws Exception {
        ClinicWrapper clinic = clinicsMap.get(clinicName);

        if (clinic == null) {
            throw new Exception("no clinic with name " + clinicName);
        }

        ContactWrapper contact = new ContactWrapper(clinic.getAppService());
        contact.setClinic(clinic);
        contact.setName(name);
        contact.setTitle(title);
        contact.setOfficeNumber(officeNumber);
        contact.setFaxNumber(faxNumber);
        contact.setEmailAddress(emailAddress);
        contact.persist();
        contact.reload();
        clinic.reload();
        contactsMap.put(name, contact);
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

    public static List<String> getContactNames() throws Exception {
        if (contactsMap == null) {
            throw new Exception("contacts have not been added");
        }
        return new ArrayList<String>(contactsMap.keySet());
    }

}
