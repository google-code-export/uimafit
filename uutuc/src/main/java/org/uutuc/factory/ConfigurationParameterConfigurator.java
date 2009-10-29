/*
 Copyright 2009
 Ubiquitous Knowledge Processing (UKP) Lab
 Technische Universitaet Darmstadt
 All rights reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package org.uutuc.factory;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.impl.ConfigurationParameter_impl;
import org.uutuc.descriptor.ConfigurationParameter;
import org.uutuc.factory.converters.BooleanConverter;
import org.uutuc.factory.converters.Converter;
import org.uutuc.factory.converters.FloatConverter;
import org.uutuc.factory.converters.IntegerConverter;
import org.uutuc.factory.converters.MatcherConverter;
import org.uutuc.factory.converters.NullConverter;
import org.uutuc.factory.converters.StringConverter;

/**
 * Access or interpret parameter annotations on UIMA components. A component
 * can be configured (setting the actual fields) or a map containing the
 * complete component configuration can be obtained.
 *
 * @author Richard Eckart de Castilho
 */
public final class ConfigurationParameterConfigurator
{
	public static enum Cardinality { ZERO, ONE, ONE_OR_MORE, ZERO_OR_MORE }

	private static final Map<Class<?>, Class<?>> typeMapping;
//	private static final Map<Class<?>, Class<?>> uimaTypeMapping;
	private static final Map<Class<?>, Class<? extends Converter>> defaultConverters;

	static {
		// Legal field types and with which type of object to fill them
		typeMapping = new HashMap<Class<?>, Class<?>>();
		typeMapping.put(Boolean.class, Boolean.class);
		typeMapping.put(Boolean.TYPE, Boolean.class);
		typeMapping.put(Integer.class, Integer.class);
		typeMapping.put(Integer.TYPE, Integer.class);
		typeMapping.put(Float.class, Float.class);
		typeMapping.put(Float.TYPE, Float.class);

//
//		// How field types map to UIMA types.
//		uimaTypeMapping = new HashMap<Class<?>, Class<?>>();
//		uimaTypeMapping.put(Boolean.class, Boolean.class);
//		uimaTypeMapping.put(Integer.class, Integer.class);
//		uimaTypeMapping.put(Float.class, Float.class);
//		uimaTypeMapping.put(String.class, String.class);
//		uimaTypeMapping.put(Matcher.class, String.class);

		defaultConverters = new HashMap<Class<?>, Class<? extends Converter>>();
		defaultConverters.put(Boolean.class, BooleanConverter.class);
		defaultConverters.put(Boolean.TYPE, BooleanConverter.class);
		defaultConverters.put(Integer.class, IntegerConverter.class);
		defaultConverters.put(Integer.TYPE, IntegerConverter.class);
		defaultConverters.put(Float.class, FloatConverter.class);
		defaultConverters.put(Float.TYPE, FloatConverter.class);
		defaultConverters.put(String.class, StringConverter.class);
		defaultConverters.put(Matcher.class, MatcherConverter.class);
	}

	private ConfigurationParameterConfigurator()
	{
		// Library class
	}

	/**
	 * Configure a component from the given context.
	 *
	 * @param <T> the component type.
	 * @param aContext the UIMA context.
	 * @param object the component.
	 */
	public static <T> void configure(UimaContext aContext, T object)
		throws ResourceInitializationException
	{
		Map<String, Object> paramIn = new HashMap<String, Object>();
		for (String name : aContext.getConfigParameterNames()) {
			paramIn.put(name, aContext.getConfigParameterValue(name));
		}
		configure(object, paramIn);
	}

	/**
	 * Configure a component from the given map of parameters. The optional
	 * UIMA context is used for logging only.
	 *
	 * @param <T> the component type.
	 * @param aContext the UIMA context.
	 * @param object the component.
	 */
	public static <T> void configure(UimaContext aContext, T object,
			CustomResourceSpecifier aDesc)
		throws ResourceInitializationException
	{
		if (aDesc.getParameters() != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			for (Parameter p : aDesc.getParameters()) {
				params.put(p.getName(), p.getValue());
			}
			configure(object, params);
		}
	}

	/**
	 * Configure a component from the given map of parameters.
	 *
	 * @param <T> the component type.
	 * @param object the component.
	 * @param aParam the parameters.
	 */
	public static <T> void configure(T object,
			Map<String, Object> aParam)
		throws ResourceInitializationException
	{
		try {
			configure(object, object.getClass(), aParam);
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Recursively configure a component and its super-classes from the given
	 * map of parameters.
	 *
	 * @param <T> the component type.
	 * @param object the component.
	 * @param aClazz the class currently being configured.
	 * @param aParam the parameters.
	 * @throws ResourceInitializationException
	 */
	private static <T> void configure(
			T object, Class<?> aClazz, Map<String, Object> aParam)
		throws ResourceInitializationException
	{
		try {
			if (aClazz.getSuperclass() != null) {
				configure(object, aClazz.getSuperclass(), aParam);
			}

			for (Field field : aClazz.getDeclaredFields()) {
				if (!isConfigurationParameter(field)) {
					continue;
				}

				// Obtain the parameter value from the provided map
				Object[] value = null;
				if (aParam.get(getParameterName(field)) != null) {
					value = cook(field, toObjectArray(aParam.get(getParameterName(field))));
				}

				// If the caller did not provide the parameter, try to get a
				// default value
				if (value == null) {
					value = cook(field, getDefaultValue(field));
				}

				// Check for the presence of mandatory parameters.
				if (value == null && isMandatory(field)) {
					throw new ResourceInitializationException(
							new IllegalStateException("Mandatory parameter ["
									+ getParameterName(field)
									+ "] is not set on ["
									+ object.getClass().getName() + "]"));
				}

				// Check for an illegal assignment
				if (value != null) {
					if (!isMultiValue(field) && (value.length != 1)) {
						throw new IllegalArgumentException("Trying to assing " +
								"multi-value to single-value field ["
								+ field.getName() + "] on ["+aClazz+"]");
					}
				}

				// Now record the setting and optionally apply it to the given
				// instance.
				if (value != null) {
					applyValue(object, field, value);
				}
			}
		}
		catch (ResourceInitializationException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Get the parameter settings for the given component class based on its
	 * annotation and the provided parameters.
	 *
	 * @param <T> the component type.
	 * @param aClazz the component class.
	 * @param aParam the parameters.
	 * @return the complete parameters.
	 */
	public static <T> Map<String, Object> getDefaultValues(Class<?> aClazz)
		throws ResourceInitializationException
	{
		try {
			Map<String, Object> values = new HashMap<String, Object>();
			getDefaultValues(aClazz, aClazz, values);
			return values;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Recursively obtain the default values from the configuration parameter
	 * annotations.
	 *
	 * @param <T> the component type.
	 * @param aBaseClazz the component class.
	 * @param aClazz the class currently being analyzed.
	 * @param aValues the default values.
	 * @throws ResourceInitializationException
	 */
	private static <T> void getDefaultValues(Class<?> aBaseClazz, Class<?> aClazz,
			Map<String, Object> aValues)
		throws ResourceInitializationException
	{
		try {
			if (aClazz.getSuperclass() != null) {
				getDefaultValues(aBaseClazz, aClazz.getSuperclass(), aValues);
			}

			for (Field field : aClazz.getDeclaredFields()) {
				if (!isConfigurationParameter(field)) {
					continue;
				}

				Object[] value = getDefaultValue(field);

				if (aValues.containsKey(getParameterName(field))) {
					throw new ResourceInitializationException(
							new IllegalStateException("Parameter name ["
									+ getParameterName(field)
									+ "] may only be used on a single field."));
				}

				if (value != null) {
					if (isMultiValue(field)) {
						aValues.put(getParameterName(field), value);
					}
					else {
						aValues.put(getParameterName(field), value[0]);
					}
				}
			}
		}
		catch (ResourceInitializationException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Get the parameter settings for the given component class based on its
	 * annotation and the provided parameters.
	 *
	 * @param <T> the component type.
	 * @param aClazz the component class.
	 * @param aParam the parameters.
	 * @return the complete parameters.
	 */
	public static <T> Map<String, org.apache.uima.resource.metadata.ConfigurationParameter> getParameterDeclarations(Class<?> aClazz)
		throws ResourceInitializationException
	{
		try {
			Map<String, org.apache.uima.resource.metadata.ConfigurationParameter> param =
				new HashMap<String, org.apache.uima.resource.metadata.ConfigurationParameter>();
			getParameterDeclarations(aClazz, aClazz, param);
			return param;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	private static <T> void getParameterDeclarations(Class<?> aBaseClazz, Class<?> aClazz,
			Map<String, org.apache.uima.resource.metadata.ConfigurationParameter> aParam)
		throws ResourceInitializationException
	{
		try {
			if (aClazz.getSuperclass() != null) {
				getParameterDeclarations(aBaseClazz, aClazz.getSuperclass(), aParam);
			}

			for (Field field : aClazz.getDeclaredFields()) {
				if (!isConfigurationParameter(field)) {
					continue;
				}

				Converter converter = getConverter(field);

				if (converter == null) {
					throw new ResourceInitializationException(
							new IllegalStateException("No converter found for field ["+field.getName()+"]"));
				}

				org.apache.uima.resource.metadata.ConfigurationParameter param =
					new ConfigurationParameter_impl();
				param.setDescription(getDescripton(field));
				param.setMandatory(isMandatory(field));
				param.setMultiValued(isMultiValue(field));
				param.setName(getParameterName(field));
				param.setType(converter.getUimaType().getSimpleName());

				if (aParam.containsKey(getParameterName(field))) {
					throw new ResourceInitializationException(
							new IllegalStateException("Parameter name ["
									+ getParameterName(field)
									+ "] may only be used on a single field."));
				}

				aParam.put(getParameterName(field), param);
			}
		}
		catch (ResourceInitializationException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Check if the specified field bear a configuration parameter annotation.
	 *
	 * @param field the field.
	 * @return whether the field is a configuration parameter.
	 */
	private static boolean isConfigurationParameter(Field field)
	{
		 return /*field.isAnnotationPresent(ConfigurationParameter.class) || */
		 		field.isAnnotationPresent(org.uutuc.descriptor.ConfigurationParameter.class);
	}

	/**
	 * Determine if the field is mandatory.
	 *
	 * @param field the field.
	 * @return whether the field is mandatory.
	 */
	private static boolean isMandatory(Field field)
	{
//		if (field.isAnnotationPresent(ConfigurationParameter.class)) {
//			return field.getAnnotation(ConfigurationParameter.class).mandatory();
//		}
//		else
		if (field.isAnnotationPresent(org.uutuc.descriptor.ConfigurationParameter.class)) {
			return field.getAnnotation(org.uutuc.descriptor.ConfigurationParameter.class).mandatory();
		}
		else {
			throw new IllegalArgumentException("Field [" + field
					+ "] carries no ConfigurationParameter annotation");
		}
	}

	/**
	 * Get the parameter name for the specified field.
	 *
	 * @param field the field.
	 * @return the parameter name.
	 */
	private static String getParameterName(Field field)
	{
//		if (field.isAnnotationPresent(ConfigurationParameter.class)) {
//			return field.getAnnotation(ConfigurationParameter.class).name();
//		}
//		else
		if (field.isAnnotationPresent(org.uutuc.descriptor.ConfigurationParameter.class)) {
			return field.getAnnotation(org.uutuc.descriptor.ConfigurationParameter.class).name();
		}
		else {
			throw new IllegalArgumentException("Field [" + field
					+ "] carries no ConfigurationParameter annotation");
		}
	}

	/**
	 * Get the description for the specified field.
	 *
	 * @param field the field.
	 * @return the description.
	 */
	private static String getDescripton(Field field)
	{
//		if (field.isAnnotationPresent(ConfigurationParameter.class)) {
//			return field.getAnnotation(ConfigurationParameter.class).description();
//		}
//		else
		if (field.isAnnotationPresent(org.uutuc.descriptor.ConfigurationParameter.class)) {
			return field.getAnnotation(org.uutuc.descriptor.ConfigurationParameter.class).description();
		}
		else {
			throw new IllegalArgumentException("Field [" + field
					+ "] carries no ConfigurationParameter annotation");
		}
	}

	/**
	 * Get the type of object that has to be created to set the annotated field.
	 *
	 * @param field the field to set.
	 * @return the type required.
	 */
	private static Class<?> getType(Field field)
	{
		// For normal fields and arrays we can get the type from the field itself
		// For collection types we have to try the default value annotation. If
		// none is present, we have to fail...
		if (isCollection(field)) {
//			if (field.isAnnotationPresent(DefaultValueBoolean.class)) {
//				return Boolean.class;
//			}
//			else if (field.isAnnotationPresent(DefaultValueFloat.class)) {
//				return Float.class;
//			}
//			else if (field.isAnnotationPresent(DefaultValueInteger.class)) {
//				return Integer.class;
//			}
//			else if (field.isAnnotationPresent(DefaultValueString.class)) {
//				return String.class;
//			}
			return null;
//			throw new IllegalArgumentException("Collection field ["
//					+ field.getName() + "] in class ["
//					+ field.getDeclaringClass().getName() + "] needs a "
//					+ "default value from which the collection type can be "
//					+ "deduced.");
		}
		else {
			Class<?> type = field.getType();

			if (type.isArray()) {
				type = type.getComponentType();
			}

			if (typeMapping.get(type) != null) {
				return typeMapping.get(type);
			}
			else {
				return type;
			}
		}
	}

	private static Converter getConverter(Field field)
		throws ResourceInitializationException
	{
		try {
			Class<? extends Converter> clazz;

			if (field.isAnnotationPresent(ConfigurationParameter.class)) {
				clazz = field.getAnnotation(ConfigurationParameter.class).converter();
				if (clazz.equals(NullConverter.class)) {
					clazz = defaultConverters.get(getType(field));
				}
			}
			else {
				clazz = defaultConverters.get(getType(field));
			}

			if (clazz == null) {
				// ERROR
				return null;
			}

			return clazz.newInstance();
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Apply the given value to the given field of the given object.
	 *
	 * @param object the object.
	 * @param field the field.
	 * @param value the value.
	 */
	@SuppressWarnings("unchecked")
	private static void applyValue(Object object, Field field, Object[] value)
		throws InstantiationException, IllegalAccessException
	{
		field.setAccessible(true);
		if (isCollection(field)) {
			requireCardinality(value, Cardinality.ZERO_OR_MORE);
			Collection c = newCollection(field);
			c.addAll(asList(value));
			field.set(object, c);
		}
		else if (field.getType().isArray()) {
			requireCardinality(value, Cardinality.ZERO_OR_MORE);
			field.set(object, value);
		}
		else {
			requireCardinality(value, Cardinality.ONE);
			field.set(object, (value)[0]);
		}
		field.setAccessible(false);
	}

	/**
	 * Get default value for the specified field. The value is always returned
	 * as an array.
	 *
	 * @param field the field.
	 * @return the default value or null if no value is specified.
	 * @throws ResourceInitializationException
	 */
	private static Object[] getDefaultValue(Field field)
		throws ResourceInitializationException
	{
//		// Get the default value for DKPro-style annotations.
//		if (field.isAnnotationPresent(ConfigurationParameter.class)) {
//			Object raw[] = null;
//			// Convert String values
//			if (field.isAnnotationPresent(DefaultValueString.class)) {
//				raw = field.getAnnotation(DefaultValueString.class).value();
//			}
//
//			// Convert Boolean/boolean values
//			else if (field.isAnnotationPresent(DefaultValueBoolean.class)) {
//				raw = toObjectArray(field.getAnnotation(DefaultValueBoolean.class).value());
//			}
//
//			// Convert Integer/integer values
//			else if (field.isAnnotationPresent(DefaultValueInteger.class)) {
//				raw = toObjectArray(field.getAnnotation(DefaultValueInteger.class).value());
//			}
//
//			// Convert Float/float values
//			else if (field.isAnnotationPresent(DefaultValueFloat.class)) {
//				raw = toObjectArray(field.getAnnotation(DefaultValueFloat.class).value());
//			}
//
//			return raw;
//		}
//		// Get the default value for UUTUC-style annotations.
//		else
		if (field.isAnnotationPresent(org.uutuc.descriptor.ConfigurationParameter.class)) {
			org.uutuc.descriptor.ConfigurationParameter cpa =
					field.getAnnotation(org.uutuc.descriptor.ConfigurationParameter.class);
			String[] raw = cpa.defaultValue();

			// No default value
			if (raw.length == 1
					&& org.uutuc.descriptor.ConfigurationParameter.NO_DEFAULT_VALUE
							.equals(raw[0])) {
				return null;
			}

			Converter converter = getConverter(field);

			Class<?> type = converter.getResultType();

			// Convert Boolean/boolean values
			if (type.isAssignableFrom(Boolean.class)) {
				Boolean[] t = new Boolean[raw.length];
				for (int i = 0; i < t.length; i++) {
					t[i] = Boolean.parseBoolean(raw[i]);
				}
				return t;
			}

			// Convert Integer/integer values
			else if (type.isAssignableFrom(Integer.class)) {
				Integer[] t = new Integer[raw.length];
				for (int i = 0; i < t.length; i++) {
					t[i] = Integer.parseInt(raw[i]);
				}
				return t;
			}

			// Convert Float/float values
			else if (type.isAssignableFrom(Float.class)) {
				Float[] t = new Float[raw.length];
				for (int i = 0; i < t.length; i++) {
					t[i] = Float.parseFloat(raw[i]);
				}
				return t;
			}
			else {
				return raw;
			}
		}
		else {
			throw new IllegalArgumentException("Field [" + field
					+ "] carries no ConfigurationParameter annotation");
		}
	}

	private static Object[] cook(Field field, Object[] raw)
		throws ResourceInitializationException
	{
		Converter converter = getConverter(field);
		Object cooked[] = (Object[]) Array.newInstance(converter
				.getResultType(), raw.length);
		for (int i = 0; i < raw.length; i++) {
			cooked[i] = converter.convert(raw[i]);
		}
		return cooked;
	}

	/**
	 * Create a new collection compatible with the given field.
	 *
	 * @param field the field to fill.
	 * @return a compatible collection instance.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private static Collection newCollection(Field field)
		throws InstantiationException, IllegalAccessException
	{
		if (!field.getType().isInterface()) {
			return (Collection) field.getType().newInstance();
		}

		if (Set.class.isAssignableFrom(field.getType())) {
			return new HashSet();
		}

		if (List.class.isAssignableFrom(field.getType())) {
			return new ArrayList();
		}

		throw new IllegalArgumentException("Do not know how to create an " +
				"instance assignable to ["+field.getType()+"]");
	}

	private static boolean isMultiValue(Field field)
	{
		return isArray(field) || isCollection(field);
	}

	private static boolean isArray(Field field)
	{
		return field.getType().isArray();
	}

	private static boolean isCollection(Field field)
	{
		return Collection.class.isAssignableFrom(field.getType());
	}

	private static void requireCardinality(Object[] aObject, Cardinality aCardiniality)
	{
		boolean valid = false;
		switch (aCardiniality) {
		case ONE:
			valid = aObject.length == 1;
			break;
		case ZERO:
			valid = aObject.length == 0;
			break;
		case ONE_OR_MORE:
			valid = aObject.length >= 1;
			break;
		case ZERO_OR_MORE:
			valid = true;
			break;
		}

		if (!valid) {
			throw new IllegalArgumentException("Number of values ["
					+ aObject.length
					+ "] does not match required cardinality [" + aCardiniality
					+ "]");
		}
	}

	public static Object[] toObjectArray(Object aObject)
	{
		Class<?> type = aObject.getClass();
		if (type.isArray()) {
			if (type.getComponentType().isPrimitive()) {
				if (type.getComponentType() == boolean.class) {
					return toObjectArray((boolean[]) aObject);
				}
				if (type.getComponentType() == int.class) {
					return toObjectArray((int[]) aObject);
				}
				if (type.getComponentType() == float.class) {
					return toObjectArray((float[]) aObject);
				}
				else {
					throw new IllegalArgumentException(
							"Illegal primitive array type ["
									+ type.getComponentType() + "]");
				}
			}
			else {
				return (Object[]) aObject;
			}
		}
		else {
			return new Object[] { aObject };
		}

	}

	private static Boolean[] toObjectArray(boolean[] array)
	{
		Boolean[] result = new Boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}

	private static Integer[] toObjectArray(int[] array)
	{
		Integer[] result = new Integer[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}

	private static Float[] toObjectArray(float[] array)
	{
		Float[] result = new Float[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}
}
