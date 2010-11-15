package ar.com.tunuyan.processor;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.TypeFilter;

import ar.com.tunuyan.dao.impl.HibernateGenericDaoImpl;

/**
 * Registra DAOs de tipo {@link HibernateGenericDaoImpl} para cada clase
 * encontrada en la metadata de Hibernate.
 * 
 * @see GenericDaoBeanFactoryPostProcessor.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 12, 2010
 */
public class HibernateMetadataBeanFactoryPostProcessor extends
		GenericDaoBeanFactoryPostProcessor {

	private SessionFactory sessionFactory;

	/**
	 * Determine whether the given class does not match any exclude filter and
	 * does match at least one include filter.
	 * 
	 * @param metadataReader
	 *            the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(MetadataReader metadataReader)
			throws IOException {
		ClassMetadata metadata = metadataReader.getClassMetadata();

		if (sessionFactory.getClassMetadata(metadata.getClassName()) != null) {

			for (TypeFilter tf : this.excludeFilters) {
				if (tf.match(metadataReader, null)) {
					return false;
				}
			}
			for (TypeFilter tf : this.includeFilters) {
				if (tf.match(metadataReader, null)) {
					return true;
				}
			}
			return true;
		}
		return false;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
