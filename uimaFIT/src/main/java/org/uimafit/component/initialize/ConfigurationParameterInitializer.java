/*
 Copyright 2009-2010 Regents of the University of Colorado.
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
package org.uimafit.component.initialize;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.UimaContextAdmin;
import org.apache.uima.resource.ConfigurationManager;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.util.LocaleUtil;
import org.uimafit.util.ReflectionUtil;

/**
 * <p>Initialize an instance of a class with fields that are annotated as
 * {@link ConfigurationParameter}s from the parameter values given in a {@link UimaContext}.</p>
 *
 * @author Philip Ogren
 */

public class ConfigurationParameterInitializer {

	private static final Map<Class<?>, Converter<?>> converters = new HashMap<Class<?>, Converter<?>>();
	static {
		converters.put(Boolean.class, new BooleanConverter());
		converters.put(Float.class, new FloatConverter());
		converters.put(double.class, new DoubleConverter());
		converters.put(Integer.class, new IntegerConverter());
		converters.put(String.class, new StringConverter());
		converters.put(boolean.class, new BooleanConverter());
		converters.put(float.class, new FloatConverter());
		converters.put(double.class, new DoubleConverter());
		converters.put(int.class, new IntegerConverter());
		converters.put(Pattern.class, new PatternConverter());
		converters.put(Locale.class, new LocaleConverter());
	}

	/**
	 * Initialize a component from an {@link UimaContext} This code can be a little confusing
	 * because the configuration parameter annotations are used in two contexts: in describing the
	 * component and to initialize member variables from a {@link UimaContext}. Here we are
	 * performing the latter task. It is important to remember that the {@link UimaContext} passed
	 * in to this method may or may not have been derived using reflection of the annotations (i.e.
	 * using {@link ConfigurationParameterFactory} via e.g. a call to a AnalysisEngineFactory.create
	 * method). It is just as possible for the description of the component to come directly from an
	 * XML descriptor file. So, for example, just because a configuration parameter specifies a
	 * default value, this does not mean that the passed in context will have a value for that
	 * configuration parameter. It should be possible for a descriptor file to specify its own value
	 * or to not provide one at all. If the context does not have a configuration parameter, then
	 * the default value provided by the developer as specified by the defaultValue element of the
	 * {@link ConfigurationParameter} will be used. See comments in the code for additional details.
	 *
	 * @param component the component to initialize.
	 * @param context a UIMA context with configuration parameters.
	 * @throws ResourceInitializationException
	 */
	public static void initialize(Object component, UimaContext context)
			throws ResourceInitializationException {
		for (Field field : ReflectionUtil.getFields(component)) { // component.getClass().getDeclaredFields())
			// {
			if (ConfigurationParameterFactory.isConfigurationParameterField(field)) {
				org.uimafit.descriptor.ConfigurationParameter annotation = field
						.getAnnotation(org.uimafit.descriptor.ConfigurationParameter.class);

				Object parameterValue;
				String configurationParameterName = ConfigurationParameterFactory
						.getConfigurationParameterName(field);

				// Obtain either from the context - or - if the context does
				// not provide the parameter, check if there is a default
				// value. Note there are three possibilities:
				// 1) Parameter present and set
				// 2) Parameter present and set to null (null value)
				// 3) Parameter not present (also provided as null value by
				// UIMA)
				// Unfortunately we cannot make a difference between case 2
				// and 3 since UIMA
				// does not allow us to actually get a list of the
				// parameters set in the
				// context. We can only get a list of the declared
				// parameters. Thus we
				// have to rely on the null value.
				parameterValue = context.getConfigParameterValue(configurationParameterName);
				if (parameterValue == null) {
					parameterValue = ConfigurationParameterFactory.getDefaultValue(field);
				}

				// TODO does this check really belong here? It seems that
				// this check is already performed by UIMA
				if (annotation.mandatory()) {
					if (parameterValue == null) {
						String key = ResourceInitializationException.CONFIG_SETTING_ABSENT;
						throw new ResourceInitializationException(key,
								new Object[] { configurationParameterName });
					}
				}
				else {
					if (parameterValue == null) {
						continue;
					}
				}
				Object fieldValue = convertValue(field, parameterValue);
				try {
					setParameterValue(component, field, fieldValue);
				}
				catch (Exception e) {
					throw new ResourceInitializationException(e);
				}
			}
		}
	}

	/**
	 * Initialize a component from a map.
	 *
	 * @param component the component to initialize.
	 * @param map a UIMA context with configuration parameters.
	 * @throws ResourceInitializationException
	 * @see #initialize(Object, UimaContext)
	 */
	public static void initialize(Object component, Map<String, Object> map)
			throws ResourceInitializationException {
		UimaContextAdmin context = UIMAFramework.newUimaContext(UIMAFramework.getLogger(),
				UIMAFramework.newDefaultResourceManager(), UIMAFramework.newConfigurationManager());
		ConfigurationManager cfgMgr = context.getConfigurationManager();
		cfgMgr.setSession(context.getSession());
		for (Entry<String, Object> e : map.entrySet()) {
			cfgMgr.setConfigParameterValue(context.getQualifiedContextName() + e.getKey(),
					e.getValue());
		}
		initialize(component, context);
	}

	/**
	 * Initialize a component from a {@link CustomResourceSpecifier}.
	 *
	 * @param component the component to initialize.
	 * @param spec a resource specifier.
	 * @throws ResourceInitializationException
	 * @see #initialize(Object, UimaContext)
	 */
	public static void initialize(Object component, ResourceSpecifier spec)
			throws ResourceInitializationException {
		try {
			Object result = spec.getClass().getMethod("getParameters").invoke(spec);
			if (result == null) {
				initialize(component);
			}
			Parameter[] parameters;
			try {
				parameters = (Parameter[]) result;
			}
			catch (ClassCastException e) {
				throw new IllegalArgumentException(
						"The method getParameters of resource specifier of type ["
								+ spec.getClass().getName()
								+ "] does not return an Parameter array.", e);
			}
			initialize(component, parameters);
		}
		catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Resource specifier of type ["
					+ spec.getClass().getName() + "] does not provide a getParameters() method.");
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unable to call getParameters() method of " +
					"resource specifier of type [" + spec.getClass().getName() + "]", e);
		}
		catch (SecurityException e) {
			throw new IllegalArgumentException("Unable to call getParameters() method of " +
					"resource specifier of type [" + spec.getClass().getName() + "]", e);
		}
		catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to call getParameters() method of " +
					"resource specifier of type [" + spec.getClass().getName() + "]", e);
		}
		catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Unable to call getParameters() method of " +
					"resource specifier of type [" + spec.getClass().getName() + "]", e);
		}
	}

	/**
	 * Initialize a component from a {@link CustomResourceSpecifier}.
	 *
	 * @param component the component to initialize.
	 * @param parameters a list of parameters.
	 * @throws ResourceInitializationException
	 * @see #initialize(Object, UimaContext)
	 */
	public static void initialize(Object component, Parameter... parameters)
			throws ResourceInitializationException {
		Map<String, Object> params = new HashMap<String, Object>();
		for (Parameter p : parameters) {
			params.put(p.getName(), p.getValue());
		}
		initialize(component, params);
	}

	/**
	 * This method converts UIMA values to values that are appropriate for instantiating the
	 * annotated member variable. For example, if the "uima" value is a string array and the member
	 * variable is of type List<String>, then this method will return a list
	 *
	 * @param field
	 * @param uimaValue
	 * @return
	 */
	public static Object convertValue(Field field, Object uimaValue) {
		if (ConfigurationParameterFactory.isConfigurationParameterField(field)) {

			Object result;
			Class<?> fieldType = field.getType();
			Class<?> componentType = getComponentType(field);
			Converter<?> converter = getConverter(componentType);

			// arrays
			if (fieldType.isArray()) {
				Object[] uimaValues = (Object[]) uimaValue;
				result = Array.newInstance(componentType, uimaValues.length);
				for (int index = 0; index < uimaValues.length; ++index) {
					Array.set(result, index, converter.convert(uimaValues[index]));
				}
			}

			// collections
			else if (Collection.class.isAssignableFrom(fieldType)) {
				Collection<Object> collection;
				if (fieldType == List.class) {
					collection = new ArrayList<Object>();
				}
				else if (fieldType == Set.class) {
					collection = new HashSet<Object>();
				}
				else {
					collection = newCollection(fieldType);
				}
				Object[] uimaValues = (Object[]) uimaValue;
				for (Object value : uimaValues) {
					collection.add(converter.convert(value));
				}
				result = collection;
			}

			// other
			else {
				result = converter.convert(uimaValue);
			}
			return result;
		}
		else {
			throw new IllegalArgumentException("field is not annotated with annotation of type "
					+ org.uimafit.descriptor.ConfigurationParameter.class.getName());
		}
	}

	@SuppressWarnings("unchecked")
	private static Collection<Object> newCollection(Class<?> cls) {
		try {
			return cls.asSubclass(Collection.class).newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Class<?> getComponentType(Field field) {
		Class<?> fieldType = field.getType();
		if (fieldType.isArray()) {
			return fieldType.getComponentType();
		}
		else if (Collection.class.isAssignableFrom(fieldType)) {
			ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
			return (Class<?>) collectionType.getActualTypeArguments()[0];
		}
		else {
			return fieldType;
		}
	}

	private static void setParameterValue(Object component, Field field, Object value)
			throws IllegalArgumentException, IllegalAccessException, SecurityException {

		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(component, value);
		}
		finally {
			field.setAccessible(accessible);
		}
	}

	private ConfigurationParameterInitializer() {
		// should not be instantiated
	}

	private static Converter<?> getConverter(Class<?> cls) {
		Converter<?> converter = converters.get(cls);
		if (converter != null) {
			return converter;
		}

		// Check if we have an enumeration type
		if (Enum.class.isAssignableFrom(cls)) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			EnumConverter tmp = new EnumConverter(cls);
			return tmp;
		}

		try {
			Constructor<?> constructor = cls.getConstructor(String.class);
			return new ConstructorConverter(constructor);
		}
		catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("don't know how to convert type " + cls);
		}
	}

	private static interface Converter<T> {
		public T convert(Object o);
	}

	private static class BooleanConverter implements Converter<Boolean> {
		public Boolean convert(Object o) {
			return (Boolean) o;
		}
	}

	private static class FloatConverter implements Converter<Float> {
		public Float convert(Object o) {
			return (Float) o;
		}
	}

	private static class DoubleConverter implements Converter<Float> {
		public Float convert(Object o) {
			return ((Number) o).floatValue();
		}
	}

	private static class IntegerConverter implements Converter<Integer> {
		public Integer convert(Object o) {
			return (Integer) o;
		}
	}

	private static class StringConverter implements Converter<String> {
		public String convert(Object o) {
			return o.toString();
		}
	}

	private static class PatternConverter implements Converter<Pattern> {
		public Pattern convert(Object o) {
			return Pattern.compile(o.toString());
		}
	}

	private static class ConstructorConverter implements Converter<Object> {
		private Constructor<?> constructor;

		public ConstructorConverter(Constructor<?> constructor) {
			this.constructor = constructor;
		}

		public Object convert(Object o) {
			try {
				return this.constructor.newInstance(o);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	private static class EnumConverter<T extends Enum<T>> implements Converter<Object> {
		private Class<T> enumClass;

		public EnumConverter(Class<T> aClass) {
			this.enumClass = aClass;
		}

		public T convert(Object o) {
			try {
				return Enum.valueOf(enumClass, o.toString());
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class LocaleConverter implements Converter<Locale> {
		public Locale convert(Object o) {
			if (o == null) {
				return Locale.getDefault();
			}
			else if (o.equals("")) {
				return Locale.getDefault();
			}
			if (o instanceof String) {
				return LocaleUtil.getLocale((String) o);
			}
			throw new RuntimeException("the value for a locale should be either null or an "
					+ "empty string to get the default locale.  Otherwise, the locale should be "
					+ "specified by a single string that names a locale constant (e.g. 'US') or "
					+ "that contains hyphen delimited locale information (e.g. 'en-US').");
		}
	}

}
