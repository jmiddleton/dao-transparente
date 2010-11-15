package ar.com.tunuyan.dao;

import static org.junit.Assert.fail;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ar.com.tunuyan.ProcessDefinition;
import ar.com.tunuyan.ProcessDefinitionId;

@ContextConfiguration( { "classpath*:META-INF/_transparentDaoContext.xml", "classpath*:META-INF/daosContext.xml", "classpath*:META-INF/testDaoContext.xml"  })
public class PerformanceDaoTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private ProcessDao processDao;

	@Test
	@Rollback(value = false)
	public void testPersist() {
		ProcessDefinition pd = new ProcessDefinition();
		pd.setId("prueba");
		pd.setName("prueba");
		pd.setVersion(1);
		pd.setAuthor("Juan");

		try {
			processDao.persist(pd);
			Assert.assertNotNull(pd.getId());
		} catch (GenericDaoException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindBy() {
		for (int i = 0; i < 2000; i++) {

			// Assert.assertEquals(1, processDao.findByNombre("prueba").size());
			Assert.assertEquals(1, processDao.findByNameAndVersion("prueba", 1).size());
			Assert.assertEquals(0, processDao
					.findByNameLikeAndCreatedBetween("%prueba%", new Date(System.currentTimeMillis() - 10000000), new Date()).size());
			Assert
					.assertEquals(0, processDao.findByNameLikeAndCreatedGreaterThan("%prueba%", new Date(System.currentTimeMillis() - 10000000))
							.size());
		}
	}

	@Test
	@Rollback(value = false)
	public void testRemoveById() {
		try {
			processDao.removeById(new ProcessDefinitionId("prueba", 1));
			Assert.assertTrue(true);
		} catch (GenericDaoException e) {
			fail(e.getMessage());
		}
	}
}
