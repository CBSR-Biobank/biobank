package edu.ualberta.med.biobank.model.logging;

import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ualberta.med.biobank.DbTest;

public class LoggingTest extends DbTest {
    protected AuditReader auditReader;

    @Autowired
    SessionFactory sessionFactory;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        auditReader = AuditReaderFactory.get(sessionFactory.getCurrentSession());
    }

    @After
    public void tearDown() throws Exception {
        auditReader = null;
    }
}
