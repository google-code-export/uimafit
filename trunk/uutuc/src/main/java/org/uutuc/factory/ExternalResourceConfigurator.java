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

import static org.apache.uima.util.Level.FINEST;
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
	 */
	public static <T> void configure(UimaContext aContext, T object)
		throws ResourceInitializationException
	{
		try {
			Map<String, Object> paramIn = new HashMap<String, Object>();
			for (String name : aContext.getConfigParameterNames()) {
				paramIn.put(name, aContext.getConfigParameterValue(name));
			}
			analyzeComponent(aContext, object.getClass(), object.getClass(),
					object, null);
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	/**
	 * Get a map describing the external dependencies of the given component
	 * class.
	 * 
	 * @param <T> the component type.
	 * @param aContext UIMA context (used for logging).
	 * @param aClazz the component class.
	 * @return External resource dependencies.
	 * @throws ResourceInitializationException
	 */
	public static <T> Map<String, ExternalResourceDependency> analyze(
			UimaContext aContext, Class<?> aClazz)
		throws ResourceInitializationException
	{
		Map<String, ExternalResourceDependency> deps = 
			new HashMap<String, ExternalResourceDependency>();
		analyzeComponent(aContext, aClazz, aClazz, null, deps);
		return deps;
	}

	@SuppressWarnings("unchecked")
	private static <T> void analyzeComponent(
			UimaContext aContext, Class<?> aBaseClazz, Class<?> aClazz, T object,
			Map<String, ExternalResourceDependency> aDeps)
		throws ResourceInitializationException
	{
		try {
			// Configure super-classes first. This allows sub-classes to
			// override behaviour
			if (aClazz.getSuperclass() != null) {
				analyzeComponent(aContext, aBaseClazz, aClazz.getSuperclass(), object, aDeps);
			}

			for (Field field : aClazz.getDeclaredFields()) {
				if (!field.isAnnotationPresent(ExternalResource.class)) {
					continue;
				}
				
				ExternalResource cpa = field.getAnnotation(ExternalResource.class);
				// If no API is set, get it from the annotated field
				Class<? extends Resource> api = cpa.api();
				if (api == Resource.class) {
					api = (Class<? extends Resource>) field.getType();
				}
				
				// If no name is set, use the field classname as name
				String key = cpa.key();
				if (key.length() == 0) {
					key = field.getType().getName();
				}

				trace(aContext, "Found annotation on field " + field.getName()
								+ " of type " + field.getType());

				if (aDeps != null) {
					aDeps.put(key, createExternalResourceDependency(key, api, !cpa.mandatory()));
				}
				
				if (object != null) {
					// Obtain the resource
					Object value = aContext.getResourceObject(key);
					if (value instanceof ExternalResourceLocator) {
						value = ((ExternalResourceLocator) value).getResource();
					}
		
					// Sanity checks
					if (value == null && cpa.mandatory()) {
						throw new ResourceInitializationException(
								new IllegalStateException("Mandatory resource ["
										+ key + "] is not set on ["+aBaseClazz+"]"));
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
		}
		catch (ResourceInitializationException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	private static void trace(UimaContext aContext, String aMsg)
	{
		if (aContext != null) {
			aContext.getLogger().log(FINEST, aMsg);
		}
	}
}
