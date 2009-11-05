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

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.ExternalResourceDependency;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.FileResourceSpecifier;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.ExternalResourceDependency_impl;
import org.apache.uima.resource.impl.ExternalResourceDescription_impl;
import org.apache.uima.resource.impl.FileResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.apache.uima.resource.metadata.ExternalResourceBinding;
import org.apache.uima.resource.metadata.ResourceManagerConfiguration;
import org.apache.uima.resource.metadata.impl.ExternalResourceBinding_impl;
import org.apache.uima.resource.metadata.impl.ResourceManagerConfiguration_impl;
import org.apache.uima.util.InvalidXMLException;

/**
 * Helper methods for external resources.
 * 
 * @author Richard Eckart de Castilho
 */
public class ExternalResourceFactory
{
	/**
	 * Create a new external resource binding.
	 * 
	 * @param aResMgrCfg the resource manager to create the binding in.
	 * @param aBindTo what key to bind to.
	 * @param aRes the resource that should be bound.
	 */
	public static void bindExternalResource(
			ResourceManagerConfiguration aResMgrCfg, String aBindTo,
			ExternalResourceDescription aRes)
	{
		ExternalResourceBinding extResBind = createExternalResourceBinding(
				aBindTo, aRes);

		aResMgrCfg.addExternalResource(aRes);
		aResMgrCfg.addExternalResourceBinding(extResBind);
	}

	/**
	 * Create an external resource description.
	 * 
	 * @param aName the name of the resource (the key).
	 * @param aInterface the interface the resource should implement.
	 * @param params parameters passed to the resource when it is created.
	 * @return the description.
	 */
	public static ExternalResourceDescription createExternalResourceDescription(
			final String aName, Class<? extends Resource> aInterface, String...aParams)
	{
		if (aParams.length % 2 != 0) {
			throw new IllegalArgumentException("Parameter arguments have to " +
					"come in key/value pairs, but found odd number of " +
					"arguments ["+ aParams.length + "]");
		}

		int numberOfParameters = aParams.length / 2;
		Parameter[] params = new Parameter[numberOfParameters];
		for (int i = 0; i < numberOfParameters; i++) {
			params[i] = new Parameter_impl();
			params[i].setName(aParams[i * 2]);
			params[i].setValue(aParams[i * 2 + 1]);
		}

		CustomResourceSpecifier spec = new CustomResourceSpecifier_impl();
		spec.setResourceClassName(aInterface.getName());
		spec.setParameters(params);

		ExternalResourceDescription extRes = new ExternalResourceDescription_impl();
		extRes.setName(aName);
		extRes.setResourceSpecifier(spec);
		return extRes;
	}

	/**
	 * Create an external resource description.
	 * 
	 * @param aName the name of the resource (the key).
	 * @param aUrl a URL.
	 * @return the description.
	 */
	public static ExternalResourceDescription createExternalResourceDescription(
			final String aName, String aUrl)
	{
		ExternalResourceDescription extRes = new ExternalResourceDescription_impl();
		extRes.setName(aName);
		FileResourceSpecifier frs = new FileResourceSpecifier_impl();
		frs.setFileUrl(aUrl);
		extRes.setResourceSpecifier(frs);
		return extRes;
	}

	/**
	 * Create an external resource binding.
	 * 
	 * @param aName the key to bind to.
	 * @param aResource the resource to bind.
	 * @return the description.
	 */
	public static ExternalResourceBinding createExternalResourceBinding(
			final String aKey, final ExternalResourceDescription aResource)
	{
		ExternalResourceBinding extResBind = new ExternalResourceBinding_impl();
		extResBind.setResourceName(aResource.getName());
		extResBind.setKey(aKey);
		return extResBind;
	}
	
	public static ExternalResourceDependency createExternalResourceDependency(
			final String aKey, final Class<? extends Resource> aInterface,
			final boolean aOptional)
	{
		ExternalResourceDependency dep = new ExternalResourceDependency_impl();
		dep.setInterfaceName(aInterface.getName());
		dep.setKey(aKey);
		dep.setOptional(aOptional);
		return dep;
	}

	/**
	 * Scan the given resource specifier for external resource dependencies
	 * and whenever a dependency is encounter that has a key equal to the
	 * resource class name, the resource will be bound.
	 * 
	 * @param aDesc a description.
	 * @param aApi the resource interface.
	 * @param aRes the resource to bind.
	 * @throws InvalidXMLException
	 */
	public static void bindResource(ResourceSpecifier aDesc,
			Class<? extends Resource> aRes, String... aParams)
		throws InvalidXMLException
	{
		bindResource(aDesc, aRes, aRes, aParams);
	}

	/**
	 * Scan the given resource specifier for external resource dependencies
	 * and whenever a dependency is encounter that has a key equal to the
	 * API class name, the resource will be bound.
	 * 
	 * @param aDesc a description.
	 * @param aApi the resource interface.
	 * @param aRes the resource to bind.
	 * @throws InvalidXMLException
	 */
	public static void bindResource(ResourceSpecifier aDesc,
			Class<?> aApi, Class<? extends Resource> aRes,
			String... aParams)
		throws InvalidXMLException
	{
		bindResource(aDesc, aApi.getName(), aRes, aParams);
	}

	/**
	 * Scan the given resource specifier for external resource dependencies
	 * and whenever a dependency with the given key is encountered, the given
	 * resource is bound to it.
	 * 
	 * @param aDesc a description.
	 * @param aKey the key to bind to.
	 * @param aRes the resource to bind.
	 * @throws InvalidXMLException
	 */
	public static void bindResource(ResourceSpecifier aDesc, String aKey,
			Class<? extends Resource> aRes, String... aParams)
		throws InvalidXMLException
	{
		// Dispatch
		if (aDesc instanceof AnalysisEngineDescription) {
			bind((AnalysisEngineDescription) aDesc, aKey, aRes, aParams);
		}
	}

	/**
	 * Scan the given resource specifier for external resource dependencies
	 * and whenever a dependency with the given key is encountered, the given
	 * resource is bound to it.
	 * 
	 * @param aDesc a description.
	 * @param aKey the key to bind to.
	 * @param aRes the resource to bind.
	 * @throws InvalidXMLException
	 */
	private static void bind(AnalysisEngineDescription aDesc, String aKey,
			Class<? extends Resource> aRes, String... aParams)
		throws InvalidXMLException
	{
		// Recursively address delegates
		if (!aDesc.isPrimitive()) {
			for(Object delegate : aDesc.getDelegateAnalysisEngineSpecifiers().values()) {
				bindResource((ResourceSpecifier) delegate, aKey, aRes, aParams);
			}
		}

		// Bind if necessary
		for (ExternalResourceDependency dep : aDesc.getExternalResourceDependencies()) {
			if (aKey.equals(dep.getKey())) {
				ExternalResourceDescription extRes = createExternalResourceDescription(
						aKey, aRes, aParams);

				ResourceManagerConfiguration resMgrCfg = aDesc.getResourceManagerConfiguration();
				if (resMgrCfg == null) {
					resMgrCfg = new ResourceManagerConfiguration_impl();
					aDesc.setResourceManagerConfiguration(resMgrCfg);
				}
				bindExternalResource(resMgrCfg,
						aKey, extRes);
			}
		}
	}
}
