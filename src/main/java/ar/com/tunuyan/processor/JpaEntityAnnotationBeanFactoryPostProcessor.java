package ar.com.tunuyan.processor;

import java.io.IOException;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.TypeFilter;

import ar.com.tunuyan.dao.impl.JpaGenericDaoImpl;

/**
 * Registra DAOs de tipo {@link JpaGenericDaoImpl} para cada clase anotada con
 * {@link javax.persistence.Entity @Entity}.
 * 
 * @see GenericDaoBeanFactoryPostProcessor.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 12, 2010
 */
public class JpaEntityAnnotationBeanFactoryPostProcessor extends
		GenericDaoBeanFactoryPostProcessor {

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
		AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();

		if (metadata.hasAnnotation("javax.persistence.Entity")
				|| metadata.hasAnnotation("org.hibernate.annotations.Entity")) {

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

}
