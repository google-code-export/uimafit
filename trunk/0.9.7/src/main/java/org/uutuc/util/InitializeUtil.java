/* 
 Copyright 2009 Regents of the University of Colorado.  
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
package org.uutuc.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.factory.ConfigurationParameterFactory;

/**
 * @author Philip Ogren
 */

public class InitializeUtil {
	public static void initialize(Object component, UimaContext context) throws ResourceInitializationException {
		initializeParameters(component, context);
	}

	public static void initializeParameters(Object component, UimaContext context) throws ResourceInitializationException {
		try {
		for (Field field : ReflectionUtil.getFields(component)) { //component.getClass().getDeclaredFields()) {
			if (ConfigurationParameterFactory.isConfigurationParameterField(field)) {
				
				Object defaultValue = ConfigurationParameterFactory.getDefaultValue(field);
				org.uutuc.descriptor.ConfigurationParameter annotation = field
						.getAnnotation(org.uutuc.descriptor.ConfigurationParameter.class);

				Object value;
				if(annotation.mandatory()) {
					value = getRequiredConfigParameterValue(context, annotation.name());
				} else {
					value = getDefaultingConfigParameterValue(context, annotation.name(), defaultValue);
				}
				setParameterValue(component, field, value);
			}
		}
		} catch(Exception e) {
			throw new ResourceInitializationException(e);
		}
		
	}

	
	
	
	
	private static void setParameterValue(Object component, Field field, Object value) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchMethodException, InvocationTargetException {
			
		
		if(value != null && !field.getType().isAssignableFrom(value.getClass())) {
			if(field.getType().isArray() && value.getClass().getComponentType().getName().equals(Boolean.class.getName())) {
				value = ArrayUtils.toPrimitive((Boolean[]) value);
			}
			else if(field.getType().isArray() && value.getClass().getComponentType().getName().equals(Integer.class.getName())) {
				value = ArrayUtils.toPrimitive((Integer[]) value);
			}
			else if(field.getType().isArray() && value.getClass().getComponentType().getName().equals(Float.class.getName())) {
				value = ArrayUtils.toPrimitive((Float[]) value);
			}
		}
		
		if((field.getModifiers() & Modifier.PUBLIC) > 0) {
			field.set(component, value);
		} else {
			Method method = getSetter(component.getClass(), field);
			method.invoke(component, value);
		}
		
	}
	
	private static Method getSetter(Class<?> clazz, Field field) throws SecurityException, NoSuchMethodException {
		String name = field.getName();
		name = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
		try {
			return clazz.getMethod(name, field.getType());
		} catch(Exception nsme) {
			String message = "setter method for the configuration parameter "+field.getName()+" in the class "+field.getDeclaringClass()+" does not exist or is not public.  Please verify that a method named "+name+ " exists for this configuration parameter.";
			throw new NoSuchMethodException(message);
		}

	}
	
	public static Object getDefaultingConfigParameterValue(UimaContext context, String paramName, Object defaultValue) {
		Object paramValue = context.getConfigParameterValue(paramName);
		if (paramValue == null) {
			paramValue = defaultValue;
		}
		else if (paramValue instanceof String) {
			String str = (String) paramValue;
			if (str.trim().equals("")) paramValue = defaultValue;
		}
		else if (paramValue instanceof String[]) {
			String[] strs = (String[]) paramValue;
			if (strs.length == 0) paramValue = defaultValue;
			if (strs.length == 1 && strs[0].trim().equals("")) paramValue = defaultValue;
		}
		return paramValue;
	}

	/**
	 * Get a configuration parameter value, raising an exception if it was not
	 * specified.
	 * 
	 * @param context
	 *            The UIMAContext where the parameter should be defined.
	 * @param paramName
	 *            The name of the parameter.
	 * @return The value of the named parameter.
	 * @throws ResourceInitializationException
	 */
	public static Object getRequiredConfigParameterValue(UimaContext context, String paramName)
			throws ResourceInitializationException {
		Object paramValue = context.getConfigParameterValue(paramName);
		if (paramValue == null) {
			String key = ResourceInitializationException.CONFIG_SETTING_ABSENT;
			throw new ResourceInitializationException(key, new Object[] { paramName });
		}
		else if (paramValue instanceof String) {
			String str = (String) paramValue;
			if (str.trim().equals("")) {
				String key = ResourceInitializationException.CONFIG_SETTING_ABSENT;
				throw new ResourceInitializationException(key, new Object[] { paramName });
			}
		}
		return paramValue;
	}

	private InitializeUtil() {}


}
