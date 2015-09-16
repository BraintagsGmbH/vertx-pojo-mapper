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
package de.braintags.io.vertx.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Several utility methods for handling classes
 * 
 * @author Michael Remme
 * 
 */

public class ClassUtil {
  private static final Logger log = LoggerFactory.getLogger(ClassUtil.class);

  /**
   * Get a defined {@link Constructor} of the given class with the arguments
   * 
   * @param cls
   *          the class to be examined
   * @param arguments
   *          the arguments of the contructor
   * @return a fitting constructor
   */
  public static Constructor<?> getConstructor(Class<?> cls, Class<?>... arguments) {
    try {
      return cls.getDeclaredConstructor(arguments);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new MappingException(e);
    }
  }

  /**
   * Get a list of all methods declared in the supplied class, and all its superclasses (except java.lang.Object),
   * recursively.
   *
   * @param type
   *          the class for which we want to retrieve the Methods
   * @return an array of all declared and inherited fields
   */
  public static List<Method> getDeclaredAndInheritedMethods(final Class<?> type) {
    return getDeclaredAndInheritedMethods(type, new ArrayList<Method>());
  }

  private static List<Method> getDeclaredAndInheritedMethods(final Class<?> type, final List<Method> methods) {
    if ((type == null) || (type == Object.class)) {
      return methods;
    }

    final Class<?> parent = type.getSuperclass();
    final List<Method> list = getDeclaredAndInheritedMethods(parent,
        methods == null ? new ArrayList<Method>() : methods);

    for (final Method m : type.getDeclaredMethods()) {
      if (!Modifier.isStatic(m.getModifiers())) {
        list.add(m);
      }
    }

    return list;
  }

  public static <T> Class<?> getTypeArgument(final Class<? extends T> clazz,
      final TypeVariable<? extends GenericDeclaration> tv) {
    final Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
    Type type = clazz;
    // start walking up the inheritance hierarchy until we hit the end
    while (type != null && !getClass(type).equals(Object.class)) {
      if (type instanceof Class) {
        // there is no useful information for us in raw types, so just
        // keep going.
        type = ((Class<?>) type).getGenericSuperclass();
      } else {
        final ParameterizedType parameterizedType = (ParameterizedType) type;
        final Class<?> rawType = (Class<?>) parameterizedType.getRawType();

        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
        for (int i = 0; i < actualTypeArguments.length; i++) {
          if (typeParameters[i].equals(tv)) {
            final Class<?> cls = getClass(actualTypeArguments[i]);
            if (cls != null) {
              return cls;
            }
            // We don't know that the type we want is the one in the map, if this argument has been
            // passed through multiple levels of the hierarchy. Walk back until we run out.
            Type typeToTest = resolvedTypes.get(actualTypeArguments[i]);
            while (typeToTest != null) {
              final Class<?> classToTest = getClass(typeToTest);
              if (classToTest != null) {
                return classToTest;
              }
              typeToTest = resolvedTypes.get(typeToTest);
            }
          }
          resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
        }

        if (!rawType.equals(Object.class)) {
          type = rawType.getGenericSuperclass();
        }
      }
    }

    return null;
  }

  /**
   * Get the underlying class for a type, or null if the type is a variable type.
   *
   * @param type
   *          the type
   * @return the underlying class
   */
  public static Class<?> getClass(final Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return getClass(((ParameterizedType) type).getRawType());
    } else if (type instanceof WildcardType) {
      return (Class<?>) ((WildcardType) type).getUpperBounds()[0];
    } else if (type instanceof GenericArrayType) {
      final Type componentType = ((GenericArrayType) type).getGenericComponentType();
      final Class<?> componentClass = getClass(componentType);
      if (componentClass != null) {
        return Array.newInstance(componentClass, 0).getClass();
      } else {
        log.debug("************ ClassUtil.getClass 1st else");
        log.debug("************ type = " + type);
        return null;
      }
    } else {
      log.debug("************ ClassUtil.getClass final else");
      log.debug("************ type = " + type);
      return null;
    }
  }

  /**
   * Generate a class definition from the given {@link Type}
   * 
   * @param t
   *          the {@link Type} to be examined
   * @return a class definition for the given {@link Type}
   */
  public static Class<?> toClass(final Type t) {
    Class<?> returnClass = null;
    if (t == null) {
      returnClass = null;
    } else if (t instanceof Class) {
      returnClass = (Class<?>) t;
    } else if (t instanceof GenericArrayType) {
      final Class<?> type = (Class<?>) ((GenericArrayType) t).getGenericComponentType();
      returnClass = Array.newInstance(type, 0).getClass();
    } else if (t instanceof ParameterizedType) {
      returnClass = (Class<?>) ((ParameterizedType) t).getRawType();
    } else if (t instanceof WildcardType) {
      returnClass = (Class<?>) ((WildcardType) t).getUpperBounds()[0];
    } else
      throw new RuntimeException("Generic TypeVariable not supported!");

    // // TODO remove this check
    // log.info("********* remove the check in ClassUtil by the time");
    // Class<?> returnClass2 = getClass(t);
    // if (returnClass == null && returnClass2 != null)
    // throw new IllegalArgumentException("result not equal");
    // if (returnClass2 == null && returnClass != null)
    // throw new IllegalArgumentException("result not equal");
    // if (returnClass != null && returnClass2 != null && !returnClass.equals(returnClass2))
    // throw new IllegalArgumentException("result not equal");

    return returnClass;
  }

  public static Type getParameterizedType(final Field field, final int index) {
    if (field != null) {
      if (field.getGenericType() instanceof ParameterizedType) {
        final ParameterizedType type = (ParameterizedType) field.getGenericType();
        if ((type.getActualTypeArguments() != null) && (type.getActualTypeArguments().length <= index)) {
          return null;
        }
        final Type paramType = type.getActualTypeArguments()[index];
        if (paramType instanceof GenericArrayType) {
          return paramType; // ((GenericArrayType) paramType).getGenericComponentType();
        } else {
          if (paramType instanceof ParameterizedType) {
            return paramType;
          } else {
            if (paramType instanceof TypeVariable) {
              // TODO: Figure out what to do... Walk back up the to
              // the parent class and try to get the variable type
              // from the T/V/X
              // throw new MappingException("Generic Typed Class not supported: <" + ((TypeVariable)
              // paramType).getName() + "> = " + ((TypeVariable) paramType).getBounds()[0]);
              return paramType;
            } else if (paramType instanceof WildcardType) {
              return paramType;
            } else if (paramType instanceof Class) {
              return paramType;
            } else {
              throw new MappingException("Unknown type... pretty bad... call for help, wave your hands... yeah!");
            }
          }
        }
      }

      // Not defined on field, but may be on class or super class...
      return getParameterizedClass(field.getType());
    }

    return null;
  }

  public static Class<?> getParameterizedClass(final Class<?> c) {
    return getParameterizedClass(c, 0);
  }

  public static Class<?> getParameterizedClass(final Class<?> c, final int index) {
    final TypeVariable<?>[] typeVars = c.getTypeParameters();
    if (typeVars.length > 0) {
      final TypeVariable<?> typeVariable = typeVars[index];
      final Type[] bounds = typeVariable.getBounds();

      final Type type = bounds[0];
      if (type instanceof Class) {
        return (Class<?>) type; // broke for EnumSet, cause bounds contain
        // type instead of class
      } else {
        return null;
      }
    } else {
      final Type superclass = c.getGenericSuperclass();
      if (superclass instanceof ParameterizedType) {
        final Type[] actualTypeArguments = ((ParameterizedType) superclass).getActualTypeArguments();
        return actualTypeArguments.length > index ? (Class<?>) actualTypeArguments[index] : null;
      } else if (!Object.class.equals(superclass)) {
        return getParameterizedClass((Class<?>) superclass);
      } else {
        return null;
      }
    }
  }

}
