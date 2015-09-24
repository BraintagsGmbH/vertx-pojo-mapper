/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mapping.datastore;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;

/**
 * The IColumnHandler is used to generate and update columns inside the connected datastore and to read structural
 * information about the columns from there.
 * 
 * @author Michael Remme
 * 
 */

public interface IColumnHandler {
  /**
   * Returned by method {@link #matches(IField)} to specify that the current columnhandler won't handle the given field
   */
  public static final short MATCH_NONE = 0;

  /**
   * Returned by method {@link #matches(IField)} to specify that the current columnhandler handles the given field in a
   * minor way. For instance, if the class of the field is not direct the class, which the typehandler deals with, but
   * an instance of
   */
  public static final short MATCH_MINOR = 1;

  /**
   * Returned by method {@link #matches(IField)} to specify that the current columnhandler handles the given field in a
   * major way. For instance, if the class of the field is the direct class
   */
  public static final short MATCH_MAJOR = 2;

  /**
   * Checks wether the given {@link IField} is matching the criteria in the current instance. The method returns a
   * graded result, one of {@link #MATCH_NONE}, {@link #MATCH_MINOR} or {@link #MATCH_MAJOR}
   * 
   * @param field
   *          the field to be checked
   * @return 0 ( zero ) if the Typ
   */
  public short matches(IField field);

  /**
   * This method generates the command, which is used to create a column in the connected datastore
   * 
   * @param field
   *          the field which shall be generated in the datastore
   * @return the creation object, like a column creation string
   */
  public Object generate(IField field);

  /**
   * Method checks, wether the {@link IColumnInfo} plannedCi has some modifications to be done in comparison to
   * existingCi. This action is performed by an IColumnHander, cause the check will respect different properties when
   * checking a Text field or a numeric field for instance
   * 
   * @param plannedCi
   *          the planned IColumnInfo, which is coming out of an {@link IMapper}
   * @param existingCi
   *          the existing IColumnInfo typically read from a datastore
   * @return true, if the plannedCi is changed in comparison to the existingCi
   */
  public boolean isColumnModified(IColumnInfo plannedCi, IColumnInfo existingCi);

}
