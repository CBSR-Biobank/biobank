package edu.ualberta.med.biobank.model.logging;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.After;
import org.junit.Before;

import edu.ualberta.med.biobank.DbTest;

public class LoggingTest extends DbTest {
	protected AuditReader auditReader;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		auditReader = AuditReaderFactory
				.get(sessionFactory.getCurrentSession());
	}

	@After
	public void tearDown() throws Exception {
		auditReader = null;
	}
}
