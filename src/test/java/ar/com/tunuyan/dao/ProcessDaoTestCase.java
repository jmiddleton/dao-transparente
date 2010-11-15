package ar.com.tunuyan.dao;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ar.com.tunuyan.ProcessDefinition;
import ar.com.tunuyan.ProcessDefinitionId;

@ContextConfiguration( { "classpath*:META-INF/_transparentDaoContext.xml", "classpath*:META-INF/applicationContext.xml",
		"classpath*:META-INF/testDaoContext.xml" })
public class ProcessDaoTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private ProcessDao daoProcess;

	@Test
	@Rollback(value = false)
	public void testPersist() {
		ProcessDefinition pd = new ProcessDefinition();
		pd.setId("prueba");
		pd.setName("prueba");
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

	@Test
	public void testGet() {
		Assert.assertNotNull(daoProcess.get(new ProcessDefinitionId("prueba", 1)));
	}

	@Test
	public void testFindBy() {
		Assert.assertEquals(1, daoProcess.findByNombre("prueba").size());
		Assert.assertEquals(1, daoProcess.findByNameAndVersion("prueba", 1).size());
		Assert.assertEquals(0, daoProcess.findByNameLikeAndCreatedBetween("%prueba%", new Date(System.currentTimeMillis() - 10000000), new Date())
				.size());
		Assert.assertEquals(0, daoProcess.findByNameLikeAndCreatedGreaterThan("%prueba%", new Date(System.currentTimeMillis() - 10000000)).size());
	}

	@Test
	public void testFindAll() {
		List<ProcessDefinition> result = daoProcess.findAllEntries(0, 50, null);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}

	@Test
	@Rollback(value = false)
	public void testRemoveById() {
		try {
			daoProcess.removeById(new ProcessDefinitionId("prueba", 1));
			Assert.assertTrue(true);
		} catch (GenericDaoException e) {
			fail(e.getMessage());
		}
	}

}
