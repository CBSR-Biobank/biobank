package edu.ualberta.med.biobank.common.cbsr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class CbsrClinics {

    private static Map<String, ClinicWrapper> clinicsMap = null;

    private static Map<String, ContactWrapper> contactsMap = null;

    public static void createClinics(SiteWrapper site) throws Exception {
        clinicsMap = new HashMap<String, ClinicWrapper>();
        contactsMap = new HashMap<String, ContactWrapper>();

        addClinic(site, "CL1-Foothills", "CL1-Foothills", "Active", null,
            "Foothills Medical Centre", "1403 29 Street", "Calgary", "Alberta",
            "t2n2t9");
        addClinic(site, "CL1-Heritage", "CL1-Heritage", "Active",
            "Shirley Cole", "Heritage Medical Research Clinic",
            "Suite 1140, 3350 Hospital Drive", "Calgary", "Alberta", "T2N4N1");
        addClinic(site, "CL1-Sunridge", "CL1-Sunridge", "Active",
            "Charlynn Ursu, Sharon Gulewich and Coralea Bignell",
            "Sunridge Medical Gallery, Alberta Health Services Building",
            "#200, 2580- 32 street NE Room 3001", "Calgary", "Alberta",
            "T1Y7M8");
        addClinic(site, "CL2-Alberta's Children's Hospital",
            "CL2-Children Hosp", "Active", null, "Alberta Children's Hospital",
            "2888 Shaganappi Trail NW", "Calgary", "Alberta", "t3b6a8");
        addClinic(site, "ED1-UofA", "ED1-UofA", "Active", null,
            "University of Alberta Hospital", null, "Edmonton", "Alberta",
            "T6G2B7");
        addClinic(site, "FM1-Phillo King", "FM1-King", "Active", "Philo King",
            "124 Beardsley Crescent", null, "Fort McMurray", "Alberta",
            "T9H2S2");
        addClinic(site, "GP1-Queen Elizabeth Hospital", "GP1-QE Hosp",
            "Active", "Sharon Mollins", "Renal Dialysis 2W", "10409-98 Street",
            "Grande Prairie", "Alberta", "T8V0E2");
        addClinic(site, "HL1-Queen Elizabeth II Hospital", "HL1-QE II",
            "Active", null, "QE11 Health Sciences Centre",
            "5788 University Avenue", "Halifax", "Nova Scotia", "B3H1V8");
        addClinic(site, "HL2-IWK Health Center", "HL2-IWK", "Active", null,
            "IWK Health Centre", "5850 University Ave PO Box 9700", "Halifax",
            "Nova Scotia", "B3K6R8");
        addClinic(site, "HM1-McMaster University", "HM1-McMaster", "Active",
            null, "McMaster University Medical Centre",
            "1200 Main street West", "Hamilton", "Ontario", "L8N3Z5");
        addClinic(site, "KN1-Kingston Cancer Center", "KN1-Cancer Ctr",
            "Active", null, "Kingston Cancer Centre", "25 King Street",
            "Kingston", "Ontario", "K7L5P9");
        addClinic(site, "LM1-Lloydminister Hospital", "LM1-Lloyd Hosp",
            "Active", "", "3820 43 Avenue", "Room 307", "Lloydminister",
            "Saskatchewan", "S9V1Y5");
        addClinic(site, "LN1-St Joseph's Health Center", "LN1-St Joseph",
            "Active", null, "St Joseph's Health Centre",
            "800 Comissioners Road East", "London", "Ontario", "N6A4V2");
        addClinic(site, "MC1-Moncton Hospital", "MC1-Moncton Hosp", "Active",
            null, "Moncton Hospital", "135 MacBeath", "Moncton",
            "New Brunswick", "E1C6Z8");
        addClinic(site, "MN1-Hopital Ste-Justine", "MN1-Ste-Justine", "Active",
            null, "Hopital Ste-Justine", "3175 Cote Ste-Catherine", "Montreal",
            "Quebec", "H3T1C5");
        addClinic(site, "MN2-Montreal Children's Hospital",
            "MN2-Children Hosp", "Active", null,
            "Montreal Children's Hospital", "2300 rue Tupper", "Montreal",
            "Quebec", "H3H1P3");
        addClinic(site, "OL1-Jodie Hingst", "OL1-Hingst", "Active", "",
            "5123 42 Street", null, "Olds", "Alberta", "T4H1X1");
        addClinic(site, "OT1-Ottawa Hospital", "OT1-Ottawa Hosp", "Active",
            null, "Ottawa Hospital", "501 Smyth Road", "Ottawa", "Ontario",
            "K1H8L6");
        addClinic(site, "OT2-Children's Hospital of Eastern Ontario",
            "OT2-Children Hosp", "Active", null,
            "Children's Hospital of Eastern Ontario", "401 Smyth Road",
            "Ottawa", "Ontario", "K1G4X3");
        addClinic(site, "QB1-Hopital Enfant-Jesus", "QB1-Enfant-Jesus",
            "Active", null, "CHA Hopital Enfant-Jesus", "1401 18e Rue",
            "Quebec City", "Quebec", "G1J1Z4");
        addClinic(site, "RD1-Red Deer Regional Hospital", "RD1-Red Deer Hosp",
            "Active", "", "Red Deer Regional Hospital",
            "Room 120, 3942 50A Avenue", "Red Deer", "Alberta", "T4N6R2");
        addClinic(site, "SB1-Saint Johns NB Regional Hospital",
            "SB1-St John NB Hosp", "Active", null,
            "Saint Johns Regional Hospital", "400 University Avenue",
            "St John", "New Brunswick", "E2L4L2");
        addClinic(site, "SD1-Sudbury Regional Hospital", "SD1-Sudbury Hosp",
            "Active", null, "Sudbury Regional Hospital", "41 Ramsey Lake Road",
            "Sudbury", "Ontario", "P3E5J1");
        addClinic(site, "SF1-Health Science Center", "SF1-Health NFLD",
            "Active", "", "Health Science Centre, Eastern Health",
            "300 Prince Philip Drive, Room 4304D", "St John's",
            "Newfoundland and Labrador", "A1B3V6");
        addClinic(site, "SP1-St Therese Hospital", "SP1-St Therese Hosp",
            "Active", "", "St Therese Hospital", "4713 48 Avenue, PO Box 880",
            "St Paul", "Alberta", "T0A3A3");
        addClinic(site, "SS1-Royal University Hospital", "SS1-Royal Hosp",
            "Active", null, "Royal University Hospital", "103 Hospital Drive",
            "Saskatoon", "Alberta", "S7N0W8");
        addClinic(site, "TH1-Thunder Bay Regional Hospital",
            "TH1-Regional Hosp", "Active", null,
            "Thunder Bay Regional Hospital", "980 Oliver Road", "Thunder Bay",
            "Ontario", "P7B6V4");
        addClinic(site, "TR1-St Michael's Hospital", "TR1-St Mikes", "Closed",
            null, null, null, "Toronto", "Ontario", null);
        addClinic(site, "VN1-St Paul's Hospital", "VN1-St Paul", "Active", "",
            "St Paul's Hospital",
            "1081 Burrard Street, Room 318 Comox Building", "Vancouver",
            "British Columbia", "V6Z1Y6");
        addClinic(site, "VN2-BC Women and Children's Hospital",
            "VN2-Childrens Hosp", "Active", null,
            "Women's and Children's Health Centre", "4480 Oak Street",
            "Vancouver", "British Columbia", "V6H3V4");
        addClinic(site, "WL1-Westlock Health Care Center", "WL1-Westlock Hosp",
            "Active", "", "Westlock Health Care Centre",
            "Clinical Laboratory, 10020 93 Street", "Westlock", "Alberta",
            "T7P2G4");
        addClinic(site, "WN1-Cancer Care Manitoba", "WN1-Cancer Care",
            "Active", null, "Cancer Care Manitoba", "675 McDermot Avenue",
            "Winnipeg", "Manitoba", "R3E0V9");

        addContact("CL1-Foothills", "Morna Brown", null, "403-944-4057",
            "403-944-1745", "morna.brown@calgaryhealthregion.ca");
        addContact("CL1-Heritage", "Shirley Cole", null, "403-220-4988", null,
            "coles@ualberta.ca");
        addContact("CL1-Sunridge", "Charlynn Ursu", null, "403-944-9883", null,
            "charlynn.ursu@calgaryhealthservices.ca");
        addContact("CL1-Sunridge", "Coralea Bignell", null, "403-944-9885",
            "403-620-8075 cell", "coralea.bignell@albertahealthservices.ca");
        addContact("CL1-Sunridge", "Sharon Gulewich", null, "403-944-9882",
            "403-816-1501 work cell",
            "sharon.gulewich@albertahealthservices.ca");
        addContact("CL2-Children Hosp", "unknown", null, null, null, null);
        addContact("ED1-UofA", "Bonny Granfield", null, "780-719-6279", null,
            "bgranfield@biosample.ca");
        addContact("ED1-UofA", "Candace Dando", "Research Nurse",
            "780-721-8013", "780-445-7324 (pager)",
            "candace.dando@capitalhealth.ca");
        addContact("ED1-UofA", "Dawn Opgenorth", "Study Coordinator",
            "780-407-1543", "780-445-7621 (pager)", "dawno@ualberta.ca");
        addContact("ED1-UofA", "Dellanee Kahlke", null, "780-407-8064",
            "780-932-2259 cell", "dellanee.kahlke@capitalhealth.ca");
        addContact("ED1-UofA", "Dr. Andrew Mason", "Principle Investigator",
            "780-492-8172", null, "andrew.mason@ualberta.ca");
        addContact("ED1-UofA", "Dr. Justin Ezekowitz",
            "Principle Investigator", "780-407-8719", "780-407-6452",
            "jae2@ualberta.ca");
        addContact("ED1-UofA", "Dr. Neesh Pannu", null, "780-401-0682 pager",
            null, null);
        addContact("ED1-UofA", "Elizabeth Taylor", "Laboratory Technician",
            "780-903-7093", null, "e.taylor@ualberta.ca");
        addContact("ED1-UofA", "Erin Rezanoff", "Study Coordinator",
            "780-407-7448", "780-407-3324",
            "erin.rezanoff@albertahealthservices.ca");
        addContact("ED1-UofA", "Marleen Irwin", "Research Nurse",
            "780-221-1503", "780-431-3031 (pager)", "mirwin@ualberta.ca");
        addContact("ED1-UofA", "Melanie Peters", null, "780-407-6588", null,
            "melaniepeters@cha.ab.ca");
        addContact("ED1-UofA", "Sue Szigety", null, "780-407-7868", null,
            "sszigety@ualberta.ca");
        addContact("ED1-UofA", "Wanda MacDonald", "Research Coordinator",
            "780-248-1037", "780-445-7769 pager", "wmacdona@ualberta.ca");
        addContact("FM1-King", "Phillo King", null, "780-799-4382", null,
            "pmking@nlhr.ca");
        addContact("GP1-QE Hosp", "Sharon Mollins", null, "780-538-7576", null,
            "sharon.mollins@pchr.ca");
        addContact("HL1-QE II", "Niki Davis", "Data Management Coordinator",
            "902-473-4611", "902-473-4667", "nicki.davis@cdha.nshealth.ca");
        addContact("HL2-IWK", "Aleasha Warner", "Research Coordinator",
            "902-470-7414", "902-470-7456", "aleasha.warner@iwk.nshealth.ca");
        addContact("HM1-McMaster", "Theresa Almonte", null, "905-521-2348",
            null, "almontet@hhsc.ca");
        addContact("KN1-Cancer Ctr", "Bonny Granfield", null, null, null, null);
        addContact("KN1-Cancer Ctr", "Maryanne Gibson", null,
            "613-544-2631 x6625", null, "maryanne.gibson@krcc.on.ca");
        addContact("LM1-Lloyd Hosp", "Janilee Dow", null, "306-825-3058 Home",
            null, "jdow15@hotmail.com");
        addContact("LN1-St Joseph", "Sheila Schembri", null,
            "519-685-8500 x53582", null, "sheila.schembri@lhsc.on.ca");
        addContact("MC1-Moncton Hosp", "Dorine Belliveau", null,
            "506-857-5465", null, "dobelliv2@serha.ca");
        addContact("MN1-Ste-Justine", "Elaine Gloutnez", null,
            "514-345-4931 x6483", null, "elaine.gloutnez.hsj@ssss.gouv.qc.ca");
        addContact("MN2-Children Hosp", "Nathalie Aubin", null, "514-412-4420",
            null, "nathalie.aubin@muhc.mcgill.ca");
        addContact("OL1-Hingst", "Jodie Hingst", null, "403-507-8520", null,
            null);
        addContact("OT1-Ottawa Hosp", "Lucie Lacasse", null, "613-737-8252",
            null, "llacasse@ottawahospital.on.ca");
        addContact("OT2-Children Hosp", "Tammy Burtenshaw", null,
            "613-373-7600 x2368", null, "tburtenshaw@cheo.on.ca");
        addContact("QB1-Enfant-Jesus", "Chantal Gagne", "Research Nurse",
            "418-649-0252 x3115", "418-649-5956",
            "chantal.gagne.recherche.cha@ssss.gouv.qc.ca");
        addContact("RD1-Red Deer Hosp", "Gwen Winter", null, "403-357-5357",
            null, "blueraven1@live.com");
        addContact("SB1-St John NB Hosp", "Louise Bedard", null, null, null,
            "bedlo@reg2.health.nb.ca");
        addContact("SD1-Sudbury Hosp", "Elizabeth-Ann Paradis", null,
            "705-522-2200 x3264", null, "eparadis@hrsrh.on.ca");
        addContact("SF1-Health NFLD", "Daisy Gibbons", "Research Nurse",
            "709-777-6508", "709-777-7622", "daisy.gibbons@easternhealth.ca");
        addContact("SP1-St Therese Hosp", null, null, null, null, null);
        addContact("SP1-St Therese Hosp", "Stacey Culp", null,
            "780-724-4325 home", null, "stacey.culp@capitalhealth.ca");
        addContact("SS1-Royal Hosp", "Dianne Dufour", null, "306-966-7962",
            null, "diannedufour@saskatoonhealthregion.ca");
        addContact("TH1-Regional Hosp", "Janet D Sharun", null, "807-684-6601",
            null, "sharunj@tbh.net");
        addContact("TR1-St Mikes", "Tony", null, null, null, null);
        addContact("VN1-St Paul", "Ann Chala", "Research Coordinator",
            "604-682-2344 x63135", "604-806-8856",
            "achala@providencehealth.bc.ca");
        addContact("VN1-St Paul", "unknown", null, null, null, null);
        addContact("VN2-Childrens Hosp", "Colleen Fitzgerald", null,
            "604-875-2000 x7277", null, "cfitzgerald@cw.bc.ca");
        addContact("WL1-Westlock Hosp", "Cathy Lent", "Clinic Director",
            "780-350-2025", "780-349-5922", "cathy.lent@aspenha.ab.ca");
        addContact("WN1-Cancer Care", "Kathy Hjalmarsson", null,
            "204-787-4254", null, "kathy.hjalmarsson@cancercare.mb.ca");
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
        clinicsMap.put(nameShort, clinic);
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

    private static ContactWrapper addContact(String clinicNameShort,
        String name, String title, String officeNumber, String faxNumber,
        String emailAddress) throws Exception {
        ClinicWrapper clinic = clinicsMap.get(clinicNameShort);

        if (clinic == null) {
            throw new Exception("no clinic with name " + clinicNameShort);
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
