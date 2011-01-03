/*
 Copyright 2009-2010
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
package org.uimafit.component.initialize;

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDependency;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ExternalResourceDependency;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.descriptor.ExternalResourceLocator;

/**
 * Configurator class for {@link ExternalResource} annotations.
 *
 * @author Richard Eckart de Castilho
 */
public class ExternalResourceInitializer
{
	/**
	 * Configure a component from the given context.
	 *
	 * @param <T> the component type.
	 * @param context the UIMA context.
	 * @param object the component.
	 * @throws ResourceInitializationException if the external resource cannot
	 * 		be configured.
	 */
	public static <T> void initialize(UimaContext context, T object)
			throws ResourceInitializationException {
		try {
			configure(context, object.getClass(), object.getClass(),
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
	 * @param context the context containing the resource bindings.
	 * @param baseCls the class on which configuration started.
	 * @param cls the class currently being configured.
	 * @param object the object being configured.
	 * @param dependencies the dependencies.
	 * @throws ResourceInitializationException if required resources could not
	 * 		be bound.
	 */
	private static <T> void configure(UimaContext context, Class<?> baseCls, Class<?> cls,
			T object, Map<String, ExternalResourceDependency> dependencies)
			throws ResourceInitializationException {
		try {
			if (cls.getSuperclass() != null) {
				configure(context, baseCls, cls.getSuperclass(), object, dependencies);
			}

			for (Field field : cls.getDeclaredFields()) {
				if (!field.isAnnotationPresent(ExternalResource.class)) {
					continue;
				}

				// Obtain the resource
				Object value = context.getResourceObject(getKey(field));
				if (value instanceof ExternalResourceLocator) {
					value = ((ExternalResourceLocator) value).getResource();
				}

				// Sanity checks
				if (value == null && isMandatory(field)) {
					throw new ResourceInitializationException(
							new IllegalStateException("Mandatory resource ["
									+ getKey(field) + "] is not set on ["+baseCls+"]"));
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

	/**
	 * @param <T>
	 * @param cls
	 * @return
	 * @throws ResourceInitializationException
	 */
	public static <T> Map<String, ExternalResourceDependency> getResourceDeclarations(Class<?> cls)
			throws ResourceInitializationException {
		try {
			Map<String, ExternalResourceDependency> deps =
				new HashMap<String, ExternalResourceDependency>();
			getResourceDeclarations(cls, cls, deps);
			return deps;
		}
		catch (ResourceInitializationException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	private static <T> void getResourceDeclarations(Class<?> baseCls, Class<?> cls,
			Map<String, ExternalResourceDependency> dependencies)
			throws ResourceInitializationException {
		if (cls.getSuperclass() != null) {
			getResourceDeclarations(baseCls, cls.getSuperclass(), dependencies);
		}

		for (Field field : cls.getDeclaredFields()) {
			if (!field.isAnnotationPresent(ExternalResource.class)) {
				continue;
			}

			if (dependencies.containsKey(getKey(field))) {
				throw new ResourceInitializationException(new IllegalStateException(
						"Key ["+getKey(field)+"] may only be used on a single field."));
			}

			dependencies.put(getKey(field), createExternalResourceDependency(getKey(field),
					getApi(field), !isMandatory(field)));
		}
	}

	/**
	 * Determine if the field is mandatory.
	 *
	 * @param field the field to bind.
	 * @return whether the field is mandatory.
	 */
	private static boolean isMandatory(Field field) {
		return field.getAnnotation(ExternalResource.class).mandatory();
	}

	/**
	 * Get the binding key for the specified field. If no key is set, use the field class name as
	 * key.
	 *
	 * @param field the field to bind.
	 * @return the binding key.
	 */
	private static String getKey(Field field) {
		ExternalResource cpa = field.getAnnotation(ExternalResource.class);
		String key = cpa.key();
		if (key.length() == 0) {
			key = field.getType().getName();
		}
		return key;
	}

	/**
	 * Get the type of class/interface a resource has to implement to bind to the annotated field.
	 * If no API is set, get it from the annotated field type.
	 *
	 * @param field the field to bind.
	 * @return the API type.
	 */
	@SuppressWarnings("unchecked")
	private static Class<? extends Resource> getApi(Field field) {
		ExternalResource cpa = field.getAnnotation(ExternalResource.class);
		// If no API is set, get it from the annotated field
		Class<? extends Resource> api = cpa.api();
		if (api == Resource.class) {
			api = (Class<? extends Resource>) field.getType();
		}
		return api;
	}
}
