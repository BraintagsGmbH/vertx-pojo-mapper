package de.braintags.vertx.jomnigate.mapping.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import de.braintags.vertx.jomnigate.annotation.Index;
import de.braintags.vertx.jomnigate.annotation.IndexField;
import de.braintags.vertx.jomnigate.annotation.IndexType;
import de.braintags.vertx.jomnigate.dataaccess.query.IIndexedField;
import de.braintags.vertx.jomnigate.mapping.IIndexDefinition;
import de.braintags.vertx.jomnigate.mapping.IIndexFieldDefinition;
import de.braintags.vertx.jomnigate.mapping.IMapper;
import de.braintags.vertx.jomnigate.mapping.IndexOption;
import de.braintags.vertx.jomnigate.mapping.IndexOption.IndexFeature;

/**
 * Implementation of {@link IIndexDefinition}
 * 
 * @author sschmitt
 *
 */
public class IndexDefinition implements IIndexDefinition {

  private String name;
  private final List<IIndexFieldDefinition> fields;
  private List<IndexOption> indexOptions;
  private String identifier;

  /**
   * Create a definition from an indexed field of a mapper
   * 
   * @param field
   *          the indexed field
   * @param mapper
   *          the mapper to fetch the column name of the field from
   */
  public IndexDefinition(final IIndexedField field, final IMapper<?> mapper) {
    name = "IdxF_" + field.getFieldName();
    fields = new ArrayList<>();
    IndexFieldDefinition fieldDef = new IndexFieldDefinition();
    fieldDef.setName(field.getColumnName(mapper));
    fieldDef.setType(IndexType.ASC);
    fields.add(fieldDef);
  }

  /**
   * Create a definition from the annotation of an entity
   * 
   * @param index
   *          the annotation
   */
  public IndexDefinition(final Index index) {
    name = index.name();
    fields = new ArrayList<>();
    for (IndexField field : index.fields()) {
      IndexFieldDefinition def = new IndexFieldDefinition();
      def.setName(field.fieldName());
      def.setType(field.type());
      fields.add(def);
    }
    if (index.options() != null) {
      getIndexOptions().add(new IndexOption(IndexFeature.UNIQUE, index.options().unique()));
    }
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public List<IIndexFieldDefinition> getFields() {
    return fields;
  }

  @Override
  public List<IndexOption> getIndexOptions() {
    if (indexOptions == null)
      indexOptions = new ArrayList<>();
    return indexOptions;
  }

  @Override
  public String getIdentifier() {
    if (identifier == null) {
      identifier = createIdentifier();
    }
    return identifier;
  }

  /**
   * Create a unique identifier consisting of all field names sorted, combined, and transformed to lowercase. The
   * resulting string is hashed to prevent overly long identifiers.
   * 
   * @return a unique identifier for the fields of the definition
   */
  private String createIdentifier() {
    return String.valueOf(fields.stream().map(IIndexFieldDefinition::getName).sorted().collect(Collectors.joining())
        .toLowerCase(Locale.US).hashCode());
  }

  @Override
  public String toString() {
    return "IndexDefinition [name=" + name + ", fields=" + fields + ", indexOptions=" + indexOptions + ", identifier="
        + getIdentifier() + "]";
  }
}
