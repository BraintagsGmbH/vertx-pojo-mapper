
/**
 * 
 * Existing annotations are:
 * 
 * ===== @Entity
 * 
 * ( name = "tableName" ) By annotating a class with de.braintags.io.vertx.pojomapper.annotation.Entity you are able to
 * set the name of the table which is used to store the information in the
 * {@link de.braintags.io.vertx.pojomapper.IDataStore}. By default the system will use the short classname of the
 * mapper.
 * 
 * ===== @Id
 * 
 * One field of the mapper must be annotated by de.braintags.io.vertx.pojomapper.annotation.field.Id, which will mark
 * the annotated field as primary key
 * 
 * ===== @Property
 * 
 * Properties of a mapper are stored inside the {@link de.braintags.io.vertx.pojomapper.IDataStore} by using the
 * fieldname by default. By annotating a field with de.braintags.io.vertx.pojomapper.annotation.field.Property you are
 * able to modify the name of the column in the table.
 * 
 * ===== @Referenced
 * 
 * This annotation is used to mark a field, so that values of this field are stored inside a separate table and that
 * those values are referenced by their id inside the stored result.
 * 
 * ===== @Embedded
 * 
 * This annotation is used to mark a field, so that values of that field are stored directly as content of the given
 * field.
 * 
 * ===== @ObjectFactory
 * 
 * By default the {@link de.braintags.io.vertx.pojomapper.mapping.IObjectFactory} is defined inside each
 * {@link de.braintags.io.vertx.pojomapper.mapping.IMapper} by using a default implementation. If you need another
 * implementation you are able to set it by adding this annotation to the mapper class and reference the class of the
 * {@link de.braintags.io.vertx.pojomapper.mapping.IObjectFactory} you want to use.
 * 
 * ===== @AfterLoad
 * 
 * All methods, which are annotated by this annotation are executed after an instance was loaded from the
 * {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ===== @BeforeSave
 * 
 * All methods, which are annotated by this annotation are executed before an instance is saved into the
 * {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ===== @AfterSave
 * 
 * All methods, which are annotated by this annotation are executed after an instance was saved into the
 * {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ===== @BeforeDelete
 * 
 * All methods, which are annotated by this annotation are executed before an instance is deleted from the
 * {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * ===== @AfterDelete
 * 
 * All methods, which are annotated by this annotation are executed after an instance was deleted from the
 * {@link de.braintags.io.vertx.pojomapper.IDataStore}
 * 
 * 
 * ===== @ConcreteClass not yet supported
 * 
 * ===== @ConstructorArguments to be tested
 * 
 * ===== @Indexes not yet implemented
 * 
 * {@link de.braintags.io.vertx.pojomapper.annotation.KeyGenerator}
 * 
 *
 * @author Michael Remme
 */
package de.braintags.io.vertx.pojomapper.annotation;
