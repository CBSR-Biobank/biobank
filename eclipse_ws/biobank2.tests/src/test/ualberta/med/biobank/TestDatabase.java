package test.ualberta.med.biobank;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;

public class TestDatabase {
    protected static WritableApplicationService appService;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private static final int ALPHABET_LEN = ALPHABET.length();

    protected Random r;

    private static final List<String> IGNORE_RETURN_TYPES = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;
        {
            add("java.lang.Class");
            add("java.lang.Object");
            add("java.util.Set");
            add("java.util.List");
            add("java.util.Collection");
        }
    };

    private class GetterInfo {
        Method getMethod;
        Method setMethod;
    }

    @Before
    public void setUp() throws Exception {
        r = new Random();
        appService = AllTests.appService;
        if (appService == null) {
            AllTests.setUp();
            appService = AllTests.appService;
        }
    }

    public Collection<GetterInfo> getGettersAndSetters(ModelWrapper<?> w) {
        HashMap<String, GetterInfo> map = new HashMap<String, GetterInfo>();
        Method[] methods = w.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get")
                && !method.getName().equals("getClass")
                && !IGNORE_RETURN_TYPES.contains(method.getReturnType()
                    .getName())
                && !method.getReturnType().getName().startsWith(
                    "edu.ualberta.med.biobank.common.wrappers")) {
                GetterInfo getterInfo = new GetterInfo();
                getterInfo.getMethod = method;
                map.put(method.getName(), getterInfo);
            }
        }

        for (Method method : methods) {
            if (method.getName().startsWith("set")
                && !method.getName().equals("setClass")) {
                String setterName = method.getName();
                String getterName = "g"
                    + setterName.substring(1, setterName.length());
                GetterInfo getterInfo = map.get(getterName);
                if (getterInfo == null) {
                    System.out.println("no getter found for "
                        + w.getClass().getName() + "." + setterName + "()");
                    continue;
                }
                getterInfo.setMethod = method;
            }
        }
        return map.values();
    }

    public void testGettersAndSetters(ModelWrapper<?> w)
        throws BiobankCheckException, Exception {
        Collection<GetterInfo> gettersInfoList = getGettersAndSetters(w);
        for (GetterInfo getterInfo : gettersInfoList) {
            if (getterInfo.setMethod == null) {
                System.out.println("no setter found for "
                    + w.getClass().getName() + "."
                    + getterInfo.getMethod.getName() + "()");
                continue;
            }

            String getReturnType = getterInfo.getMethod.getReturnType()
                .getName();

            for (int i = 0; i < 5; ++i) {
                Object parameter = null;

                if (getReturnType.equals("java.lang.Boolean")) {
                    parameter = new Boolean(r.nextBoolean());
                } else if (getReturnType.equals("java.lang.Integer")) {
                    parameter = new Integer(r.nextInt());
                } else if (getReturnType.equals("java.lang.Double")) {
                    parameter = new Double(r.nextDouble());
                } else if (getReturnType.equals("java.lang.String")) {
                    String str = new String();
                    for (int j = 0, n = r.nextInt(32); j < n; ++j) {
                        int begin = r.nextInt(ALPHABET_LEN - 1);
                        str += ALPHABET.substring(begin, begin + 1);
                    }
                    parameter = str;
                } else if (getReturnType.equals("java.util.Date")) {
                    parameter = getRandomDate();
                } else {
                    throw new Exception("return type " + getReturnType
                        + " for method " + getterInfo.getMethod.getName()
                        + " for class " + w.getClass().getName()
                        + " not implemented");
                }

                getterInfo.setMethod.invoke(w, parameter);
                w.persist();
                w.reload();
                Object getResult = getterInfo.getMethod.invoke(w);

                Assert.assertEquals(w.getClass().getName() + "."
                    + getterInfo.getMethod.getName() + "()", parameter,
                    getResult);
            }
        }

    }

    public Date getRandomDate() throws ParseException {
        String dateStr = String.format("%04-%02-%02 %02:%02", 2000 + r
            .nextInt(40), r.nextInt(12) + 1, r.nextInt(30) + 1,
            r.nextInt(24) + 1, r.nextInt(60) + 1);
        return DateFormatter.dateFormatter.parse(dateStr);
    }

    public <T> T chooseRandomlyInList(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() > 1) {
            int pos = r.nextInt(list.size());
            return list.get(pos);
        }
        return null;
    }

    protected ContainerTypeWrapper newContainerType(SiteWrapper site,
        String name, String nameShort, Integer labelingScheme,
        Integer rowCapacity, Integer colCapacity, boolean isTopLevel) {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(appService);
        ct.setSite(site);
        ct.setName(name);
        ct.setNameShort(nameShort);
        ct.setChildLabelingScheme(labelingScheme);
        if (rowCapacity != null)
            ct.setRowCapacity(rowCapacity);
        if (colCapacity != null)
            ct.setColCapacity(colCapacity);
        ct.setTopLevel(isTopLevel);
        return ct;
    }

    protected ContainerTypeWrapper addContainerType(SiteWrapper site,
        String name, String nameShort, Integer labelingScheme,
        Integer rowCapacity, Integer colCapacity, boolean isTopLevel)
        throws BiobankCheckException, Exception {
        ContainerTypeWrapper container = newContainerType(site, name,
            nameShort, labelingScheme, rowCapacity, colCapacity, isTopLevel);
        container.persist();
        return container;
    }

    protected ContainerWrapper newContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type)
        throws Exception {
        ContainerWrapper container;

        container = new ContainerWrapper(appService);
        if (label != null) {
            if (type.getTopLevel()) {
                container.setLabel(label);
            } else {
                throw new Exception(
                    "cannot set label on non top level containers");
            }
        }
        container.setProductBarcode(barcode);
        if (parent != null) {
            container.setParent(parent);
        }
        if (site != null) {
            container.setSite(site);
        }
        container.setContainerType(type);
        return container;
    }

    protected ContainerWrapper newContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type,
        Integer row, Integer col) throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type);
        container.setPosition(row, col);
        return container;
    }

    protected ContainerWrapper addContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type)
        throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type);
        container.persist();
        return container;
    }

    protected ContainerWrapper addContainer(String label, String barcode,
        ContainerWrapper parent, SiteWrapper site, ContainerTypeWrapper type,
        Integer row, Integer col) throws Exception {
        ContainerWrapper container = newContainer(label, barcode, parent, site,
            type, row, col);
        container.persist();
        return container;

    }

    protected PatientWrapper newPatient(String number) {
        PatientWrapper patient = new PatientWrapper(appService);
        patient.setNumber(number);
        return patient;
    }

    protected PatientWrapper addPatient(String number, StudyWrapper study)
        throws Exception {
        PatientWrapper patient = newPatient(number);
        patient.setStudy(study);
        patient.persist();
        return patient;
    }

    protected PatientVisitWrapper newPatientVisit(PatientWrapper patient,
        Date dateDrawn, Date dateProcessed, Date dateReceived) {
        PatientVisitWrapper pv = new PatientVisitWrapper(appService);
        pv.setPatient(patient);
        pv.setDateDrawn(dateDrawn);
        pv.setDateProcessed(dateProcessed);
        pv.setDateReceived(dateReceived);
        return pv;
    }

    protected PatientVisitWrapper addPatientVisit(PatientWrapper patient,
        Date dateDrawn, Date dateProcessed, Date dateReceived) throws Exception {
        PatientVisitWrapper pv = newPatientVisit(patient, dateDrawn,
            dateProcessed, dateReceived);
        pv.persist();
        return pv;
    }

    protected SampleWrapper newSample(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) {
        SampleWrapper sample = new SampleWrapper(appService);
        sample.setSampleType(sampleType);
        sample.setParent(container);
        sample.setPatientVisit(pv);
        sample.setPosition(row, col);
        return sample;
    }

    protected SampleWrapper addSample(SampleTypeWrapper sampleType,
        ContainerWrapper container, PatientVisitWrapper pv, Integer row,
        Integer col) throws Exception {
        SampleWrapper sample = newSample(sampleType, container, pv, row, col);
        sample.persist();
        return sample;
    }

    protected StudyWrapper newStudy(String name, String nameShort,
        SiteWrapper site) {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName(name);
        study.setNameShort(nameShort);
        study.setSite(site);
        return study;
    }

    protected StudyWrapper addStudy(String name, String nameShort,
        SiteWrapper site) throws Exception {
        StudyWrapper study = newStudy(name, nameShort, site);
        study.persist();
        return study;
    }

    protected SiteWrapper newSite(String name, String street1) {
        SiteWrapper site = new SiteWrapper(appService);
        site.setName(name);
        site.setStreet1(street1);
        return site;
    }

    protected SiteWrapper addSite(String name, String street1) throws Exception {
        SiteWrapper site = newSite(name, street1);
        site.persist();
        return site;
    }

}
