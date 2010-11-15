package ar.com.tunuyan.dao;

import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import ar.com.tunuyan.ProcessDefinition;
import ar.com.tunuyan.ProcessDefinitionId;

@SuppressWarnings("unchecked")
@ContextConfiguration( { "classpath*:META-INF/_transparentDaoContext.xml", "classpath*:META-INF/applicationContext.xml" })
public class GenericDaoTestCase extends AbstractTransactionalJUnit4SpringContextTests {

	@Resource(name="daoProcessDefinition")
	private GenericDao dao;

	@Test
	@Rollback(value = false)
	public void testPersist() {
		ProcessDefinition pd = new ProcessDefinition();
		pd.setId("prueba");
		pd.setName("prueba");
		pd.setVersion(1);
		pd.setAuthor("Juan");

		try {
			dao.persist(pd);
			Assert.assertNotNull(pd.getId());
		} catch (GenericDaoException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGet() {
		Assert.assertNotNull(dao.get(new ProcessDefinitionId("prueba", 1)));
	}

	@Test
	public void testFindEntries() {
		List<ProcessDefinition> result = dao.findAllEntries(0, 50, null);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testLCount() {
		Assert.assertEquals(1, dao.count());
	}

	@Test
	@Rollback(value = false)
	public void testRemoveById() {
		try {
			dao.removeById(new ProcessDefinitionId("prueba", 1));
			Assert.assertTrue(true);
		} catch (GenericDaoException e) {
			fail(e.getMessage());
		}
	}

}
