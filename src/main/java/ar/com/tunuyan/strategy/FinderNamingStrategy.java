package ar.com.tunuyan.strategy;

/**
 * Estrategia para generar el nombre de la consulta a buscar.
 * 
 * @author <a href="mailto:jorge.middleton@gmail.com">Jorge L. Middleton</a>
 * @version $
 * 
 * @FechaCreacion May 6, 2010
 */
@SuppressWarnings("unchecked")
/*
 * Method Expressions

A method expression in GORM is made up of the prefix such as findBy followed by an expression that combines one or more properties. The basic form is:

Book.findBy([Property][Comparator][Boolean Operator])?[Property][Comparator]

The tokens marked with a '?' are optional. Each comparator changes the nature of the query. For example:

def book = Book.findByTitle("The Stand")

book = Book.findByTitleLike("Harry Pot%")

In the above example the first query is equivalent to equality whilst the latter, due to the Like comparator, is equivalent to a SQL like expression.

The possible comparators include:

    * InList - In the list of given values
    * LessThan - less than the given value
    * LessThanEquals - less than or equal a give value
    * GreaterThan - greater than a given value
    * GreaterThanEquals - greater than or equal a given value
    * Like - Equivalent to a SQL like expression
    * Ilike - Similar to a Like, except case insensitive
    * NotEqual - Negates equality
    * Between - Between two values (requires two arguments)
    * IsNotNull - Not a null value (doesn't require an argument)
    * IsNull - Is a null value (doesn't require an argument)

 */
public interface FinderNamingStrategy {

	/**
	 * Devuelve el nombre de la consulta previamente parametrizada ya sea por
	 * annotation o en un archivo de configuracion de consultas Hibernate.
	 * 
	 * @param entityClass
	 * @param methodName
	 * @return
	 */
	String getQueryName(Class entityClass, String methodName);

	/**
	 * Genera la consulta con base a los datos del nombre del metodo. Por
	 * ejemplo para el metodo findByNameAndVersion, se devuelve una consulta por
	 * name y version. <b> Tambien calcula el nombre cuando la palabra es
	 * compuesta por ejemplo FechaDesde como fecha_desde segun el separador
	 * especificado.
	 * 
	 * @param entityClass
	 * @param methodName
	 * @return
	 */
	String generateScaffoldQuery(Class entityClass, String methodName);

}
