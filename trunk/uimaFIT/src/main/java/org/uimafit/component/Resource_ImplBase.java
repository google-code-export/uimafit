/*
 Copyright 2011
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
package org.uimafit.component;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.component.initialize.ConfigurationParameterInitializer;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.util.ExtendedLogger;

/**
 * Base class for external resources which initializes itself based on annotations.
 *
 * @author Richard Eckart de Castilho
 */
public abstract class Resource_ImplBase extends org.apache.uima.resource.Resource_ImplBase
		implements ExternalResourceAware {

	private ExtendedLogger logger;
	private String resourcePrefix;
	
	@Override
	public ExtendedLogger getLogger() {
		if (logger == null) {
			logger = new ExtendedLogger(getUimaContext());
		}
		return logger;
	}
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

		// Remember the prefix for resource keys. This has been set in 
		// ExternalResourceFactory.bindExternalResource()
		resourcePrefix = (String) ConfigurationParameterFactory.getParameterSettings(aSpecifier)
				.get(ExternalResourceFactory.PARAM_RESOURCE_PREFIX);
				
		ConfigurationParameterInitializer.initialize(this, aSpecifier);
		// We cannot call ExternalResourceInitializer.initialize() yet because the 
		// ResourceManager_impl has not added the resources to the context yet.
		
		return true;
	}
	
	public String getResourcePrefix() {
		return resourcePrefix;
	}
	
	public void afterResourcesInitialized() {
		// Per default nothing is done here.
	}
}
