package edu.ualberta.med.biobank.test.wrappers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.TestDatabase;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TestFunctionalities extends TestDatabase {

    /**
     * Test equals method on object get from database
     */
    @Test
    public void equalsSite() throws Exception {
        int id = getSite().getId();

        Site site = new Site();

        site.setId(id);

        Site search1 = (Site) appService.search(Site.class, site).get(0);

        Site search2 = (Site) appService.search(Site.class, site).get(0);

        Assert.assertEquals(search1.getId(), search2.getId());

        System.out.println(search1.getClass());
        System.out.println(search2.getClass());
        Assert.assertEquals(search1, search2);
    }

    /**
     * Add a Aliquot Position
     * 
     * @throws Exception
     */
    @Test
    public void addAliquotPosition() throws Exception {
        int abstractPositionSize = appService.search(AbstractPosition.class,
            new AbstractPosition()).size();
        int aliquotPositionSize = appService.search(AliquotPosition.class,
            new AliquotPosition()).size();

        Container sc = getContainer();

        // 1st test = aliquot not used in another aliquot position
        Aliquot aliquot = findNotUsedSampleInAliquotPosition();

        tryAliquotPositionInsert(aliquot, sc);

        int abstractPositionSizeAfterTest1 = appService.search(
            AbstractPosition.class, new AbstractPosition()).size();
        int aliquotPositionSizeAfterTest1 = appService.search(
            AliquotPosition.class, new AliquotPosition()).size();

        // insertion should be ok
        Assert.assertEquals(abstractPositionSize + 1,
            abstractPositionSizeAfterTest1);
        Assert.assertEquals(aliquotPositionSize + 1,
            aliquotPositionSizeAfterTest1);

        // 2nd test = aliquot already used - can't work
        aliquot = findUsedSampleInAliquotPosition();

        tryAliquotPositionInsert(aliquot, sc);

        int abstractPositionSizeAfterTest2 = appService.search(
            AbstractPosition.class, new AbstractPosition()).size();
        int aliquotPositionSizeAfterTest2 = appService.search(
            AliquotPosition.class, new AliquotPosition()).size();
        // insertion should not be done
        Assert.assertEquals(abstractPositionSizeAfterTest1,
            abstractPositionSizeAfterTest2);
        Assert.assertEquals(aliquotPositionSizeAfterTest1,
            aliquotPositionSizeAfterTest2);
    }

    /**
     * Insert a new AliquotPosition
     */
    private void tryAliquotPositionInsert(Aliquot aliquot, Container sc) {
        try {
            AliquotPosition aliquotPosition = new AliquotPosition();
            aliquotPosition.setRow(3);
            aliquotPosition.setCol(3);
            aliquotPosition.setAliquot(aliquot);
            aliquotPosition.setContainer(sc);
            SDKQueryResult res = appService
                .executeQuery(new InsertExampleQuery(aliquotPosition));
            aliquotPosition = (AliquotPosition) res.getObjectResult();
        } catch (Exception e) {
            System.err.println("tryAliquotPositionInsert="
                + e.getCause().getMessage());
        }
    }

    /**
     * Insert 2 aliquotPosition with the executeBatchQuery method
     * 
     * @throws Exception
     */
    @Test
    public void batchQueriesAliquotPosition() throws Exception {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();

        int aliquotPositionSize = appService.search(AliquotPosition.class,
            new AliquotPosition()).size();

        Container sc = getContainer();

        Aliquot aliquot = findNotUsedSampleInAliquotPosition();
        System.out.println("Not used aliquot = " + aliquot.getId());

        AliquotPosition aliquotPosition = new AliquotPosition();
        aliquotPosition.setRow(1);
        aliquotPosition.setCol(1);
        aliquotPosition.setAliquot(aliquot);
        aliquotPosition.setContainer(sc);
        queries.add(new InsertExampleQuery(aliquotPosition));

        aliquotPosition = new AliquotPosition();
        aliquotPosition.setAliquot(aliquot);
        aliquotPosition.setRow(2);
        aliquotPosition.setCol(2);
        aliquotPosition.setContainer(sc);
        // will failed because aliquot can't be link to 2 aliquotPosition
        // roll back should be launched
        queries.add(new InsertExampleQuery(aliquotPosition));

        try {
            appService.executeBatchQuery(queries);
        } catch (ApplicationException ae) {
            System.err.println("batchQueriesAliquotPosition: "
                + ae.getCause().getMessage());
        } finally {
            int aliquotPositionSizeAfter = appService.search(
                AliquotPosition.class, new AliquotPosition()).size();
            Assert.assertEquals(aliquotPositionSize, aliquotPositionSizeAfter);
        }
    }

    /**
     * Find a aliquot that is not used in a aliquotPosition
     */
    private Aliquot findNotUsedSampleInAliquotPosition() throws Exception {
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Aliquot as aliquot "
                + " where aliquot not in "
                + " (select p.aliquot from edu.ualberta.med.biobank.model.AliquotPosition as p)");
        List<Aliquot> samples = appService.query(c);
        if (samples.size() > 0) {
            return samples.get(0);
        }
        throw new Exception("No samples to test");
    }

    /**
     * Find a aliquot that is used in a aliquotPosition
     */
    private Aliquot findUsedSampleInAliquotPosition() throws Exception {
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Aliquot as aliquot "
                + " where aliquot in "
                + " (select p.aliquot from edu.ualberta.med.biobank.model.AliquotPosition as p)");
        List<Aliquot> samples = appService.query(c);
        if (samples.size() > 0) {
            return samples.get(0);
        }
        throw new Exception("No samples to test");
    }

    /**
     * Get a site from the database
     */
    private Site getSite() throws Exception {
        Site site = new Site();

        List<Site> sites = appService.search(Site.class, site);
        if (sites.size() > 0) {
            return sites.get(0);
        }
        throw new Exception("One site should be added");
    }

    /**
     * insert 2 site with the executeBatchQuery method
     */
    @Test
    public void batchQueriesSite() throws Exception {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();

        Address address = new Address();
        address.setCity("Vesoul" + r.nextInt());
        address = (Address) appService.executeQuery(
            new InsertExampleQuery(address)).getObjectResult();

        int sitesSize = appService.search(Site.class, new Site()).size();

        Site site = new Site();
        site.setName("TestBatch1" + r.nextInt());
        site.setAddress(address);
        queries.add(new InsertExampleQuery(site));

        site = new Site();
        site.setName("TestBatch2" + r.nextInt());
        // Insert will failed because address is missing
        // Roll back should be launched
        site.setAddress(address);
        queries.add(new InsertExampleQuery(site));

        try {
            appService.executeBatchQuery(queries);
        } catch (Exception ae) {
            System.err.println("batchQueriesSite : "
                + ae.getCause().getMessage());
        } finally {
            int sitesSizeAfter = appService.search(Site.class, new Site())
                .size();
            Assert.assertEquals(sitesSize, sitesSizeAfter);
        }
    }

    // @Test
    // public void batchQueriesPerson() throws Exception {
    // List<SDKQuery> queries = new ArrayList<SDKQuery>();
    //
    // Address address = new Address();
    // address.setZip("dada");
    // address = (Address) appService.executeQuery(
    // new InsertExampleQuery(address)).getObjectResult();
    //
    // int personSize = appService.search(Person.class, new Person()).size();
    //
    // Person p = new Person();
    // p.setName("TestBatch" + r.nextInt());
    // p.setLivesAt(address);
    // queries.add(new InsertExampleQuery(p));
    //
    // p = new Person();
    // p.setName("TestBatch2" + r.nextInt());
    // // Insert will failed because address is missing
    // // Roll back should be launched
    // p.setLivesAt(address);
    // queries.add(new InsertExampleQuery(p));
    //
    // try {
    // appService.executeBatchQuery(queries);
    //
    // } catch (Exception ae) {
    // System.err.println("batchQueriesSite : "
    // + ae.getCause().getMessage());
    // } finally {
    // int personSizeAfter = appService.search(Person.class, new Person())
    // .size();
    // Assert.assertEquals(personSize, personSizeAfter);
    // }
    // }

    //
    // @Test
    // public void batchQueriesBag() throws Exception {
    // List<SDKQuery> queries = new ArrayList<SDKQuery>();
    //
    // Handle handle = new Handle();
    // handle.setColor("blue");
    // handle = (Handle) appService.executeQuery(
    // new InsertExampleQuery(handle)).getObjectResult();
    //
    // int bagSize = getSize(Bag.class);
    //
    // Bag b = new Bag();
    // b.setStyle("TestBatch" + r.nextInt());
    // b.setHandle(handle);
    // queries.add(new InsertExampleQuery(b));
    //
    // b = new Bag();
    // b.setStyle("TestBatch2" + r.nextInt());
    // // Insert will failed because address is missing
    // // Roll back should be launched
    // b.setHandle(handle);
    // queries.add(new InsertExampleQuery(b));
    //
    // try {
    // appService.executeBatchQuery(queries);
    //
    // } catch (Exception ae) {
    // System.err.println("batchQueriesSite : "
    // + ae.getCause().getMessage());
    // } finally {
    // int bagSizeAfter = getSize(Bag.class);
    // Assert.assertEquals(bagSize, bagSizeAfter);
    // }
    // }
    //
    // //
    // @Test
    // public void batchQueriesChain() throws Exception {
    // List<SDKQuery> queries = new ArrayList<SDKQuery>();
    //
    // OrderLine o = new OrderLine();
    // o.setName("lineTest");
    // o = (OrderLine) appService.executeQuery(new InsertExampleQuery(o))
    // .getObjectResult();
    //
    // int productSize = getSize(Product.class);
    //
    // Product p = new Product();
    // p.setName("TestBatch" + r.nextInt());
    // p.setLine(o);
    // queries.add(new InsertExampleQuery(p));
    //
    // p = new Product();
    // p.setName("TestBatch" + r.nextInt());
    // // // Insert will failed because address is missing
    // // // Roll back should be launched
    // p.setLine(o);
    // queries.add(new InsertExampleQuery(p));
    //
    // try {
    // appService.executeBatchQuery(queries);
    //
    // } catch (Exception ae) {
    // System.err.println("batchQueriesSite : "
    // + ae.getCause().getMessage());
    // } finally {
    // int productSizeAfter = getSize(Product.class);
    // Assert.assertEquals(productSize, productSizeAfter);
    // }
    // }
    //
    // @Test
    // public void testOrderline() throws Exception {
    // HQLCriteria criteria = new HQLCriteria("from "
    // + Product.class.getName() + " where line is null");
    // List<Product> list = appService.query(criteria);
    // System.out.println(list.size());
    // }

    @Test
    public void testContainers() throws Exception {
        HQLCriteria criteria = new HQLCriteria("from "
            + Container.class.getName()
            + " where locatedAtPosition.parentContainer is not null");
        List<Container> list = appService.query(criteria);
        System.out.println(list.size());
    }

    /**
     * Insert 2 aliquots with the executeBatchQuery method
     */
    @Test
    public void batchQueriesSample() throws Exception {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();

        int sampleSize = appService.search(Aliquot.class, new Aliquot()).size();

        SampleType st = getSampleType();

        PatientVisit pv = getPatientVisit();

        Aliquot aliquot = new Aliquot();
        aliquot.setInventoryId("Test1_" + r.nextInt());
        aliquot.setSampleType(st);
        aliquot.setPatientVisit(pv);
        queries.add(new InsertExampleQuery(aliquot));

        aliquot = new Aliquot();
        aliquot.setInventoryId("Test2_" + r.nextInt());
        // Insert will failed because of sampleType missing
        // Roll back should be launched
        queries.add(new InsertExampleQuery(aliquot));

        try {
            appService.executeBatchQuery(queries);
        } catch (ApplicationException ae) {
            System.out.println("batchQueriesSample : "
                + ae.getCause().getMessage());
        } finally {
            int sampleSizeAfter = appService.search(Aliquot.class,
                new Aliquot()).size();
            Assert.assertEquals(sampleSize, sampleSizeAfter);
        }
    }

    /**
     * get a patient visit from the database
     */
    private PatientVisit getPatientVisit() throws Exception {
        List<PatientVisit> visits = appService.search(PatientVisit.class,
            new PatientVisit());
        if (visits.size() > 0) {
            return visits.get(0);
        }
        throw new Exception("One patient visit type should be added");
    }

    /**
     * get a sample type from the database
     */
    private SampleType getSampleType() throws Exception {
        List<SampleType> types = appService.search(SampleType.class,
            new SampleType());
        if (types.size() > 0) {
            return types.get(0);
        }
        throw new Exception("One sample type should be added");
    }

    /**
     * Insert 2 containerType with executeBatchQuery method
     */
    @Test
    public void batchQueriesContainerType() throws Exception {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();

        int containerTypeSize = appService.search(ContainerType.class,
            new ContainerType()).size();

        Capacity capacity = getNewCapacity();
        Site site = getSite();

        ContainerType st = new ContainerType();
        st.setCapacity(capacity);
        st.setSite(site);
        st.setDefaultTemperature(r.nextDouble());
        st.setName("TestBatch_" + r.nextInt());
        queries.add(new InsertExampleQuery(st));

        st = new ContainerType();
        queries.add(new InsertExampleQuery(st));

        try {
            appService.executeBatchQuery(queries);
        } catch (ApplicationException ae) {
            System.out.println("batchQueriesContainerType : "
                + ae.getCause().getMessage());
        } finally {
            int containerTypeSizeAfter = appService.search(ContainerType.class,
                new ContainerType()).size();
            Assert.assertEquals(containerTypeSize, containerTypeSizeAfter);
        }

    }

    /**
     * Create a new capacity
     */
    private Capacity getNewCapacity() throws ApplicationException {
        Capacity capacity = new Capacity();
        capacity.setRowCapacity(r.nextInt(30));
        capacity.setColCapacity(r.nextInt(30));
        return (Capacity) appService.executeQuery(
            new InsertExampleQuery(capacity)).getObjectResult();
    }

    /**
     * insert 2 container position with executeBatchQuery method
     */
    @Test
    public void batchQueriesContainerPosition() throws Exception {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();

        int containerPositionSize = appService.search(ContainerPosition.class,
            new ContainerPosition()).size();

        Container sc = getContainer();
        Container scParent = getAnotherContainer(sc.getId());

        ContainerPosition containerPosition = new ContainerPosition();
        containerPosition.setRow(8);
        containerPosition.setCol(8);
        containerPosition.setContainer(sc);
        containerPosition.setParentContainer(scParent);
        queries.add(new InsertExampleQuery(containerPosition));

        containerPosition = new ContainerPosition();
        containerPosition.setContainer(sc); // same occupied container !
        containerPosition.setParentContainer(scParent);
        containerPosition.setRow(9);
        containerPosition.setCol(9);
        queries.add(new InsertExampleQuery(containerPosition));

        try {
            appService.executeBatchQuery(queries);
        } catch (ApplicationException ae) {
            System.out.println("batchQueriesContainerPosition : "
                + ae.getCause().getMessage());
        } finally {
            int containerPositionSizeAfter = appService.search(
                ContainerPosition.class, new ContainerPosition()).size();

            Assert.assertEquals(containerPositionSize,
                containerPositionSizeAfter);
        }
    }

    /**
     * Find a Container in the database
     */
    private Container getContainer() throws Exception {
        return getAnotherContainer(null);
    }

    /**
     * Find a Container with id different from param id (if null, take whichever
     * Container)
     */
    private Container getAnotherContainer(Integer id) throws Exception {
        List<Container> scs;
        if (id == null) {
            scs = appService.search(Container.class, new Container());
        } else {
            HQLCriteria c = new HQLCriteria(
                "from edu.ualberta.med.biobank.model.Container where id <> "
                    + id);
            scs = appService.query(c);
        }
        if (scs.size() == 0) {
            Capacity capacity = new Capacity();
            capacity.setRowCapacity(8);
            capacity.setColCapacity(12);
            SDKQueryResult result = appService
                .executeQuery(new InsertExampleQuery(capacity));
            capacity = (Capacity) result.getObjectResult();

            Site site = getSite();

            ContainerType st = new ContainerType();
            st.setCapacity(capacity);
            st.setDefaultTemperature(40.0);
            st.setSite(site);
            result = appService.executeQuery(new InsertExampleQuery(st));
            st = (ContainerType) result.getObjectResult();

            Container sc = new Container();
            sc.setLabel("scTest");
            sc.setSite(site);
            sc.setContainerType(st);
            result = appService.executeQuery(new InsertExampleQuery(sc));
            sc = (Container) result.getObjectResult();
            return sc;
        }
        return scs.get(0);
    }

    /**
     * insert 2 storage containers using executeBatchQuery method
     */
    @Test
    public void batchQueriesContainer() throws Exception {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();

        int containerSize = appService.search(Container.class, new Container())
            .size();

        Site site = getSite();

        Capacity capacity = getNewCapacity();
        System.out.println("capacity=" + capacity.getId());

        System.out.println("site=" + site.getId());

        ContainerType st = getContainerType();
        System.out.println("st=" + st.getId());

        Container sc = new Container();
        sc.setLabel(String.format("%02d", r.nextInt()));
        sc.setSite(site);
        sc.setContainerType(st);
        queries.add(new InsertExampleQuery(sc));

        sc = new Container();
        sc.setLabel(String.format("%02d", r.nextInt()));
        // no site !!
        queries.add(new InsertExampleQuery(sc));
        try {
            appService.executeBatchQuery(queries);
        } catch (ApplicationException ae) {
            System.out.println("batchQueriesContainer:"
                + ae.getCause().getMessage());
        } finally {
            int containerSizeAfter = appService.search(Container.class,
                new Container()).size();

            Assert.assertEquals(containerSize, containerSizeAfter);
        }
    }

    /**
     * return a storage type from the database
     */
    private ContainerType getContainerType() throws Exception {
        List<ContainerType> types = appService.search(ContainerType.class,
            new ContainerType());
        if (types.size() > 0) {
            return types.get(0);
        }
        throw new Exception("One ContainerType should be added");
    }

    @Test
    public void addStudy() throws Exception {
        Study study = new Study();
        study.setName("Toto");
        study.setSite(getSite());
        study = (Study) appService.executeQuery(new InsertExampleQuery(study))
            .getObjectResult();
    }

    @Test
    public void insertContainerTypeChild() throws Exception {
        ContainerType type = getContainerType();

        int childrenCount = 0;
        Collection<ContainerType> children = type
            .getChildContainerTypeCollection();
        if (children != null) {
            childrenCount = children.size();
        }

        Capacity capacity = getNewCapacity();
        Site site = getSite();

        ContainerType st = new ContainerType();
        st.setCapacity(capacity);
        st.setSite(site);
        st.setDefaultTemperature(r.nextDouble());
        st.setName("enfantType" + r.nextInt());
        st = (ContainerType) appService
            .executeQuery(new InsertExampleQuery(st)).getObjectResult();

        if (children == null) {
            children = new HashSet<ContainerType>();
            type.setChildContainerTypeCollection(children);
        }
        children.add(st);
        appService.executeQuery(new UpdateExampleQuery(type));

        // reread from database
        ContainerType searchType = new ContainerType();
        searchType.setId(type.getId());
        type = (ContainerType) appService.search(ContainerType.class,
            searchType).get(0);
        int childrenCountAfter = 0;
        children = type.getChildContainerTypeCollection();
        if (children != null) {
            childrenCountAfter = children.size();
        }

        Assert.assertEquals(childrenCount + 1, childrenCountAfter);
    }

    @SuppressWarnings("unused")
    private int getSize(Class<?> classType) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        List<?> list = appService.search(classType, instance);
        return list.size();
    }
}
