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

import static org.uutuc.factory.ExternalResourceFactory.createExternalResourceDependency;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ExternalResourceDependency;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceInitializationException;
import org.uutuc.descriptor.ExternalResource;
import org.uutuc.descriptor.ExternalResourceLocator;

/**
 * Configurator class for {@link ExternalResource} annotations.
 *
 * @author Richard Eckart de Castilho
 */
public class ExternalResourceConfigurator
{
	/**
	 * Configure a component from the given context.
	 *
	 * @param <T> the component type.
	 * @param aContext the UIMA context.
	 * @param object the component.
	 * @throws ResourceInitializationException if the external resource cannot
	 * 		be configured.
	 */
	public static <T> void configure(UimaContext aContext, T object)
		throws ResourceInitializationException
	{
		try {
			configure(aContext, object.getClass(), object.getClass(),
					object, getResourceDeclarations(object.getClass()));
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * Helper method for recursively configuring super-classes.
	 *
	 * @param <T> the component type.
	 * @param aContext the context containing the resource bindings.
	 * @param aBaseClazz the class on which configuration started.
	 * @param aClazz the class currently being configured.
	 * @param object the object being configured.
	 * @param aDeps the dependencies.
	 * @throws ResourceInitializationException if required resources could not
	 * 		be bound.
	 */
	private static <T> void configure(
			UimaContext aContext, Class<?> aBaseClazz, Class<?> aClazz, T object,
			Map<String, ExternalResourceDependency> aDeps)
		throws ResourceInitializationException
	{
		try {
			if (aClazz.getSuperclass() != null) {
				configure(aContext, aBaseClazz, aClazz.getSuperclass(), object, aDeps);
			}

			for (Field field : aClazz.getDeclaredFields()) {
				if (!field.isAnnotationPresent(ExternalResource.class)) {
					continue;
				}

				// Obtain the resource
				Object value = aContext.getResourceObject(getKey(field));
				if (value instanceof ExternalResourceLocator) {
					value = ((ExternalResourceLocator) value).getResource();
				}

				// Sanity checks
				if (value == null && isMandatory(field)) {
					throw new ResourceInitializationException(
							new IllegalStateException("Mandatory resource ["
									+ getKey(field) + "] is not set on ["+aBaseClazz+"]"));
				}

				// Now record the setting and optionally apply it to the given
				// instance.
				if (value != null) {
					field.setAccessible(true);
					field.set(object, value);
					field.setAccessible(false);
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

	public static <T> Map<String, ExternalResourceDependency> getResourceDeclarations(
			Class<?> aClazz)
		throws ResourceInitializationException
	{
		try {
			Map<String, ExternalResourceDependency> deps =
				new HashMap<String, ExternalResourceDependency>();
			getResourceDeclarations(aClazz, aClazz, deps);
			return deps;
		}
		catch (ResourceInitializationException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	private static <T> void getResourceDeclarations(
			Class<?> aBaseClazz, Class<?> aClazz,
			Map<String, ExternalResourceDependency> aDeps)
		throws ResourceInitializationException
	{
		if (aClazz.getSuperclass() != null) {
			getResourceDeclarations(aBaseClazz, aClazz.getSuperclass(), aDeps);
		}

		for (Field field : aClazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(ExternalResource.class)) {
				continue;
			}

			if (aDeps.containsKey(getKey(field))) {
				throw new ResourceInitializationException(new IllegalStateException(
						"Key ["+getKey(field)+"] may only be used on a single field."));
			}

			aDeps.put(getKey(field), createExternalResourceDependency(getKey(field),
					getApi(field), !isMandatory(field)));
		}
	}

	/**
	 * Determine if the field is mandatory.
	 *
	 * @param field the field to bind.
	 * @return whether the field is mandatory.
	 */
	private static boolean isMandatory(Field field)
	{
		return field.getAnnotation(ExternalResource.class).mandatory();
	}

	/**
	 * Get the binding key for the specified field. If no key is set, use the
	 * field class name as key.
	 *
	 * @param field the field to bind.
	 * @return the binding key.
	 */
	private static String getKey(Field field)
	{
		ExternalResource cpa = field.getAnnotation(ExternalResource.class);
		String key = cpa.key();
		if (key.length() == 0) {
			key = field.getType().getName();
		}
		return key;
	}

	/**
	 * Get the type of class/interface a resource has to implement to bind to
	 * the annotated field. If no API is set, get it from the annotated field
	 * type.
	 *
	 * @param field the field to bind.
	 * @return the API type.
	 */
	@SuppressWarnings("unchecked")
	private static Class<? extends Resource> getApi(Field field)
	{
		ExternalResource cpa = field.getAnnotation(ExternalResource.class);
		// If no API is set, get it from the annotated field
		Class<? extends Resource> api = cpa.api();
		if (api == Resource.class) {
			api = (Class<? extends Resource>) field.getType();
		}
		return api;
	}
}
