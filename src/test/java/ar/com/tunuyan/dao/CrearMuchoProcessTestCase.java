package ar.com.tunuyan.dao;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ar.com.tunuyan.ProcessDefinition;

@ContextConfiguration( { "classpath*:META-INF/_transparentDaoContext.xml", "classpath*:META-INF/applicationContext.xml",
		"classpath*:META-INF/testDaoContext.xml" })
public class CrearMuchoProcessTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private ProcessDao daoProcess;

	@Test
	@Rollback(value = false)
	public void testPersist() {
		for (int i = 0; i < 100; i++) {

			ProcessDefinition pd = new ProcessDefinition();
			pd.setId("prueba" + i);
			pd.setName("prueba " + i);
			pd.setVersion(1);
			pd.setAuthor("Juan");

			try {
				daoProcess.persist(pd);
				Assert.assertNotNull(pd.getId());
			} catch (GenericDaoException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		}
	}
}
